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

import bisq.network.identity.NetworkId;
import bisq.trade.mu_sig.messages.network.mu_sig_data.SwapTxSignature;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString(callSuper = true)
@Getter
@EqualsAndHashCode(callSuper = true)
public final class PaymentReceivedMessage_F extends MuSigTradeMessage {
    private final SwapTxSignature swapTxSignature;

    public PaymentReceivedMessage_F(String id,
                                    String tradeId,
                                    String protocolVersion,
                                    NetworkId sender,
                                    NetworkId receiver,
                                    SwapTxSignature swapTxSignature) {
        super(id, tradeId, protocolVersion, sender, receiver);
        this.swapTxSignature = swapTxSignature;

        verify();
    }

    @Override
    public void verify() {
        super.verify();
    }

    @Override
    protected bisq.trade.protobuf.MuSigTradeMessage.Builder getMuSigTradeMessageBuilder(boolean serializeForHash) {
        return bisq.trade.protobuf.MuSigTradeMessage.newBuilder()
                .setPaymentReceivedMessageF(toPaymentReceivedMessage_FProto(serializeForHash));
    }

    private bisq.trade.protobuf.PaymentReceivedMessage_F toPaymentReceivedMessage_FProto(boolean serializeForHash) {
        bisq.trade.protobuf.PaymentReceivedMessage_F.Builder builder = getPaymentReceivedMessage_F(serializeForHash);
        return resolveBuilder(builder, serializeForHash).build();
    }

    private bisq.trade.protobuf.PaymentReceivedMessage_F.Builder getPaymentReceivedMessage_F(boolean serializeForHash) {
        return bisq.trade.protobuf.PaymentReceivedMessage_F.newBuilder()
                .setSwapTxSignature(swapTxSignature.toProto(serializeForHash));
    }

    public static PaymentReceivedMessage_F fromProto(bisq.trade.protobuf.TradeMessage proto) {
        bisq.trade.protobuf.PaymentReceivedMessage_F muSigMessageProto = proto.getMuSigTradeMessage().getPaymentReceivedMessageF();
        return new PaymentReceivedMessage_F(
                proto.getId(),
                proto.getTradeId(),
                proto.getProtocolVersion(),
                NetworkId.fromProto(proto.getSender()),
                NetworkId.fromProto(proto.getReceiver()),
                SwapTxSignature.fromProto(muSigMessageProto.getSwapTxSignature()));
    }

    @Override
    public double getCostFactor() {
        return getCostFactor(0.1, 0.3);
    }
}
