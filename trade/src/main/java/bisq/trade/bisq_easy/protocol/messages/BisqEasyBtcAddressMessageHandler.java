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

package bisq.trade.bisq_easy.protocol.messages;

import bisq.account.payment_method.BitcoinPaymentRail;
import bisq.common.util.StringUtils;
import bisq.common.validation.BitcoinAddressValidation;
import bisq.common.validation.LightningInvoiceValidation;
import bisq.trade.ServiceProvider;
import bisq.trade.bisq_easy.BisqEasyTrade;
import bisq.trade.bisq_easy.handler.BisqEasyTradeMessageHandler;
import lombok.extern.slf4j.Slf4j;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Slf4j
public class BisqEasyBtcAddressMessageHandler extends BisqEasyTradeMessageHandler<BisqEasyTrade, BisqEasyBtcAddressMessage> {
    private String bitcoinPaymentData;

    public BisqEasyBtcAddressMessageHandler(ServiceProvider serviceProvider, BisqEasyTrade model) {
        super(serviceProvider, model);
    }

    @Override
    protected void verify(BisqEasyBtcAddressMessage message) {
        String bitcoinPaymentData = message.getBitcoinPaymentData();
        checkArgument(StringUtils.isNotEmpty(bitcoinPaymentData), "Bitcoin payment data must not be empty");

        boolean isMainChain = trade.getContract().getBaseSidePaymentMethodSpec().getPaymentMethod().getPaymentRail() == BitcoinPaymentRail.MAIN_CHAIN;
        if (isMainChain) {
            // We do not check for the min. length as we do not enforce the validation in the UI
            checkArgument(bitcoinPaymentData.length() <= BitcoinAddressValidation.MAX_LENGTH,
                    "Bitcoin address length must not be longer than " + BitcoinAddressValidation.MAX_LENGTH);
        } else {
            // We do not check for the min. length as we do not enforce the validation in the UI
            checkArgument(bitcoinPaymentData.length() <= LightningInvoiceValidation.MAX_LENGTH,
                    "Lightning invoice length must not be longer than " + LightningInvoiceValidation.MAX_LENGTH);
        }

        checkNotNull(message.getBisqEasyOffer(), "BisqEasyOffer must not be null");
    }

    @Override
    protected void process(BisqEasyBtcAddressMessage message) {
        bitcoinPaymentData = message.getBitcoinPaymentData();
    }

    @Override
    protected void commit() {
        trade.getBitcoinPaymentData().set(bitcoinPaymentData);
    }
}