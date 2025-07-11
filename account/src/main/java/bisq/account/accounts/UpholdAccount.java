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

package bisq.account.accounts;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@ToString
@EqualsAndHashCode(callSuper = true)
public final class UpholdAccount extends CountryBasedAccount<UpholdAccountPayload> {
    public UpholdAccount(String id, long creationDate, String accountName, UpholdAccountPayload accountPayload) {
        super(id, creationDate, accountName, accountPayload);
    }

    @Override
    protected bisq.account.protobuf.CountryBasedAccount.Builder getCountryBasedAccountBuilder(boolean serializeForHash) {
        return super.getCountryBasedAccountBuilder(serializeForHash)
                .setUpholdAccount(toUpholdAccountProto(serializeForHash));
    }

    private bisq.account.protobuf.UpholdAccount toUpholdAccountProto(boolean serializeForHash) {
        return resolveBuilder(getUpholdAccountBuilder(serializeForHash), serializeForHash).build();
    }

    private bisq.account.protobuf.UpholdAccount.Builder getUpholdAccountBuilder(boolean serializeForHash) {
        return bisq.account.protobuf.UpholdAccount.newBuilder();
    }

    public static UpholdAccount fromProto(bisq.account.protobuf.Account proto) {
        return new UpholdAccount(proto.getId(),
                proto.getCreationDate(),
                proto.getAccountName(),
                UpholdAccountPayload.fromProto(proto.getAccountPayload())
        );
    }
}