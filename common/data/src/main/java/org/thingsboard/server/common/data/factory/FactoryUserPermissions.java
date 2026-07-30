/**
 * Copyright © 2016-2026 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.common.data.factory;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.security.Authority;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Data
@Schema
public class FactoryUserPermissions implements Serializable {

    private static final long serialVersionUID = 1L;

    private TenantId tenantId;
    private CustomerId customerId;
    private UserId userId;
    private Authority authority;
    private List<FactoryRole> roles = new ArrayList<>();
    private Set<String> permissions = new LinkedHashSet<>();
    private List<FactoryRoleScope> scopes = new ArrayList<>();

    public boolean hasPermission(String permissionCode) {
        return permissions != null && permissions.contains(permissionCode);
    }

    public boolean hasAnyPermission(Set<String> permissionCodes) {
        return permissions != null && permissionCodes != null && permissionCodes.stream().anyMatch(permissions::contains);
    }

    public boolean hasAllPermissions(Set<String> permissionCodes) {
        return permissions != null && permissionCodes != null && permissions.containsAll(permissionCodes);
    }

}
