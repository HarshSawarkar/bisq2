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

package bisq.desktop.main.content.bisq_easy.trade_guide.process;

import bisq.desktop.common.view.View;
import bisq.desktop.components.controls.BisqHyperlink;
import bisq.desktop.components.controls.OrderedList;
import bisq.i18n.Res;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BisqEasyGuideProcessView extends View<VBox, BisqEasyGuideProcessModel, BisqEasyGuideProcessController> {
    private final Button backButton, nextButton;
    private final Hyperlink learnMore;

    public BisqEasyGuideProcessView(BisqEasyGuideProcessModel model, BisqEasyGuideProcessController controller) {
        super(new VBox(), model, controller);

        root.setSpacing(20);
        root.setAlignment(Pos.TOP_LEFT);

        Label headline = new Label(Res.get("bisqEasy.tradeGuide.process.headline"));
        headline.getStyleClass().add("bisq-easy-trade-guide-headline");

        Label content = new Label(Res.get("bisqEasy.tradeGuide.process.content"));
        content.setWrapText(true);
        content.getStyleClass().add("bisq-easy-trade-guide-content");
        OrderedList rules = new OrderedList(Res.get("bisqEasy.tradeGuide.process.steps"), "bisq-easy-trade-guide-content");
        rules.setSpacing(15);

        backButton = new Button(Res.get("action.back"));

        nextButton = new Button(Res.get("action.next"));
        nextButton.setDefaultButton(true);

        HBox buttons = new HBox(20, backButton, nextButton);

        learnMore = new BisqHyperlink(Res.get("action.learnMore"), "https://bisq.wiki/Bisq_Easy");

        VBox.setMargin(headline, new Insets(10, 0, -5, 0));
        VBox.setMargin(learnMore, new Insets(0, 0, 10, 0));
        root.getChildren().addAll(headline, content, rules, learnMore, buttons);
    }

    @Override
    protected void onViewAttached() {
        backButton.setOnAction(e -> controller.onBack());
        nextButton.setOnAction(e -> controller.onNext());
        learnMore.setOnAction(e -> controller.onLearnMore());
    }

    @Override
    protected void onViewDetached() {
        backButton.setOnAction(null);
        nextButton.setOnAction(null);
        learnMore.setOnAction(null);
    }
}
