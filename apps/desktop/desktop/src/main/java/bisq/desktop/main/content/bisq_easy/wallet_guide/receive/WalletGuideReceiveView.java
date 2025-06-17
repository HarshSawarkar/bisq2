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

package bisq.desktop.main.content.bisq_easy.wallet_guide.receive;

import bisq.desktop.common.utils.ImageUtil;
import bisq.desktop.common.view.View;
import bisq.desktop.components.containers.Carousel;
import bisq.desktop.components.controls.BisqHyperlink;
import bisq.i18n.Res;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WalletGuideReceiveView extends View<HBox, WalletGuideReceiveModel, WalletGuideReceiveController> {
    private final Button backButton, closeButton;
    private final Hyperlink link1, link2;
    private final Carousel imageCarousel;

    public WalletGuideReceiveView(WalletGuideReceiveModel model, WalletGuideReceiveController controller) {
        super(new HBox(20), model, controller);

        VBox vBox = new VBox(20);
        vBox.setFillWidth(true);
        vBox.setAlignment(Pos.TOP_LEFT);

        Label headline = new Label(Res.get("bisqEasy.walletGuide.receive.headline"));
        headline.getStyleClass().add("bisq-easy-trade-guide-headline");

        Text text = new Text(Res.get("bisqEasy.walletGuide.receive.content"));
        text.getStyleClass().add("bisq-easy-trade-guide-content");
        TextFlow content = new TextFlow(text);

        link1 = new BisqHyperlink(Res.get("bisqEasy.walletGuide.receive.link1"), "https://www.youtube.com/watch?v=NqY3wBhloH4");
        link2 = new BisqHyperlink(Res.get("bisqEasy.walletGuide.receive.link2"), "https://www.youtube.com/watch?v=imMX7i4qpmg");
        backButton = new Button(Res.get("action.back"));
        closeButton = new Button(Res.get("action.close"));
        closeButton.setDefaultButton(true);

        HBox buttons = new HBox(20, backButton, closeButton);

        VBox.setMargin(headline, new Insets(0, 0, -5, 0));
        VBox.setMargin(content, new Insets(0, 0, 5, 0));
        VBox.setMargin(link1, new Insets(-10, 0, -22.5, 0));
        VBox.setMargin(link2, new Insets(0, 0, 0, 0));
        vBox.getChildren().addAll(headline, content, link1, link2, buttons);

        ImageView image1 = ImageUtil.getImageViewById("blue-wallet-tx");
        ImageView image2 = ImageUtil.getImageViewById("blue-wallet-qr");

        configureImage(image1, 250, 430);
        configureImage(image2, 250, 430);

        imageCarousel = new Carousel();
        imageCarousel.addItem(image1);
        imageCarousel.addItem(image2);

        root.getChildren().addAll(vBox,
                new VBox(10, imageCarousel) {{
                    setAlignment(Pos.CENTER);
                    getStyleClass().add("carousel-container");
                }});
        HBox.setHgrow(vBox, Priority.ALWAYS);
        root.setAlignment(Pos.TOP_LEFT);
    }

    @Override
    protected void onViewAttached() {
        closeButton.setOnAction(e -> controller.onClose());
        backButton.setOnAction(e -> controller.onBack());
        link1.setOnAction(e -> controller.onOpenLink1());
        link2.setOnAction(e -> controller.onOpenLink2());

        imageCarousel.initialize();
        imageCarousel.start();
    }

    @Override
    protected void onViewDetached() {
        closeButton.setOnAction(null);
        backButton.setOnAction(null);
        link1.setOnAction(null);
        link2.setOnAction(null);

        imageCarousel.dispose();
    }

    private void configureImage(ImageView image, double width, double height) {
        image.setFitWidth(width);
        image.setFitHeight(height);
        image.setPreserveRatio(true);
    }
}