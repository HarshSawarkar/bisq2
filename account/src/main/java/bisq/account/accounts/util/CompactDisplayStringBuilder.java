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

package bisq.account.accounts.util;

public class CompactDisplayStringBuilder {
    StringBuilder stringBuilder = new StringBuilder();

    public CompactDisplayStringBuilder(String... values) {
        for (int i = 0; i < values.length; i++) {
            String value = values[i];
            stringBuilder.append(value).append(": ");
            i++;
            value = values[i];
            stringBuilder.append(value);
            if (i < values.length - 1) {
                stringBuilder.append("\n");
            }
        }
    }

    public String toString() {
        // Remove potential trailing linebreak
        return stringBuilder.toString().replaceFirst("\\R?$", "");
    }

    public void add(String description, String value) {
        stringBuilder.append(description).append(": ")
                .append(value).append("\n");
    }
}
