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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.factory.FactoryPermission;
import org.thingsboard.server.common.data.factory.FactoryRole;
import org.thingsboard.server.common.data.factory.FactoryRoleScope;
import org.thingsboard.server.common.data.factory.FactorySubjectRole;
import org.thingsboard.server.common.data.factory.FactorySubjectType;
import org.thingsboard.server.common.data.factory.FactoryUserPermissions;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.FactoryRoleId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.dao.eventsourcing.DeleteEntityEvent;
import org.thingsboard.server.dao.eventsourcing.SaveEntityEvent;
import org.thingsboard.server.exception.DataValidationException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FactoryRoleServiceImpl implements FactoryRoleService {

    private final FactoryRoleDao factoryRoleDao;
    private final FactoryPermissionDao factoryPermissionDao;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public FactoryRole saveRole(TenantId tenantId, FactoryRole role) {
        log.trace("Executing saveRole [{}][{}]", tenantId, role);
        FactoryRole oldRole = role.getId() != null ? findRoleById(tenantId, role.getId()) : null;
        FactoryRole savedRole = factoryRoleDao.save(tenantId, role);
        eventPublisher.publishEvent(SaveEntityEvent.builder()
                .tenantId(savedRole.getTenantId())
                .entityId(savedRole.getId())
                .entity(savedRole)
                .oldEntity(oldRole)
                .created(oldRole == null)
                .build());
        return savedRole;
    }

    @Override
    public FactoryRole findRoleById(TenantId tenantId, FactoryRoleId roleId) {
        log.trace("Executing findRoleById [{}][{}]", tenantId, roleId);
        return factoryRoleDao.findById(tenantId, roleId);
    }

    @Override
    public FactoryRole findRoleByTenantIdAndCode(TenantId tenantId, String roleCode) {
        log.trace("Executing findRoleByTenantIdAndCode [{}][{}]", tenantId, roleCode);
        return factoryRoleDao.findByTenantIdAndCode(tenantId, roleCode);
    }

    @Override
    public PageData<FactoryRole> findRoles(TenantId tenantId, PageLink pageLink) {
        log.trace("Executing findRoles [{}][{}]", tenantId, pageLink);
        return factoryRoleDao.findRoles(tenantId, pageLink);
    }

    @Override
    @Transactional
    public void deleteRole(TenantId tenantId, FactoryRoleId roleId) {
        log.trace("Executing deleteRole [{}][{}]", tenantId, roleId);
        FactoryRole role = findRoleById(tenantId, roleId);
        if (role != null) {
            factoryRoleDao.deleteById(tenantId, roleId);
            eventPublisher.publishEvent(DeleteEntityEvent.builder()
                    .tenantId(tenantId)
                    .entityId(roleId)
                    .entity(role)
                    .build());
        }
    }

    @Override
    public List<FactoryPermission> findAllPermissions() {
        log.trace("Executing findAllPermissions");
        return factoryPermissionDao.findAll();
    }

    @Override
    public List<String> findRolePermissionCodes(TenantId tenantId, FactoryRoleId roleId) {
        log.trace("Executing findRolePermissionCodes [{}][{}]", tenantId, roleId);
        return factoryRoleDao.findRolePermissionCodes(tenantId, roleId);
    }

    @Override
    @Transactional
    public void saveRolePermissions(TenantId tenantId, FactoryRoleId roleId, List<String> permissionCodes) {
        log.trace("Executing saveRolePermissions [{}][{}]", tenantId, roleId);
        FactoryRole role = findRoleById(tenantId, roleId);
        if (role == null) {
            throw new DataValidationException("Factory role does not exist!");
        }
        factoryRoleDao.saveRolePermissions(tenantId, roleId, validatePermissionCodes(permissionCodes));
    }

    @Override
    public PageData<FactorySubjectRole> findRoleSubjects(TenantId tenantId, FactoryRoleId roleId, PageLink pageLink) {
        log.trace("Executing findRoleSubjects [{}][{}][{}]", tenantId, roleId, pageLink);
        return factoryRoleDao.findRoleSubjects(tenantId, roleId, pageLink);
    }

    @Override
    public List<FactorySubjectRole> findSubjectRoles(TenantId tenantId, FactorySubjectType subjectType, UUID subjectId) {
        log.trace("Executing findSubjectRoles [{}][{}][{}]", tenantId, subjectType, subjectId);
        return factoryRoleDao.findSubjectRoles(tenantId, subjectType, subjectId);
    }

    @Override
    @Transactional
    public void assignSubjects(TenantId tenantId, FactoryRoleId roleId, List<FactorySubjectRole> subjects) {
        log.trace("Executing assignSubjects [{}][{}]", tenantId, roleId);
        FactoryRole role = findRoleById(tenantId, roleId);
        if (role == null) {
            throw new DataValidationException("Factory role does not exist!");
        }
        factoryRoleDao.assignSubjects(tenantId, roleId, subjects);
    }

    @Override
    @Transactional
    public void unassignSubjects(TenantId tenantId, FactoryRoleId roleId, FactorySubjectType subjectType, List<UUID> subjectIds) {
        log.trace("Executing unassignSubjects [{}][{}][{}]", tenantId, roleId, subjectType);
        factoryRoleDao.unassignSubjects(tenantId, roleId, subjectType, subjectIds);
    }

    @Override
    public List<FactoryRoleScope> findRoleScopes(TenantId tenantId, FactoryRoleId roleId) {
        log.trace("Executing findRoleScopes [{}][{}]", tenantId, roleId);
        return factoryRoleDao.findRoleScopes(tenantId, roleId);
    }

    @Override
    public List<FactoryRoleScope> findSubjectScopes(TenantId tenantId, FactorySubjectType subjectType, UUID subjectId) {
        log.trace("Executing findSubjectScopes [{}][{}][{}]", tenantId, subjectType, subjectId);
        return factoryRoleDao.findSubjectScopes(tenantId, subjectType, subjectId);
    }

    @Override
    @Transactional
    public void saveRoleScopes(TenantId tenantId, FactoryRoleId roleId, List<FactoryRoleScope> scopes) {
        log.trace("Executing saveRoleScopes [{}][{}]", tenantId, roleId);
        FactoryRole role = findRoleById(tenantId, roleId);
        if (role == null) {
            throw new DataValidationException("Factory role does not exist!");
        }
        factoryRoleDao.saveRoleScopes(tenantId, roleId, scopes);
    }

    @Override
    @Transactional
    public void saveSubjectScopes(TenantId tenantId, FactorySubjectType subjectType, UUID subjectId, List<FactoryRoleScope> scopes) {
        log.trace("Executing saveSubjectScopes [{}][{}][{}]", tenantId, subjectType, subjectId);
        factoryRoleDao.saveSubjectScopes(tenantId, subjectType, subjectId, scopes);
    }

    @Override
    public FactoryUserPermissions findUserPermissions(TenantId tenantId, UserId userId, CustomerId customerId) {
        log.trace("Executing findUserPermissions [{}][{}][{}]", tenantId, userId, customerId);
        List<FactorySubjectRole> subjectRoles = new ArrayList<>(factoryRoleDao.findSubjectRoles(tenantId, FactorySubjectType.USER, userId.getId()));
        if (customerId != null && customerId.getId() != null && !EntityId.NULL_UUID.equals(customerId.getId())) {
            subjectRoles.addAll(factoryRoleDao.findSubjectRoles(tenantId, FactorySubjectType.CUSTOMER, customerId.getId()));
        }

        Set<UUID> roleIds = new LinkedHashSet<>();
        subjectRoles.forEach(subjectRole -> {
            if (subjectRole.getRoleId() != null) {
                roleIds.add(subjectRole.getRoleId().getId());
            }
        });

        List<FactoryRole> roles = factoryRoleDao.findByTenantIdAndIds(tenantId, roleIds).stream()
                .filter(FactoryRole::isEnabled)
                .toList();
        Set<UUID> enabledRoleIds = toRoleUuids(roles);
        Set<String> enabledPermissionCodes = factoryPermissionDao.findEnabled().stream()
                .map(FactoryPermission::getPermissionCode)
                .collect(Collectors.toSet());
        Set<String> permissionCodes = factoryRoleDao.findPermissionCodesByRoleIds(tenantId, enabledRoleIds).stream()
                .filter(enabledPermissionCodes::contains)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        List<FactoryRoleScope> scopes = new ArrayList<>();
        for (FactoryRole role : roles) {
            scopes.addAll(factoryRoleDao.findRoleScopes(tenantId, role.getId()));
        }
        scopes.addAll(factoryRoleDao.findSubjectScopes(tenantId, FactorySubjectType.USER, userId.getId()));
        if (customerId != null && customerId.getId() != null && !EntityId.NULL_UUID.equals(customerId.getId())) {
            scopes.addAll(factoryRoleDao.findSubjectScopes(tenantId, FactorySubjectType.CUSTOMER, customerId.getId()));
        }

        FactoryUserPermissions permissions = new FactoryUserPermissions();
        permissions.setTenantId(tenantId);
        permissions.setCustomerId(customerId);
        permissions.setUserId(userId);
        permissions.setAuthority(resolveBaseAuthority(tenantId, customerId));
        permissions.setRoles(roles);
        permissions.setPermissions(permissionCodes);
        permissions.setScopes(scopes);
        return permissions;
    }

    private Set<UUID> toRoleUuids(Collection<FactoryRole> roles) {
        Set<UUID> roleIds = new LinkedHashSet<>();
        roles.forEach(role -> {
            if (role.getId() != null) {
                roleIds.add(role.getId().getId());
            }
        });
        return roleIds;
    }

    private List<String> validatePermissionCodes(List<String> permissionCodes) {
        if (permissionCodes == null || permissionCodes.isEmpty()) {
            return List.of();
        }
        List<String> normalizedCodes = permissionCodes.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(code -> !code.isEmpty())
                .distinct()
                .toList();
        Set<String> enabledPermissionCodes = factoryPermissionDao.findEnabled().stream()
                .map(FactoryPermission::getPermissionCode)
                .collect(Collectors.toSet());
        List<String> unknownCodes = normalizedCodes.stream()
                .filter(code -> !enabledPermissionCodes.contains(code))
                .toList();
        if (!unknownCodes.isEmpty()) {
            throw new DataValidationException("Unknown or disabled factory permission codes: " + unknownCodes);
        }
        return normalizedCodes;
    }

    private Authority resolveBaseAuthority(TenantId tenantId, CustomerId customerId) {
        if (TenantId.SYS_TENANT_ID.equals(tenantId)) {
            return Authority.SYS_ADMIN;
        }
        if (customerId != null && customerId.getId() != null && !EntityId.NULL_UUID.equals(customerId.getId())) {
            return Authority.CUSTOMER_USER;
        }
        return Authority.TENANT_ADMIN;
    }

}
