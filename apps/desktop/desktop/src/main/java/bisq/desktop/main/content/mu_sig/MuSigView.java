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

package bisq.desktop.main.content.mu_sig;

import bisq.desktop.common.view.Controller;
import bisq.desktop.common.view.Model;
import bisq.desktop.common.view.View;
import bisq.desktop.main.content.ContentTabView;
import bisq.desktop.main.content.mu_sig.offerbook.MuSigOfferbookView;
import bisq.desktop.navigation.NavigationTarget;
import bisq.i18n.Res;
import javafx.scene.Parent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MuSigView extends ContentTabView<MuSigModel, MuSigController> {
    public MuSigView(MuSigModel model, MuSigController controller) {
        super(model, controller);

        addTab(Res.get("muSig.offerbook"), NavigationTarget.MU_SIG_OFFERBOOK);
        addTab(Res.get("muSig.myOffers"), NavigationTarget.MU_SIG_MY_OFFERS);
        addTab(Res.get("muSig.openTrades"), NavigationTarget.MU_SIG_OPEN_TRADES);
        addTab(Res.get("muSig.history"), NavigationTarget.MU_SIG_HISTORY);
    }

    @Override
    protected boolean useFitToHeight(View<? extends Parent, ? extends Model, ? extends Controller> childView) {
        return childView instanceof MuSigOfferbookView;
    }
}
