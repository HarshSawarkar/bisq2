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

package bisq.desktop.main.content.mu_sig.open_trades.trade_details;

import bisq.account.accounts.AccountPayload;
import bisq.account.payment_method.BitcoinPaymentRail;
import bisq.chat.mu_sig.open_trades.MuSigOpenTradeChannel;
import bisq.contract.mu_sig.MuSigContract;
import bisq.desktop.ServiceProvider;
import bisq.desktop.common.view.Controller;
import bisq.desktop.common.view.InitWithDataController;
import bisq.desktop.common.view.NavigationController;
import bisq.desktop.navigation.NavigationTarget;
import bisq.desktop.overlay.OverlayController;
import bisq.i18n.Res;
import bisq.offer.price.spec.FixPriceSpec;
import bisq.offer.price.spec.PriceSpecFormatter;
import bisq.presentation.formatters.DateFormatter;
import bisq.presentation.formatters.PriceFormatter;
import bisq.presentation.formatters.TimeFormatter;
import bisq.trade.mu_sig.MuSigTrade;
import bisq.trade.mu_sig.MuSigTradeFormatter;
import bisq.trade.mu_sig.MuSigTradeUtils;
import bisq.user.profile.UserProfile;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class MuSigTradeDetailsController extends NavigationController implements InitWithDataController<MuSigTradeDetailsController.InitData> {
    @Getter
    @EqualsAndHashCode
    @ToString
    public static class InitData {
        private final MuSigTrade trade;
        private final MuSigOpenTradeChannel channel;

        public InitData(MuSigTrade trade, MuSigOpenTradeChannel channel) {
            this.trade = trade;
            this.channel = channel;
        }
    }

    @Getter
    private final MuSigTradeDetailsModel model;
    @Getter
    private final MuSigTradeDetailsView view;


    public MuSigTradeDetailsController(ServiceProvider serviceProvider) {
        super(NavigationTarget.MU_SIG_TRADE_DETAILS);

        model = new MuSigTradeDetailsModel();
        view = new MuSigTradeDetailsView(model, this);
    }

    @Override
    public void initWithData(InitData initData) {
        model.setTrade(initData.trade);
        model.setChannel(initData.channel);
    }

    @Override
    public void onActivate() {
        MuSigTrade trade = model.getTrade();
        MuSigOpenTradeChannel channel = model.getChannel();
        MuSigContract contract = trade.getContract();

        model.setTradeDate(DateFormatter.formatDateTime(contract.getTakeOfferDate()));

        Optional<String> tradeDuration = trade.getTradeCompletedDate()
                .map(tradeCompletedDate -> tradeCompletedDate - contract.getTakeOfferDate())
                .map(TimeFormatter::formatAge);
        model.setTradeDuration(tradeDuration);

        model.setMe(String.format("%s (%s)", channel.getMyUserIdentity().getNickName(), MuSigTradeFormatter.getMakerTakerRole(trade).toLowerCase()));
        model.setPeer(channel.getPeer().getUserName());
        model.setOfferType(trade.getOffer().getDirection().isBuy()
                ? Res.get("bisqEasy.openTrades.tradeDetails.offerTypeAndMarket.buyOffer")
                : Res.get("bisqEasy.openTrades.tradeDetails.offerTypeAndMarket.sellOffer"));
        model.setMarket(Res.get("bisqEasy.openTrades.tradeDetails.offerTypeAndMarket.fiatMarket",
                trade.getOffer().getMarket().getQuoteCurrencyCode()));
        model.setFiatAmount(MuSigTradeFormatter.formatQuoteSideAmount(trade));
        model.setFiatCurrency(trade.getOffer().getMarket().getQuoteCurrencyCode());
        model.setBtcAmount(MuSigTradeFormatter.formatBaseSideAmount(trade));
        model.setPrice(PriceFormatter.format(MuSigTradeUtils.getPriceQuote(contract)));
        model.setPriceCodes(trade.getOffer().getMarket().getMarketCodes());
        model.setPriceSpec(trade.getOffer().getPriceSpec() instanceof FixPriceSpec
                ? ""
                : String.format("(%s)", PriceSpecFormatter.getFormattedPriceSpec(trade.getOffer().getPriceSpec(), true)));
        model.setPaymentMethod(contract.getQuoteSidePaymentMethodSpec().getShortDisplayString());
        model.setSettlementMethod(contract.getBaseSidePaymentMethodSpec().getShortDisplayString());
        model.setTradeId(trade.getId());
        model.setPeerNetworkAddress(channel.getPeer().getAddressByTransportDisplayString(50));

        //todo add also my account by account name
        Optional<AccountPayload<?>> peersAccountPayload = trade.getPeer().getAccountPayload();
        model.setPaymentAccountDataEmpty(peersAccountPayload.isEmpty());
        model.setAssignedMediator(channel.getMediator().map(UserProfile::getUserName).orElse(""));
        model.setHasMediatorBeenAssigned(channel.getMediator().isPresent());

        //todo toCompactDisplayString would require text area
        model.setPeersAccountPayloadDisplayString(peersAccountPayload.isEmpty()
                ? Res.get("bisqEasy.openTrades.tradeDetails.dataNotYetProvided")
                : peersAccountPayload.get().getAccountDataDisplayString());

        model.setDepositTxId(trade.getDepositTxId() == null
                ? Res.get("bisqEasy.openTrades.tradeDetails.dataNotYetProvided")
                : trade.getDepositTxId());
        model.setDepositTxIdEmpty(trade.getDepositTxId() == null);

        boolean isOnChainSettlement = contract.getBaseSidePaymentMethodSpec().getPaymentMethod().getPaymentRail() == BitcoinPaymentRail.MAIN_CHAIN;
        model.setOnChainSettlement(isOnChainSettlement);

        // At LN its optional, so we show it only if set
        model.setDepositTxIdVisible(isOnChainSettlement || trade.getDepositTxId() != null);
    }

    @Override
    public void onDeactivate() {
    }

    @Override
    protected Optional<? extends Controller> createController(NavigationTarget navigationTarget) {
        return Optional.empty();
    }

    void onClose() {
        OverlayController.hide();
    }
}
