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

package bisq.trade.mu_sig.messages.network;

import bisq.contract.ContractSignatureData;
import bisq.contract.mu_sig.MuSigContract;
import bisq.network.identity.NetworkId;
import bisq.trade.mu_sig.messages.network.mu_sig_data.NonceShares;
import bisq.trade.mu_sig.messages.network.mu_sig_data.PubKeyShares;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString(callSuper = true)
@Getter
@EqualsAndHashCode(callSuper = true)
public final class SetupTradeMessage_B extends MuSigTradeMessage {
    private final MuSigContract contract;
    private final ContractSignatureData contractSignatureData;
    private final PubKeyShares pubKeyShares;
    private final NonceShares nonceShares;

    public SetupTradeMessage_B(String id,
                               String tradeId,
                               String protocolVersion,
                               NetworkId sender,
                               NetworkId receiver,
                               MuSigContract contract,
                               ContractSignatureData contractSignatureData,
                               PubKeyShares pubKeyShares,
                               NonceShares nonceShares) {
        super(id, tradeId, protocolVersion, sender, receiver);
        this.contract = contract;
        this.contractSignatureData = contractSignatureData;
        this.pubKeyShares = pubKeyShares;
        this.nonceShares = nonceShares;

        verify();
    }

    @Override
    public void verify() {
        super.verify();
    }

    @Override
    protected bisq.trade.protobuf.MuSigTradeMessage.Builder getMuSigTradeMessageBuilder(boolean serializeForHash) {
        return bisq.trade.protobuf.MuSigTradeMessage.newBuilder()
                .setSetupTradeMessageB(toSetupTradeMessage_BProto(serializeForHash));
    }

    private bisq.trade.protobuf.SetupTradeMessage_B toSetupTradeMessage_BProto(boolean serializeForHash) {
        bisq.trade.protobuf.SetupTradeMessage_B.Builder builder = getSetupTradeMessage_B(serializeForHash);
        return resolveBuilder(builder, serializeForHash).build();
    }

    private bisq.trade.protobuf.SetupTradeMessage_B.Builder getSetupTradeMessage_B(boolean serializeForHash) {
        return bisq.trade.protobuf.SetupTradeMessage_B.newBuilder()
                .setContract(contract.toProto(serializeForHash))
                .setContractSignatureData(contractSignatureData.toProto(serializeForHash))
                .setPubKeyShares(pubKeyShares.toProto(serializeForHash))
                .setNonceShares(nonceShares.toProto(serializeForHash));
    }

    public static SetupTradeMessage_B fromProto(bisq.trade.protobuf.TradeMessage proto) {
        bisq.trade.protobuf.SetupTradeMessage_B muSigMessageProto = proto.getMuSigTradeMessage().getSetupTradeMessageB();
        return new SetupTradeMessage_B(
                proto.getId(),
                proto.getTradeId(),
                proto.getProtocolVersion(),
                NetworkId.fromProto(proto.getSender()),
                NetworkId.fromProto(proto.getReceiver()),
                MuSigContract.fromProto(muSigMessageProto.getContract()),
                ContractSignatureData.fromProto(muSigMessageProto.getContractSignatureData()),
                PubKeyShares.fromProto(muSigMessageProto.getPubKeyShares()),
                NonceShares.fromProto(muSigMessageProto.getNonceShares()));
    }

    @Override
    public double getCostFactor() {
        return getCostFactor(0.1, 0.3);
    }
}
