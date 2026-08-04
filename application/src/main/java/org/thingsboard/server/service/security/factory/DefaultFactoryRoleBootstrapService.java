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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.factory.FactoryPermission;
import org.thingsboard.server.common.data.factory.FactoryPermissionCodes;
import org.thingsboard.server.common.data.factory.FactoryRole;
import org.thingsboard.server.common.data.factory.FactoryRoleScope;
import org.thingsboard.server.common.data.factory.FactoryRoleScopeTargetType;
import org.thingsboard.server.common.data.factory.FactoryRoleType;
import org.thingsboard.server.common.data.factory.FactoryScopeType;
import org.thingsboard.server.common.data.factory.FactorySubjectRole;
import org.thingsboard.server.common.data.factory.FactorySubjectType;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.dao.factory.FactoryRoleService;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@TbCoreComponent
@RequiredArgsConstructor
@Slf4j
public class DefaultFactoryRoleBootstrapService implements FactoryRoleBootstrapService {

    public static final String FACTORY_ADMIN_ROLE_CODE = "FACTORY_ADMIN";

    public static final String FACTORY_MANAGER_ROLE_CODE = "FACTORY_MANAGER";
    public static final String PROCESS_ENGINEER_ROLE_CODE = "PROCESS_ENGINEER";
    public static final String SHIFT_LEADER_ROLE_CODE = "SHIFT_LEADER";
    public static final String OPERATOR_ROLE_CODE = "OPERATOR";
    public static final String VISITOR_ROLE_CODE = "VISITOR";

