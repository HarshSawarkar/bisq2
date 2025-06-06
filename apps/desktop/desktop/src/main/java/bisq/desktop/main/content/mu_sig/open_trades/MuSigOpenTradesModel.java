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

package bisq.desktop.main.content.mu_sig.open_trades;

import bisq.chat.ChatChannelDomain;
import bisq.desktop.main.content.chat.ChatModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public final class MuSigOpenTradesModel extends ChatModel {
    private final BooleanProperty noOpenTrades = new SimpleBooleanProperty();
    private final BooleanProperty tradeWelcomeVisible = new SimpleBooleanProperty();
    private final BooleanProperty tradeRulesAccepted = new SimpleBooleanProperty();
    private final BooleanProperty chatVisible = new SimpleBooleanProperty();
    private final BooleanProperty tradeStateVisible = new SimpleBooleanProperty();
    private final BooleanProperty isAnyTradeInMediation = new SimpleBooleanProperty();
    private final StringProperty chatWindowTitle = new SimpleStringProperty();
    private final ObjectProperty<Stage> chatWindow = new SimpleObjectProperty<>();
    private final ObjectProperty<MuSigOpenTradeListItem> selectedItem = new SimpleObjectProperty<>();
    private final ObservableList<MuSigOpenTradeListItem> listItems = FXCollections.observableArrayList();
    private final FilteredList<MuSigOpenTradeListItem> filteredList = new FilteredList<>(listItems);
    private final SortedList<MuSigOpenTradeListItem> sortedList = new SortedList<>(filteredList);

    public MuSigOpenTradesModel(ChatChannelDomain chatChannelDomain) {
        super(chatChannelDomain);
    }

    void reset() {
        noOpenTrades.set(false);
        tradeWelcomeVisible.set(false);
        tradeRulesAccepted.set(false);
        chatVisible.set(false);
        tradeStateVisible.set(false);
        isAnyTradeInMediation.set(false);
        if (chatWindow.get() != null) {
            chatWindow.get().close();
            chatWindow.set(null);
        }
        chatWindowTitle.set(null);
        selectedItem.set(null);
        listItems.forEach(MuSigOpenTradeListItem::dispose);
        listItems.clear();
    }
}
