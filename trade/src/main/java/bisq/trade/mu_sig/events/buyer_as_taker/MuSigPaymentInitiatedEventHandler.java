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
import bisq.trade.ServiceProvider;
import bisq.trade.mu_sig.MuSigTrade;
import bisq.trade.mu_sig.handler.MuSigTradeEventHandlerAsMessageSender;
import bisq.trade.mu_sig.messages.network.MuSigPaymentInitiatedMessage_E;
import bisq.trade.mu_sig.messages.network.mu_sig_data.PartialSignatures;

public final class MuSigPaymentInitiatedEventHandler extends MuSigTradeEventHandlerAsMessageSender<MuSigTrade, MuSigPaymentInitiatedEvent> {
    public MuSigPaymentInitiatedEventHandler(ServiceProvider serviceProvider, MuSigTrade model) {
        super(serviceProvider, model);
    }

    @Override
    public void process(MuSigPaymentInitiatedEvent event) {
        tradeService.startCloseTimeout(trade, new MuSigBuyersCloseTimeoutEvent());
    }

    @Override
    protected void commit() {
    }

    @Override
    protected void sendMessage() {
        PartialSignatures partialSignatures = PartialSignatures.from(trade.getMyself().getMyPartialSignaturesMessage().orElseThrow());
        byte[] swapTxInputPartialSignature = partialSignatures.getSwapTxInputPartialSignature();
        send(new MuSigPaymentInitiatedMessage_E(StringUtils.createUid(),
                trade.getId(),
                trade.getProtocolVersion(),
                trade.getMyIdentity().getNetworkId(),
                trade.getPeer().getNetworkId(),
                swapTxInputPartialSignature));
    }

    @Override
    protected void sendLogMessage() {
    }
}