    private static final List<RoleDefinition> DEFAULT_ROLES = List.of(
            new RoleDefinition(
                    FACTORY_ADMIN_ROLE_CODE,
                    "Factory Administrator",
                    Authority.TENANT_ADMIN,
                    "Default factory administrator role for tenant administrators.",
                    List.of()),
            new RoleDefinition(
                    FACTORY_MANAGER_ROLE_CODE,
                    "Factory Manager",
                    Authority.TENANT_ADMIN,
                    "Default factory manager role for production overview, scheduling and reports.",
                    List.of(
                            FactoryPermissionCodes.ROLE_READ,
                            FactoryPermissionCodes.USER_READ,
                            FactoryPermissionCodes.CUSTOMER_READ,
                            FactoryPermissionCodes.DASHBOARD_READ,
                            FactoryPermissionCodes.DEVICE_READ,
                            FactoryPermissionCodes.DEVICE_PROFILE_READ,
                            FactoryPermissionCodes.ASSET_READ,
                            FactoryPermissionCodes.ASSET_PROFILE_READ,
                            FactoryPermissionCodes.RELATION_READ,
                            FactoryPermissionCodes.TELEMETRY_READ,
                            FactoryPermissionCodes.ATTRIBUTE_READ,
                            FactoryPermissionCodes.ALARM_READ,
                            FactoryPermissionCodes.ALARM_ACK,
                            FactoryPermissionCodes.ALARM_CLEAR,
                            FactoryPermissionCodes.ALARM_ASSIGN,
                            FactoryPermissionCodes.ALARM_COMMENT_READ,
                            FactoryPermissionCodes.ALARM_COMMENT_WRITE,
                            FactoryPermissionCodes.RECIPE_READ,
                            FactoryPermissionCodes.RECIPE_APPROVE,
                            FactoryPermissionCodes.BATCH_READ,
                            FactoryPermissionCodes.PRODUCTION_READ,
                            FactoryPermissionCodes.PRODUCTION_SCHEDULE,
                            FactoryPermissionCodes.REPORT_READ,
                            FactoryPermissionCodes.RULECHAIN_READ)),
            new RoleDefinition(
                    PROCESS_ENGINEER_ROLE_CODE,
                    "Process Engineer",
                    Authority.TENANT_ADMIN,
                    "Default process engineer role for recipe design, parameter monitoring and recipe dispatch.",
                    List.of(
                            FactoryPermissionCodes.DASHBOARD_READ,
                            FactoryPermissionCodes.DEVICE_READ,
                            FactoryPermissionCodes.DEVICE_PROFILE_READ,
                            FactoryPermissionCodes.ASSET_READ,
                            FactoryPermissionCodes.ASSET_PROFILE_READ,
                            FactoryPermissionCodes.RELATION_READ,
                            FactoryPermissionCodes.TELEMETRY_READ,
                            FactoryPermissionCodes.TELEMETRY_WRITE,
                            FactoryPermissionCodes.ATTRIBUTE_READ,
                            FactoryPermissionCodes.ATTRIBUTE_WRITE,
                            FactoryPermissionCodes.ALARM_READ,
                            FactoryPermissionCodes.ALARM_COMMENT_READ,
                            FactoryPermissionCodes.RECIPE_READ,
                            FactoryPermissionCodes.RECIPE_CREATE,
                            FactoryPermissionCodes.RECIPE_UPDATE,
                            FactoryPermissionCodes.RECIPE_DELETE,
                            FactoryPermissionCodes.RECIPE_APPROVE,
                            FactoryPermissionCodes.RECIPE_DISPATCH,
                            FactoryPermissionCodes.BATCH_READ,
                            FactoryPermissionCodes.PRODUCTION_READ,
                            FactoryPermissionCodes.REPORT_READ,
                            FactoryPermissionCodes.MACHINE_CONTROL,
                            FactoryPermissionCodes.MACHINE_PARAM_WRITE)),
            new RoleDefinition(
                    SHIFT_LEADER_ROLE_CODE,
                    "Shift Leader",
                    Authority.CUSTOMER_USER,
                    "Default shift leader role for shift monitoring, alarm handling and emergency operations.",
                    List.of(
                            FactoryPermissionCodes.DASHBOARD_READ,
                            FactoryPermissionCodes.DEVICE_READ,
                            FactoryPermissionCodes.DEVICE_PROFILE_READ,
                            FactoryPermissionCodes.ASSET_READ,
                            FactoryPermissionCodes.ASSET_PROFILE_READ,
                            FactoryPermissionCodes.RELATION_READ,
                            FactoryPermissionCodes.TELEMETRY_READ,
                            FactoryPermissionCodes.ATTRIBUTE_READ,
                            FactoryPermissionCodes.ALARM_READ,
                            FactoryPermissionCodes.ALARM_ACK,
                            FactoryPermissionCodes.ALARM_CLEAR,
                            FactoryPermissionCodes.ALARM_ASSIGN,
                            FactoryPermissionCodes.ALARM_COMMENT_READ,
                            FactoryPermissionCodes.ALARM_COMMENT_WRITE,
                            FactoryPermissionCodes.RECIPE_READ,
                            FactoryPermissionCodes.RECIPE_DISPATCH,
                            FactoryPermissionCodes.BATCH_READ,
                            FactoryPermissionCodes.BATCH_OPERATE,
                            FactoryPermissionCodes.PRODUCTION_READ,
                            FactoryPermissionCodes.REPORT_READ,
                            FactoryPermissionCodes.MACHINE_EMERGENCY_STOP,
                            FactoryPermissionCodes.MACHINE_CONTROL)),
            new RoleDefinition(
                    OPERATOR_ROLE_CODE,
                    "Operator",
                    Authority.CUSTOMER_USER,
                    "Default operator role for daily machine operation and authorized parameter updates.",
                    List.of(
                            FactoryPermissionCodes.DASHBOARD_READ,
                            FactoryPermissionCodes.DEVICE_READ,
                            FactoryPermissionCodes.DEVICE_PROFILE_READ,
                            FactoryPermissionCodes.TELEMETRY_READ,
                            FactoryPermissionCodes.ATTRIBUTE_READ,
                            FactoryPermissionCodes.ATTRIBUTE_WRITE,
                            FactoryPermissionCodes.ALARM_READ,
                            FactoryPermissionCodes.ALARM_ACK,
                            FactoryPermissionCodes.ALARM_COMMENT_READ,
                            FactoryPermissionCodes.RECIPE_READ,
                            FactoryPermissionCodes.RECIPE_DISPATCH,
                            FactoryPermissionCodes.BATCH_READ,
                            FactoryPermissionCodes.BATCH_OPERATE,
                            FactoryPermissionCodes.PRODUCTION_READ,
                            FactoryPermissionCodes.MACHINE_EMERGENCY_STOP,
                            FactoryPermissionCodes.MACHINE_CONTROL,
                            FactoryPermissionCodes.MACHINE_PARAM_WRITE)),
            new RoleDefinition(
                    VISITOR_ROLE_CODE,
                    "Visitor",
                    Authority.CUSTOMER_USER,
                    "Default visitor role for read-only production dashboards and reports.",
                    List.of(
                            FactoryPermissionCodes.DASHBOARD_READ,
                            FactoryPermissionCodes.PRODUCTION_READ,
                            FactoryPermissionCodes.REPORT_READ))
    );

    private final FactoryRoleService factoryRoleService;

    @Override
    public void ensureTenantDefaultRoles(TenantId tenantId) {
        if (tenantId == null || TenantId.SYS_TENANT_ID.equals(tenantId)) {
            return;
        }
        DEFAULT_ROLES.forEach(roleDefinition -> ensureDefaultRole(tenantId, roleDefinition));
    }

