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

package bisq.trade.mu_sig.messages.grpc;

import bisq.common.proto.Proto;
import bisq.trade.mu_sig.messages.network.mu_sig_data.NonceShares;
import com.google.protobuf.ByteString;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
public final class NonceSharesMessage implements Proto {
    public static NonceSharesMessage from(NonceShares nonceShares) {
        return new NonceSharesMessage(
                nonceShares.getWarningTxFeeBumpAddress(),
                nonceShares.getRedirectTxFeeBumpAddress(),
                nonceShares.getHalfDepositPsbt(),
                nonceShares.getSwapTxInputNonceShare(),
                nonceShares.getBuyersWarningTxBuyerInputNonceShare(),
                nonceShares.getBuyersWarningTxSellerInputNonceShare(),
                nonceShares.getSellersWarningTxBuyerInputNonceShare(),
                nonceShares.getSellersWarningTxSellerInputNonceShare(),
                nonceShares.getBuyersRedirectTxInputNonceShare(),
                nonceShares.getSellersRedirectTxInputNonceShare()
        );
    }

    private final String warningTxFeeBumpAddress;
    private final String redirectTxFeeBumpAddress;
    private final byte[] halfDepositPsbt;
    private final byte[] swapTxInputNonceShare;
    private final byte[] buyersWarningTxBuyerInputNonceShare;
    private final byte[] buyersWarningTxSellerInputNonceShare;
    private final byte[] sellersWarningTxBuyerInputNonceShare;
    private final byte[] sellersWarningTxSellerInputNonceShare;
    private final byte[] buyersRedirectTxInputNonceShare;
    private final byte[] sellersRedirectTxInputNonceShare;

    private NonceSharesMessage(String warningTxFeeBumpAddress,
                               String redirectTxFeeBumpAddress,
                               byte[] halfDepositPsbt,
                               byte[] swapTxInputNonceShare,
                               byte[] buyersWarningTxBuyerInputNonceShare,
                               byte[] buyersWarningTxSellerInputNonceShare,
                               byte[] sellersWarningTxBuyerInputNonceShare,
                               byte[] sellersWarningTxSellerInputNonceShare,
                               byte[] buyersRedirectTxInputNonceShare,
                               byte[] sellersRedirectTxInputNonceShare) {
        this.warningTxFeeBumpAddress = warningTxFeeBumpAddress;
        this.redirectTxFeeBumpAddress = redirectTxFeeBumpAddress;
        this.halfDepositPsbt = halfDepositPsbt;
        this.swapTxInputNonceShare = swapTxInputNonceShare;
        this.buyersWarningTxBuyerInputNonceShare = buyersWarningTxBuyerInputNonceShare;
        this.buyersWarningTxSellerInputNonceShare = buyersWarningTxSellerInputNonceShare;
        this.sellersWarningTxBuyerInputNonceShare = sellersWarningTxBuyerInputNonceShare;
        this.sellersWarningTxSellerInputNonceShare = sellersWarningTxSellerInputNonceShare;
        this.buyersRedirectTxInputNonceShare = buyersRedirectTxInputNonceShare;
        this.sellersRedirectTxInputNonceShare = sellersRedirectTxInputNonceShare;
    }

    @Override
    public bisq.trade.protobuf.NonceSharesMessage.Builder getBuilder(boolean serializeForHash) {
        return bisq.trade.protobuf.NonceSharesMessage.newBuilder()
                .setWarningTxFeeBumpAddress(warningTxFeeBumpAddress)
                .setRedirectTxFeeBumpAddress(redirectTxFeeBumpAddress)
                .setHalfDepositPsbt(ByteString.copyFrom(halfDepositPsbt))
                .setSwapTxInputNonceShare(ByteString.copyFrom(swapTxInputNonceShare))
                .setBuyersWarningTxBuyerInputNonceShare(ByteString.copyFrom(buyersWarningTxBuyerInputNonceShare))
                .setBuyersWarningTxSellerInputNonceShare(ByteString.copyFrom(buyersWarningTxSellerInputNonceShare))
                .setSellersWarningTxBuyerInputNonceShare(ByteString.copyFrom(sellersWarningTxBuyerInputNonceShare))
                .setSellersWarningTxSellerInputNonceShare(ByteString.copyFrom(sellersWarningTxSellerInputNonceShare))
                .setBuyersRedirectTxInputNonceShare(ByteString.copyFrom(buyersRedirectTxInputNonceShare))
                .setSellersRedirectTxInputNonceShare(ByteString.copyFrom(sellersRedirectTxInputNonceShare));
    }

    @Override
    public bisq.trade.protobuf.NonceSharesMessage toProto(boolean serializeForHash) {
        return getBuilder(serializeForHash).build();
    }

    public static NonceSharesMessage fromProto(bisq.trade.protobuf.NonceSharesMessage proto) {
        return new NonceSharesMessage(proto.getWarningTxFeeBumpAddress(),
                proto.getRedirectTxFeeBumpAddress(),
                proto.getHalfDepositPsbt().toByteArray(),
                proto.getSwapTxInputNonceShare().toByteArray(),
                proto.getBuyersWarningTxBuyerInputNonceShare().toByteArray(),
                proto.getBuyersWarningTxSellerInputNonceShare().toByteArray(),
                proto.getSellersWarningTxBuyerInputNonceShare().toByteArray(),
                proto.getSellersWarningTxSellerInputNonceShare().toByteArray(),
                proto.getBuyersRedirectTxInputNonceShare().toByteArray(),
                proto.getSellersRedirectTxInputNonceShare().toByteArray());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof NonceSharesMessage that)) return false;

        return Objects.equals(warningTxFeeBumpAddress, that.warningTxFeeBumpAddress) &&
                Objects.equals(redirectTxFeeBumpAddress, that.redirectTxFeeBumpAddress) &&
                Arrays.equals(halfDepositPsbt, that.halfDepositPsbt) &&
                Arrays.equals(swapTxInputNonceShare, that.swapTxInputNonceShare) &&
                Arrays.equals(buyersWarningTxBuyerInputNonceShare, that.buyersWarningTxBuyerInputNonceShare) &&
                Arrays.equals(buyersWarningTxSellerInputNonceShare, that.buyersWarningTxSellerInputNonceShare) &&
                Arrays.equals(sellersWarningTxBuyerInputNonceShare, that.sellersWarningTxBuyerInputNonceShare) &&
                Arrays.equals(sellersWarningTxSellerInputNonceShare, that.sellersWarningTxSellerInputNonceShare) &&
                Arrays.equals(buyersRedirectTxInputNonceShare, that.buyersRedirectTxInputNonceShare) &&
                Arrays.equals(sellersRedirectTxInputNonceShare, that.sellersRedirectTxInputNonceShare);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(warningTxFeeBumpAddress);
        result = 31 * result + Objects.hashCode(redirectTxFeeBumpAddress);
        result = 31 * result + Arrays.hashCode(halfDepositPsbt);
        result = 31 * result + Arrays.hashCode(swapTxInputNonceShare);
        result = 31 * result + Arrays.hashCode(buyersWarningTxBuyerInputNonceShare);
        result = 31 * result + Arrays.hashCode(buyersWarningTxSellerInputNonceShare);
        result = 31 * result + Arrays.hashCode(sellersWarningTxBuyerInputNonceShare);
        result = 31 * result + Arrays.hashCode(sellersWarningTxSellerInputNonceShare);
        result = 31 * result + Arrays.hashCode(buyersRedirectTxInputNonceShare);
        result = 31 * result + Arrays.hashCode(sellersRedirectTxInputNonceShare);
        return result;
    }
}
