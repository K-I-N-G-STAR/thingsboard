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
package org.thingsboard.server.dao.sql.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.factory.FactoryRole;
import org.thingsboard.server.common.data.factory.FactoryRolePermission;
import org.thingsboard.server.common.data.factory.FactoryRoleScope;
import org.thingsboard.server.common.data.factory.FactoryRoleScopeTargetType;
import org.thingsboard.server.common.data.factory.FactorySubjectRole;
import org.thingsboard.server.common.data.factory.FactorySubjectType;
import org.thingsboard.server.common.data.id.FactoryRoleId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.factory.FactoryRoleDao;
import org.thingsboard.server.dao.model.sql.factory.FactoryRoleEntity;
import org.thingsboard.server.dao.model.sql.factory.FactoryRolePermissionEntity;
import org.thingsboard.server.dao.model.sql.factory.FactoryRoleScopeEntity;
import org.thingsboard.server.dao.model.sql.factory.FactorySubjectRoleEntity;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@SqlDao
public class JpaFactoryRoleDao extends JpaAbstractDao<FactoryRoleEntity, FactoryRole> implements FactoryRoleDao {

    @Autowired
    private FactoryRoleRepository factoryRoleRepository;
    @Autowired
    private FactoryRolePermissionRepository factoryRolePermissionRepository;
    @Autowired
    private FactorySubjectRoleRepository factorySubjectRoleRepository;
    @Autowired
    private FactoryRoleScopeRepository factoryRoleScopeRepository;

    @Override
    protected Class<FactoryRoleEntity> getEntityClass() {
        return FactoryRoleEntity.class;
    }

    @Override
    protected JpaRepository<FactoryRoleEntity, UUID> getRepository() {
        return factoryRoleRepository;
    }

    @Override
    public FactoryRole save(TenantId tenantId, FactoryRole role) {
        if (role.getTenantId() == null) {
            role.setTenantId(tenantId);
        }
        role.setUpdatedTime(System.currentTimeMillis());
        return super.save(tenantId, role);
    }

    @Override
    public FactoryRole findById(TenantId tenantId, FactoryRoleId roleId) {
        return DaoUtil.getData(factoryRoleRepository.findByTenantIdAndId(tenantId.getId(), roleId.getId()));
    }

    @Override
    public FactoryRole findByTenantIdAndCode(TenantId tenantId, String roleCode) {
        return DaoUtil.getData(factoryRoleRepository.findByTenantIdAndRoleCode(tenantId.getId(), roleCode));
    }

