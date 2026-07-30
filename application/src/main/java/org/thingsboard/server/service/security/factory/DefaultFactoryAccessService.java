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
package org.thingsboard.server.service.security.factory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.factory.FactoryRoleScope;
import org.thingsboard.server.common.data.factory.FactoryScopeType;
import org.thingsboard.server.common.data.factory.FactoryUserPermissions;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.dao.factory.FactoryRoleService;
import org.thingsboard.server.service.security.model.SecurityUser;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DefaultFactoryAccessService implements FactoryAccessService {

    private static final String PERMISSION_DENIED_MSG = "You don't have permission to perform this operation!";

    private final FactoryRoleService factoryRoleService;

    @Override
    public void checkPermission(SecurityUser user, String permissionCode) throws ThingsboardException {
        if (!hasPermission(user, permissionCode)) {
            throw new ThingsboardException(PERMISSION_DENIED_MSG, ThingsboardErrorCode.PERMISSION_DENIED);
        }
    }

    @Override
    public void checkPermission(SecurityUser user, String permissionCode, EntityId targetEntityId) throws ThingsboardException {
        if (!hasPermission(user, permissionCode)) {
            throw new ThingsboardException(PERMISSION_DENIED_MSG, ThingsboardErrorCode.PERMISSION_DENIED);
        }
        if (targetEntityId != null && !hasScope(user, targetEntityId)) {
            throw new ThingsboardException(PERMISSION_DENIED_MSG, ThingsboardErrorCode.PERMISSION_DENIED);
        }
    }

    @Override
    public boolean hasPermission(SecurityUser user, String permissionCode) {
        if (user == null || permissionCode == null) {
            return false;
        }
        if (Authority.SYS_ADMIN.equals(user.getAuthority())) {
            return true;
        }
        if (Authority.TENANT_ADMIN.equals(user.getAuthority()) && isTenantAdminBootstrapPermission(permissionCode)) {
            return true;
        }
        FactoryUserPermissions permissions = factoryRoleService.findUserPermissions(user.getTenantId(), user.getId(), user.getCustomerId());
        return permissions.hasPermission(permissionCode);
    }

    private boolean isTenantAdminBootstrapPermission(String permissionCode) {
        return "role:manage".equals(permissionCode);
    }

    private boolean hasScope(SecurityUser user, EntityId targetEntityId) {
        if (Authority.SYS_ADMIN.equals(user.getAuthority())) {
            return true;
        }
        FactoryUserPermissions permissions = factoryRoleService.findUserPermissions(user.getTenantId(), user.getId(), user.getCustomerId());
        return permissions.getScopes() != null && permissions.getScopes().stream().anyMatch(scope -> matchesScope(scope, targetEntityId));
    }

    private boolean matchesScope(FactoryRoleScope scope, EntityId targetEntityId) {
        if (scope == null) {
            return false;
        }
        if (FactoryScopeType.TENANT.equals(scope.getScopeType())) {
            return true;
        }
        return Objects.equals(scope.getEntityType(), targetEntityId.getEntityType())
                && Objects.equals(scope.getEntityId(), targetEntityId.getId());
    }

}
