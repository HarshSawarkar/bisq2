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

package bisq.desktop.main.content.mu_sig.open_trades.trade_state.states;

import bisq.account.accounts.AccountPayload;
import bisq.chat.mu_sig.open_trades.MuSigOpenTradeChannel;
import bisq.desktop.ServiceProvider;
import bisq.desktop.common.utils.ClipboardUtil;
import bisq.desktop.components.controls.MaterialTextArea;
import bisq.desktop.components.controls.MaterialTextField;
import bisq.desktop.components.controls.WrappingText;
import bisq.desktop.components.controls.validator.SettableErrorValidator;
import bisq.desktop.components.overlay.Popup;
import bisq.i18n.Res;
import bisq.mu_sig.MuSigService;
import bisq.support.moderator.ModerationRequestService;
import bisq.trade.mu_sig.MuSigTrade;
import bisq.user.profile.UserProfile;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class State2BuyerSendPayment extends BaseState {
    private final Controller controller;

    public State2BuyerSendPayment(ServiceProvider serviceProvider,
                                  MuSigTrade trade,
                                  MuSigOpenTradeChannel channel) {
        controller = new Controller(serviceProvider, trade, channel);
    }

    public VBox getRoot() {
        return controller.getView().getRoot();
    }

    private static class Controller extends BaseState.Controller<Model, View> {
        private final MuSigService muSigService;
        private final ModerationRequestService moderationRequestService;

        private Controller(ServiceProvider serviceProvider,
                           MuSigTrade trade,
                           MuSigOpenTradeChannel channel) {
            super(serviceProvider, trade, channel);

            muSigService = serviceProvider.getMuSigService();
            moderationRequestService = serviceProvider.getSupportService().getModerationRequestService();
        }

        @Override
        protected Model createModel(MuSigTrade trade, MuSigOpenTradeChannel channel) {
            return new Model(trade, channel);
        }

        @Override
        protected View createView() {
            return new View(model, this);
        }

        @Override
        public void onActivate() {
            super.onActivate();

            MuSigTrade trade = model.getTrade();
            model.setPaymentMethodName(trade.getContract().getQuoteSidePaymentMethodSpec().getShortDisplayString());
            AccountPayload<?> peersAccountPayload = trade.getBuyer().getAccountPayload().orElseThrow();
            if (muSigService.isAccountDataBanned(peersAccountPayload)) {
                model.getConfirmFiatSentButtonDisabled().set(true);
                model.getAccountDataBannedValidator().setIsInvalid(true);

                UserProfile peer = model.getChannel().getPeer();
                String peerUserName = peer.getUserName();

                // Report to moderator
                String message = "Account data of " + peerUserName + " is banned: " + peersAccountPayload;
                moderationRequestService.reportUserProfile(peer, message);

                new Popup().warning(Res.get("bisqEasy.tradeState.info.buyer.phase2a.accountDataBanned.popup.warning")).show();
            } else {
                model.getConfirmFiatSentButtonDisabled().set(false);
                model.getAccountDataBannedValidator().setIsInvalid(false);
            }
        }

        @Override
        public void onDeactivate() {
            super.onDeactivate();
        }

        private void onConfirmFiatSent() {
            sendTradeLogMessage(Res.encode("bisqEasy.tradeState.info.buyer.phase2a.tradeLogMessage",
                    model.getChannel().getMyUserIdentity().getUserName(), model.getQuoteCode()));
            muSigTradeService.paymentInitiated(model.getTrade());
        }
    }

    @Getter
    private static class Model extends BaseState.Model {
        private final BooleanProperty confirmFiatSentButtonDisabled = new SimpleBooleanProperty();
        private final SettableErrorValidator accountDataBannedValidator = new SettableErrorValidator(Res.get("bisqEasy.tradeState.info.buyer.phase2a.accountDataBannedError"));
        @Setter
        private String paymentMethodName;
        protected Model(MuSigTrade trade, MuSigOpenTradeChannel channel) {
            super(trade, channel);
        }
    }

    public static class View extends BaseState.View<Model, Controller> {
        private final Button confirmFiatSentButton;
        private final MaterialTextArea account;
        private final MaterialTextField quoteAmount, paymentReason;
        private final WrappingText headline;

        private View(Model model, Controller controller) {
            super(model, controller);

            headline = MuSigFormUtils.getHeadline();

            quoteAmount = MuSigFormUtils.getTextField(Res.get("bisqEasy.tradeState.info.buyer.phase2a.quoteAmount"), "", false);
            paymentReason = MuSigFormUtils.getTextField(Res.get("bisqEasy.tradeState.info.buyer.phase2a.paymentReason"), "", false);
            account = MuSigFormUtils.addTextArea(Res.get("bisqEasy.tradeState.info.buyer.phase2a.sellersAccount"), "", false);
            account.setValidator(model.getAccountDataBannedValidator());

            confirmFiatSentButton = new Button();
            confirmFiatSentButton.setDefaultButton(true);
            VBox.setMargin(confirmFiatSentButton, new Insets(0, 0, 5, 0));

            root.getChildren().addAll(
                    headline,
                    quoteAmount,
                    paymentReason,
                    account,
                    confirmFiatSentButton);
        }

        @Override
        protected void onViewAttached() {
            super.onViewAttached();

            headline.setText(Res.get("muSig.tradeState.info.buyer.phase2a.headline", model.getFormattedQuoteAmount(), model.getPaymentMethodName()));
            quoteAmount.setText(model.getFormattedQuoteAmount());
            quoteAmount.getIconButton().setOnAction(e -> ClipboardUtil.copyToClipboard(model.getQuoteAmount()));
            paymentReason.setText(model.getTrade().getShortId());
            paymentReason.getIconButton().setOnAction(e -> ClipboardUtil.copyToClipboard(model.getTrade().getShortId()));
            account.setText(model.getTrade().getPeer().getAccountPayload().orElseThrow().getAccountDataDisplayString());
            account.validate();
            confirmFiatSentButton.setText(Res.get("bisqEasy.tradeState.info.buyer.phase2a.confirmFiatSent", model.getFormattedQuoteAmount()));
            confirmFiatSentButton.setOnAction(e -> controller.onConfirmFiatSent());
            confirmFiatSentButton.disableProperty().bind(model.getConfirmFiatSentButtonDisabled());
        }

        @Override
        protected void onViewDetached() {
            super.onViewDetached();

            confirmFiatSentButton.disableProperty().unbind();
            confirmFiatSentButton.setOnAction(null);
            quoteAmount.getIconButton().setOnAction(null);
            paymentReason.getIconButton().setOnAction(null);
        }
    }
}
