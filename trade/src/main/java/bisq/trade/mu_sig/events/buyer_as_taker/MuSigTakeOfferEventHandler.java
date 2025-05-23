/*
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package bisq.trade.mu_sig.events.buyer_as_taker;

import bisq.common.util.StringUtils;
import bisq.contract.ContractSignatureData;
import bisq.contract.mu_sig.MuSigContract;
import bisq.i18n.Res;
import bisq.offer.mu_sig.MuSigOffer;
import bisq.trade.ServiceProvider;
import bisq.trade.mu_sig.MuSigTrade;
import bisq.trade.mu_sig.MuSigTradeParty;
import bisq.trade.mu_sig.handler.MuSigTradeEventHandlerAsMessageSender;
import bisq.trade.mu_sig.messages.grpc.PubKeySharesResponse;
import bisq.trade.mu_sig.messages.network.MuSigSetupTradeMessage_A;
import bisq.trade.mu_sig.messages.network.mu_sig_data.PubKeyShares;
import bisq.trade.mu_sig.protocol.MuSigProtocolException;
import bisq.trade.protobuf.PubKeySharesRequest;
import bisq.trade.protobuf.Role;
import bisq.user.profile.UserProfile;
import lombok.extern.slf4j.Slf4j;

import java.security.GeneralSecurityException;
import java.util.Optional;

@Slf4j
public final class MuSigTakeOfferEventHandler extends MuSigTradeEventHandlerAsMessageSender<MuSigTrade, MuSigTakeOfferEvent> {
    private PubKeySharesResponse myPubKeySharesResponse;
    private ContractSignatureData myContractSignatureData;
    private MuSigContract contract;

    public MuSigTakeOfferEventHandler(ServiceProvider serviceProvider, MuSigTrade model) {
        super(serviceProvider, model);
    }

    @Override
    public void process(MuSigTakeOfferEvent event) {
        try {
            bisq.trade.protobuf.PubKeySharesResponse proto = musigBlockingStub.initTrade(PubKeySharesRequest.newBuilder()
                    .setTradeId(trade.getId())
                    .setMyRole(Role.BUYER_AS_TAKER)
                    .build());
            myPubKeySharesResponse = PubKeySharesResponse.fromProto(proto);

            contract = trade.getContract();
            myContractSignatureData = serviceProvider.getContractService().signContract(contract,
                    trade.getMyIdentity().getKeyBundle().getKeyPair());
        } catch (GeneralSecurityException e) {
            log.error("Signing contract failed", e);
            throw new MuSigProtocolException(e);
        }
    }

    @Override
    protected void commit() {
        MuSigTradeParty mySelf = trade.getMyself();
        mySelf.getContractSignatureData().set(myContractSignatureData);
        mySelf.setMyPubKeySharesResponse(myPubKeySharesResponse);
    }

    @Override
    protected void sendMessage() {
        PubKeyShares pubKeyShares = PubKeyShares.from(myPubKeySharesResponse);
        send(new MuSigSetupTradeMessage_A(StringUtils.createUid(),
                trade.getId(),
                trade.getProtocolVersion(),
                trade.getMyIdentity().getNetworkId(),
                trade.getPeer().getNetworkId(),
                contract,
                myContractSignatureData,
                pubKeyShares));
    }

    @Override
    protected void sendLogMessage() {
        String takerId = trade.getTaker().getNetworkId().getId();
        MuSigOffer offer = trade.getOffer();
        Optional<UserProfile> takerUserProfile = serviceProvider.getUserService().getUserProfileService().findUserProfile(takerId);
        String makerId = offer.getMakersUserProfileId();
        Optional<UserProfile> makerUserProfile = serviceProvider.getUserService().getUserProfileService().findUserProfile(makerId);
        sendLogMessage(Res.encode("muSig.protocol.logMessage.takeOffer",
                takerUserProfile.orElseThrow().getUserName(),
                makerUserProfile.orElseThrow().getUserName(),
                offer.getShortId()));

        sendLogMessage("Buyer created his pubKeyShares.\n" +
                "Buyer sent his pubKeyShares to seller.");
    }
}
