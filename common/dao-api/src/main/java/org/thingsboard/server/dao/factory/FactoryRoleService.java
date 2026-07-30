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
package org.thingsboard.server.dao.factory;

import org.thingsboard.server.common.data.factory.FactoryPermission;
import org.thingsboard.server.common.data.factory.FactoryRole;
import org.thingsboard.server.common.data.factory.FactoryRoleScope;
import org.thingsboard.server.common.data.factory.FactorySubjectRole;
import org.thingsboard.server.common.data.factory.FactorySubjectType;
import org.thingsboard.server.common.data.factory.FactoryUserPermissions;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.FactoryRoleId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;

import java.util.List;
import java.util.UUID;

public interface FactoryRoleService {

    FactoryRole saveRole(TenantId tenantId, FactoryRole role);

    FactoryRole findRoleById(TenantId tenantId, FactoryRoleId roleId);

    FactoryRole findRoleByTenantIdAndCode(TenantId tenantId, String roleCode);

    PageData<FactoryRole> findRoles(TenantId tenantId, PageLink pageLink);

    void deleteRole(TenantId tenantId, FactoryRoleId roleId);

    List<FactoryPermission> findAllPermissions();

    List<String> findRolePermissionCodes(TenantId tenantId, FactoryRoleId roleId);

    void saveRolePermissions(TenantId tenantId, FactoryRoleId roleId, List<String> permissionCodes);

    PageData<FactorySubjectRole> findRoleSubjects(TenantId tenantId, FactoryRoleId roleId, PageLink pageLink);

    List<FactorySubjectRole> findSubjectRoles(TenantId tenantId, FactorySubjectType subjectType, UUID subjectId);

    void assignSubjects(TenantId tenantId, FactoryRoleId roleId, List<FactorySubjectRole> subjects);

    void unassignSubjects(TenantId tenantId, FactoryRoleId roleId, FactorySubjectType subjectType, List<UUID> subjectIds);

    List<FactoryRoleScope> findRoleScopes(TenantId tenantId, FactoryRoleId roleId);

    List<FactoryRoleScope> findSubjectScopes(TenantId tenantId, FactorySubjectType subjectType, UUID subjectId);

    void saveRoleScopes(TenantId tenantId, FactoryRoleId roleId, List<FactoryRoleScope> scopes);

    void saveSubjectScopes(TenantId tenantId, FactorySubjectType subjectType, UUID subjectId, List<FactoryRoleScope> scopes);

    FactoryUserPermissions findUserPermissions(TenantId tenantId, UserId userId, CustomerId customerId);

}