    @Override
    public List<FactoryRole> findByTenantIdAndIds(TenantId tenantId, Collection<UUID> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Collections.emptyList();
        }
        return DaoUtil.convertDataList(factoryRoleRepository.findByTenantIdAndIdIn(tenantId.getId(), roleIds));
    }

    @Override
    public PageData<FactoryRole> findRoles(TenantId tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(factoryRoleRepository.findByTenantId(tenantId.getId(), pageLink.getTextSearch(), DaoUtil.toPageable(pageLink)));
    }

    @Override
    @Transactional
    public void deleteById(TenantId tenantId, FactoryRoleId roleId) {
        UUID tenantUuid = tenantId.getId();
        UUID roleUuid = roleId.getId();
        factoryRolePermissionRepository.deleteByTenantIdAndRoleId(tenantUuid, roleUuid);
        factorySubjectRoleRepository.deleteByTenantIdAndRoleId(tenantUuid, roleUuid);
        factoryRoleScopeRepository.deleteByTenantIdAndTargetTypeAndTargetId(tenantUuid, FactoryRoleScopeTargetType.ROLE, roleUuid);
        factoryRoleRepository.deleteById(roleUuid);
        factoryRoleRepository.flush();
    }

    @Override
    public List<String> findRolePermissionCodes(TenantId tenantId, FactoryRoleId roleId) {
        return factoryRolePermissionRepository.findByTenantIdAndRoleId(tenantId.getId(), roleId.getId()).stream()
                .map(FactoryRolePermissionEntity::getPermissionCode)
                .toList();
    }

    @Override
    public List<String> findPermissionCodesByRoleIds(TenantId tenantId, Collection<UUID> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Collections.emptyList();
        }
        Set<String> permissionCodes = new LinkedHashSet<>();
        factoryRolePermissionRepository.findByTenantIdAndRoleIdIn(tenantId.getId(), roleIds).forEach(entity -> {
            if (entity.getPermissionCode() != null) {
                permissionCodes.add(entity.getPermissionCode());
            }
        });
        return List.copyOf(permissionCodes);
    }

    @Override
    @Transactional
    public void saveRolePermissions(TenantId tenantId, FactoryRoleId roleId, List<String> permissionCodes) {
        UUID tenantUuid = tenantId.getId();
        UUID roleUuid = roleId.getId();
        factoryRolePermissionRepository.deleteByTenantIdAndRoleId(tenantUuid, roleUuid);
        if (permissionCodes != null && !permissionCodes.isEmpty()) {
            long now = System.currentTimeMillis();
            List<FactoryRolePermissionEntity> entities = permissionCodes.stream()
                    .distinct()
                    .map(permissionCode -> {
                        FactoryRolePermission rolePermission = new FactoryRolePermission();
                        rolePermission.setCreatedTime(now);
                        rolePermission.setTenantId(tenantId);
                        rolePermission.setRoleId(roleId);
                        rolePermission.setPermissionCode(permissionCode);
                        return new FactoryRolePermissionEntity(rolePermission);
                    })
                    .toList();
            factoryRolePermissionRepository.saveAll(entities);
        }
        factoryRolePermissionRepository.flush();
    }

    @Override
    public PageData<FactorySubjectRole> findRoleSubjects(TenantId tenantId, FactoryRoleId roleId, PageLink pageLink) {
        return DaoUtil.toPageData(factorySubjectRoleRepository.findByTenantIdAndRoleId(tenantId.getId(), roleId.getId(), DaoUtil.toPageable(pageLink)));
    }

    @Override
    public List<FactorySubjectRole> findSubjectRoles(TenantId tenantId, FactorySubjectType subjectType, UUID subjectId) {
        return DaoUtil.convertDataList(factorySubjectRoleRepository.findByTenantIdAndSubjectTypeAndSubjectIdAndEnabledTrue(tenantId.getId(), subjectType, subjectId));
    }

    @Override
    @Transactional
    public void assignSubjects(TenantId tenantId, FactoryRoleId roleId, List<FactorySubjectRole> subjects) {
        if (subjects == null || subjects.isEmpty()) {
            return;
        }
        List<FactorySubjectRole> normalizedSubjects = subjects.stream()
                .filter(subject -> subject.getSubjectType() != null && subject.getSubjectId() != null)
                .collect(Collectors.toMap(
                        subject -> subject.getSubjectType() + ":" + subject.getSubjectId(),
                        Function.identity(),
                        (previous, current) -> current,
                        LinkedHashMap::new))
                .values().stream()
                .toList();
        if (normalizedSubjects.isEmpty()) {
            return;
        }
        Map<FactorySubjectType, List<UUID>> subjectIdsByType = normalizedSubjects.stream()
                .collect(Collectors.groupingBy(FactorySubjectRole::getSubjectType,
                        Collectors.mapping(FactorySubjectRole::getSubjectId, Collectors.toList())));
        subjectIdsByType.forEach((subjectType, subjectIds) ->
                factorySubjectRoleRepository.deleteByTenantIdAndRoleIdAndSubjectTypeAndSubjectIdIn(
                        tenantId.getId(), roleId.getId(), subjectType, subjectIds));
        long now = System.currentTimeMillis();
        List<FactorySubjectRoleEntity> entities = normalizedSubjects.stream()
                .map(subject -> {
                    subject.setCreatedTime(subject.getCreatedTime() > 0 ? subject.getCreatedTime() : now);
                    subject.setTenantId(tenantId);
                    subject.setRoleId(roleId);
                    return new FactorySubjectRoleEntity(subject);
                })
                .toList();
        factorySubjectRoleRepository.saveAll(entities);
        factorySubjectRoleRepository.flush();
    }

    @Override
    @Transactional
    public void unassignSubjects(TenantId tenantId, FactoryRoleId roleId, FactorySubjectType subjectType, List<UUID> subjectIds) {
        if (subjectIds == null || subjectIds.isEmpty()) {
            return;
        }
        factorySubjectRoleRepository.deleteByTenantIdAndRoleIdAndSubjectTypeAndSubjectIdIn(tenantId.getId(), roleId.getId(), subjectType, subjectIds);
        factorySubjectRoleRepository.flush();
    }

    @Override
    public List<FactoryRoleScope> findRoleScopes(TenantId tenantId, FactoryRoleId roleId) {
        return DaoUtil.convertDataList(factoryRoleScopeRepository.findByTenantIdAndTargetTypeAndTargetId(
                tenantId.getId(), FactoryRoleScopeTargetType.ROLE, roleId.getId()));
    }

    @Override
    public List<FactoryRoleScope> findSubjectScopes(TenantId tenantId, FactorySubjectType subjectType, UUID subjectId) {
        return DaoUtil.convertDataList(factoryRoleScopeRepository.findByTenantIdAndTargetTypeAndTargetId(
                tenantId.getId(), toScopeTargetType(subjectType), subjectId));
    }

    @Override
    @Transactional
    public void saveRoleScopes(TenantId tenantId, FactoryRoleId roleId, List<FactoryRoleScope> scopes) {
        saveScopes(tenantId, FactoryRoleScopeTargetType.ROLE, roleId.getId(), scopes);
    }

    @Override
    @Transactional
    public void saveSubjectScopes(TenantId tenantId, FactorySubjectType subjectType, UUID subjectId, List<FactoryRoleScope> scopes) {
        saveScopes(tenantId, toScopeTargetType(subjectType), subjectId, scopes);
    }

    private void saveScopes(TenantId tenantId, FactoryRoleScopeTargetType targetType, UUID targetId, List<FactoryRoleScope> scopes) {
        factoryRoleScopeRepository.deleteByTenantIdAndTargetTypeAndTargetId(tenantId.getId(), targetType, targetId);
        if (scopes != null && !scopes.isEmpty()) {
            long now = System.currentTimeMillis();
            List<FactoryRoleScope> normalizedScopes = scopes.stream()
                    .filter(scope -> scope != null && scope.getScopeType() != null)
                    .collect(Collectors.toMap(
                            this::scopeKey,
                            Function.identity(),
                            (previous, current) -> current,
                            LinkedHashMap::new))
                    .values().stream()
                    .toList();
            List<FactoryRoleScopeEntity> entities = normalizedScopes.stream()
                    .map(scope -> {
                        scope.setCreatedTime(scope.getCreatedTime() > 0 ? scope.getCreatedTime() : now);
                        scope.setTenantId(tenantId);
                        scope.setTargetType(targetType);
                        scope.setTargetId(targetId);
                        return new FactoryRoleScopeEntity(scope);
                    })
                    .toList();
            factoryRoleScopeRepository.saveAll(entities);
        }
        factoryRoleScopeRepository.flush();
    }

    private String scopeKey(FactoryRoleScope scope) {
        return scope.getScopeType() + ":" +
                Objects.toString(scope.getEntityType(), "") + ":" +
                Objects.toString(scope.getEntityId(), "");
    }

    private FactoryRoleScopeTargetType toScopeTargetType(FactorySubjectType subjectType) {
        return switch (subjectType) {
            case USER -> FactoryRoleScopeTargetType.USER;
            case CUSTOMER -> FactoryRoleScopeTargetType.CUSTOMER;
        };
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.FACTORY_ROLE;
    }

}
