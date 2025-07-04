package bisq.account.accounts;

import bisq.account.payment_method.FiatPaymentMethod;
import bisq.account.payment_method.FiatPaymentRail;
import bisq.account.protobuf.AccountPayload;
import bisq.common.validation.NetworkDataValidation;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@ToString
@EqualsAndHashCode(callSuper = true)
public final class PixAccountPayload extends CountryBasedAccountPayload {
    public static final int HOLDER_NAME_MIN_LENGTH = 2;
    public static final int HOLDER_NAME_MAX_LENGTH = 70;
    public static final int PIX_KEY_MIN_LENGTH = 2;
    public static final int PIX_KEY_MAX_LENGTH = 100;

    private final String pixKey;
    private final String holderName;

    public PixAccountPayload(String id, String countryCode, String pixKey, String holderName) {
        super(id, countryCode);
        this.pixKey = pixKey;
        this.holderName = holderName;
    }

    @Override
    public void verify() {
        super.verify();

        NetworkDataValidation.validateRequiredText(holderName, HOLDER_NAME_MIN_LENGTH, HOLDER_NAME_MAX_LENGTH);
        NetworkDataValidation.validateRequiredText(pixKey, PIX_KEY_MIN_LENGTH, PIX_KEY_MAX_LENGTH);
    }

    @Override
    protected bisq.account.protobuf.CountryBasedAccountPayload.Builder getCountryBasedAccountPayloadBuilder(boolean serializeForHash) {
        return super.getCountryBasedAccountPayloadBuilder(serializeForHash).setPixAccountPayload(
                toPixAccountPayloadProto(serializeForHash));
    }

    private bisq.account.protobuf.PixAccountPayload toPixAccountPayloadProto(boolean serializeForHash) {
        return resolveBuilder(getPixAccountPayloadBuilder(serializeForHash), serializeForHash).build();
    }

    private bisq.account.protobuf.PixAccountPayload.Builder getPixAccountPayloadBuilder(boolean serializeForHash) {
        return bisq.account.protobuf.PixAccountPayload.newBuilder()
                .setPixKey(pixKey)
                .setHolderName(holderName);
    }

    public static PixAccountPayload fromProto(AccountPayload proto) {
        bisq.account.protobuf.CountryBasedAccountPayload countryBasedAccountPayload = proto.getCountryBasedAccountPayload();
        bisq.account.protobuf.PixAccountPayload pixAccountPayload = countryBasedAccountPayload.getPixAccountPayload();
        return new PixAccountPayload(proto.getId(),
                countryBasedAccountPayload.getCountryCode(),
                pixAccountPayload.getPixKey(),
                pixAccountPayload.getHolderName()
        );
    }

    @Override
    public FiatPaymentMethod getPaymentMethod() {
        return FiatPaymentMethod.fromPaymentRail(FiatPaymentRail.PIX);
    }
}
