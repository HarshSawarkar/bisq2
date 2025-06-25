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

package bisq.account.payment_method;

import bisq.account.protocol_type.TradeProtocolType;
import bisq.common.currency.FiatCurrencyRepository;
import bisq.common.currency.TradeCurrency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FiatPaymentRailUtil {

    public static List<FiatPaymentRail> getPaymentRails() {
        return List.of(FiatPaymentRail.values());
    }

    public static List<FiatPaymentRail> getPaymentRails(TradeProtocolType protocolType) {
        return switch (protocolType) {
            case BISQ_EASY, MU_SIG, BISQ_LIGHTNING -> getPaymentRails();
            case MONERO_SWAP, LIQUID_SWAP, BSQ_SWAP ->
                    throw new UnsupportedOperationException("No paymentMethods for that protocolType");
            default -> throw new RuntimeException("Not handled case: protocolType=" + protocolType);
        };
    }

    public static List<FiatPaymentRail> getPaymentRails(String currencyCode) {
        return getPaymentRails().stream()
                .filter(fiatPaymentRail -> {
                    if (currencyCode.equals("EUR") &&
                            (fiatPaymentRail == FiatPaymentRail.NATIONAL_BANK)) {
                        // For EUR, we don't add NATIONAL_BANK as SEPA is the common payment rail for EUR
                        // SWIFT is added to support non-EUR countries offering EUR accounts like Switzerland
                        return false;
                    }
                    // We add NATIONAL_BANK to all others
                    if (fiatPaymentRail == FiatPaymentRail.NATIONAL_BANK) {
                        return true;
                    }
                    return fiatPaymentRail.supportsCurrency(currencyCode);
                })
                .collect(Collectors.toList());
    }

    static public List<String> getAllSepaCountries() {
        List<String> sepaEuroCountries = getSepaEuroCountries();
        List<String> sepaNonEuroCountries = getSepaNonEuroCountries();
        List<String> sepaCountries = new ArrayList<>(sepaEuroCountries);
        sepaCountries.addAll(sepaNonEuroCountries);
        Collections.sort(sepaCountries);
        return sepaCountries;
    }

    static public List<String> getSepaEuroCountries() {
        return List.of("AT", "BE", "CY", "DE", "EE", "FI", "FR", "GR", "IE",
                "IT", "LV", "LT", "LU", "MC", "MT", "NL", "PT", "SK", "SI", "ES", "AD", "SM", "VA");
    }

    static public List<String> getSepaNonEuroCountries() {
        return List.of("BG", "HR", "CZ", "DK", "GB", "HU", "PL", "RO",
                "SE", "IS", "NO", "LI", "CH", "JE", "GI");
    }

    static List<TradeCurrency> toTradeCurrencies(List<String> currencyCodes) {
        return currencyCodes.stream()
                .map(FiatCurrencyRepository::getCurrencyByCode)
                .distinct()
                .sorted(Comparator.comparingInt(TradeCurrency::hashCode))
                .collect(Collectors.toList());
    }

    // https://wise.com/help/articles/2571907/what-currencies-can-i-send-to-and-from?origin=related-article-2571942
    // https://github.com/bisq-network/proposals/issues/243
    static List<String> getWiseCountries() {
        List<String> list = new ArrayList<>(List.of("AR", "AU", "BD", "BR", "BG", "CA", "CL", "CN", "CO", "CR", "CZ", "DK", "EG",
                "GE", "GH", "HK", "HU", "IN", "ID", "IL", "JP", "KE", "MY", "MX", "MA", "NP", "NZ", "NO",
                "PK", "PH", "PL", "RO", "SG", "ZA", "KR", "LK", "SE", "CH", "TZ", "TH", "TR", "UG", "UA", "AE",
                "GB", "US", "UY", "VN", "ZM"));
        list.addAll(getSepaEuroCountries());
        return list;
    }

    // Took all currencies from: https://wise.com/help/articles/2571907/what-currencies-can-i-send-to-and-from
    static List<String> getWiseCurrencies() {
        return List.of(
                "AED",
                "ARS",
                "AUD",
                "BDT",
                "BGN",
                "BRL",
                "BWP",
                "CAD",
                "CHF",
                "CLP",
                "CNY",
                "COP",
                "CRC",
                "CZK",
                "DKK",
                "EGP",
                "EUR",
                "FJD",
                "GEL",
                "GHS",
                "GBP",
                "HKD",
                "HUF",
                "IDR",
                "ILS",
                "INR",
                "JPY",
                "KES",
                "KRW",
                "LKR",
                "MAD",
                "MXN",
                "MYR",
                "NOK",
                "NPR",
                "NZD",
                "PHP",
                "PKR",
                "PLN",
                "RON",
                "SEK",
                "SGD",
                "THB",
                "TRY",
                "UAH",
                "UGX",
                "USD",
                "UYU",
                "VND",
                "ZAR",
                "ZMW"
        );
    }

    // https://help.revolut.com/help/wealth/exchanging-money/what-currencies-are-available/what-currencies-are-supported-for-holding-and-exchange/
    static List<String> getRevolutCountries() {
        return List.of("AT", "BE", "BG", "HR", "CY", "CZ", "DK", "EE", "FI", "FR",
                "DE", "GR", "HU", "IS", "IE", "IT", "LV", "LI", "LT", "LU", "MT", "NL",
                "NO", "PL", "PT", "RO", "SK", "SI", "ES", "SE", "GB",
                "AU", "CA", "SG", "CH", "US");
    }

    static List<String> getRevolutCurrencies() {
        return List.of(
                "AED",
                "AUD",
                "BGN",
                "CAD",
                "CHF",
                "CZK",
                "DKK",
                "EUR",
                "GBP",
                "HKD",
                "HUF",
                "ILS",
                "ISK",
                "JPY",
                "MAD",
                "MXN",
                "NOK",
                "NZD",
                "PLN",
                "QAR",
                "RON",
                "RSD",
                "RUB",
                "SAR",
                "SEK",
                "SGD",
                "THB",
                "TRY",
                "USD",
                "ZAR"
        );
    }

    // Popularity scores based on historical snapshot of Bisq1 offers count
    // Higher scores indicate more commonly used payment methods
    public static Map<FiatPaymentRail, Integer> getPopularityScore() {
        return Map.ofEntries(
                Map.entry(FiatPaymentRail.SEPA, 10),
                Map.entry(FiatPaymentRail.ZELLE, 9),
                Map.entry(FiatPaymentRail.PIX, 8),
                Map.entry(FiatPaymentRail.NATIONAL_BANK, 7),
                Map.entry(FiatPaymentRail.REVOLUT, 6),
                Map.entry(FiatPaymentRail.CASH_BY_MAIL, 6),
                Map.entry(FiatPaymentRail.ACH_TRANSFER, 5),
                Map.entry(FiatPaymentRail.STRIKE, 5),
                Map.entry(FiatPaymentRail.INTERAC_E_TRANSFER, 4),
                Map.entry(FiatPaymentRail.WISE, 4),
                Map.entry(FiatPaymentRail.F2F, 3),
                Map.entry(FiatPaymentRail.US_POSTAL_MONEY_ORDER, 3),
                Map.entry(FiatPaymentRail.PAY_ID, 3),
                Map.entry(FiatPaymentRail.FASTER_PAYMENTS, 3),
                Map.entry(FiatPaymentRail.AMAZON_GIFT_CARD, 2),
                Map.entry(FiatPaymentRail.SWIFT, 2),
                Map.entry(FiatPaymentRail.BIZUM, 2),
                Map.entry(FiatPaymentRail.CASH_DEPOSIT, 2),
                Map.entry(FiatPaymentRail.UPI, 1),
                Map.entry(FiatPaymentRail.CASH_APP, 1)
        );
    }
}