    @Override
    public void assignFactoryAdminToTenantAdmin(User user) {
        if (user == null || user.getId() == null || user.getTenantId() == null ||
                !Authority.TENANT_ADMIN.equals(user.getAuthority())) {
            return;
        }

        FactoryRole role = ensureFactoryAdminRole(user.getTenantId());
        boolean alreadyAssigned = factoryRoleService.findSubjectRoles(user.getTenantId(), FactorySubjectType.USER, user.getUuidId()).stream()
                .anyMatch(subjectRole -> subjectRole.getRoleId() != null && Objects.equals(subjectRole.getRoleId(), role.getId()));
        if (alreadyAssigned) {
            return;
        }

        FactorySubjectRole subjectRole = new FactorySubjectRole();
        subjectRole.setTenantId(user.getTenantId());
        subjectRole.setSubjectType(FactorySubjectType.USER);
        subjectRole.setSubjectId(user.getUuidId());
        subjectRole.setRoleId(role.getId());
        subjectRole.setEnabled(true);
        subjectRole.setCreatedBy(user.getId());
        factoryRoleService.assignSubjects(user.getTenantId(), role.getId(), List.of(subjectRole));
        log.debug("[{}][{}] Assigned {} factory role to tenant admin user", user.getTenantId(), user.getId(), FACTORY_ADMIN_ROLE_CODE);
    }

    private FactoryRole ensureFactoryAdminRole(TenantId tenantId) {
        return ensureDefaultRole(tenantId, DEFAULT_ROLES.stream()
                .filter(roleDefinition -> FACTORY_ADMIN_ROLE_CODE.equals(roleDefinition.roleCode()))
                .findFirst()
                .orElseThrow());
    }

    private FactoryRole ensureDefaultRole(TenantId tenantId, RoleDefinition definition) {
        FactoryRole role = factoryRoleService.findRoleByTenantIdAndCode(tenantId, definition.roleCode());
        if (role == null) {
            role = new FactoryRole();
            role.setTenantId(tenantId);
            role.setRoleCode(definition.roleCode());
        }

        boolean changed = false;
        if (!Objects.equals(role.getRoleName(), definition.roleName())) {
            role.setRoleName(definition.roleName());
            changed = true;
        }
        if (!FactoryRoleType.SYSTEM.equals(role.getRoleType())) {
            role.setRoleType(FactoryRoleType.SYSTEM);
            changed = true;
        }
        if (!definition.baseAuthority().equals(role.getBaseAuthority())) {
            role.setBaseAuthority(definition.baseAuthority());
            changed = true;
        }
        if (!Objects.equals(role.getDescription(), definition.description())) {
            role.setDescription(definition.description());
            changed = true;
        }
        if (!role.isEnabled()) {
            role.setEnabled(true);
            changed = true;
        }
        if (role.getId() == null || changed) {
            role = factoryRoleService.saveRole(tenantId, role);
        }

        factoryRoleService.saveRolePermissions(tenantId, role.getId(), getPermissionCodes(definition));
        factoryRoleService.saveRoleScopes(tenantId, role.getId(), List.of(createTenantScope(tenantId, role)));
        return role;
    }

    private List<String> getPermissionCodes(RoleDefinition definition) {
        Set<String> enabledPermissionCodes = getEnabledPermissionCodes();
        if (FACTORY_ADMIN_ROLE_CODE.equals(definition.roleCode())) {
            return enabledPermissionCodes.stream()
                    .filter(permissionCode -> !FactoryPermissionCodes.PERMISSION_MANAGE.equals(permissionCode))
                    .toList();
        }
        return distinctEnabled(definition.permissionCodes(), enabledPermissionCodes);
    }

    private Set<String> getEnabledPermissionCodes() {
        return factoryRoleService.findAllPermissions().stream()
                .filter(FactoryPermission::isEnabled)
                .map(FactoryPermission::getPermissionCode)
                .filter(Objects::nonNull)
                .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
    }

    private List<String> distinctEnabled(Collection<String> permissionCodes, Set<String> enabledPermissionCodes) {
        return permissionCodes.stream()
                .filter(Objects::nonNull)
                .filter(enabledPermissionCodes::contains)
                .distinct()
                .toList();
    }

    private FactoryRoleScope createTenantScope(TenantId tenantId, FactoryRole role) {
        FactoryRoleScope scope = new FactoryRoleScope();
        scope.setTenantId(tenantId);
        scope.setTargetType(FactoryRoleScopeTargetType.ROLE);
        scope.setTargetId(role.getUuidId());
        scope.setScopeType(FactoryScopeType.TENANT);
        scope.setEntityType(EntityType.TENANT);
        scope.setEntityId(tenantId.getId());
        return scope;
    }

    private record RoleDefinition(String roleCode, String roleName, Authority baseAuthority, String description,
                                  List<String> permissionCodes) {
    }

}
