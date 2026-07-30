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
import org.thingsboard.server.common.data.factory.FactoryPermission;
import org.thingsboard.server.common.data.id.FactoryPermissionId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.factory.FactoryPermissionDao;
import org.thingsboard.server.dao.model.sql.factory.FactoryPermissionEntity;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.List;
import java.util.UUID;

@Component
@SqlDao
public class JpaFactoryPermissionDao extends JpaAbstractDao<FactoryPermissionEntity, FactoryPermission> implements FactoryPermissionDao {

    @Autowired
    private FactoryPermissionRepository factoryPermissionRepository;
    @Autowired
    private FactoryRolePermissionRepository factoryRolePermissionRepository;

    @Override
    protected Class<FactoryPermissionEntity> getEntityClass() {
        return FactoryPermissionEntity.class;
    }

    @Override
    protected JpaRepository<FactoryPermissionEntity, UUID> getRepository() {
        return factoryPermissionRepository;
    }

    @Override
    public FactoryPermission save(FactoryPermission permission) {
        return super.save(TenantId.SYS_TENANT_ID, permission);
    }

    @Override
    public FactoryPermission findById(FactoryPermissionId permissionId) {
        return DaoUtil.getData(factoryPermissionRepository.findById(permissionId.getId()));
    }

    @Override
    public FactoryPermission findByCode(String permissionCode) {
        return DaoUtil.getData(factoryPermissionRepository.findByPermissionCode(permissionCode));
    }

    @Override
    public PageData<FactoryPermission> findPermissions(PageLink pageLink) {
        return DaoUtil.toPageData(factoryPermissionRepository.findPermissions(pageLink.getTextSearch(), DaoUtil.toPageable(pageLink)));
    }

    @Override
    public List<FactoryPermission> findAll() {
        return DaoUtil.convertDataList(factoryPermissionRepository.findAllByOrderBySortOrderAscPermissionCodeAsc());
    }

    @Override
    public List<FactoryPermission> findEnabled() {
        return DaoUtil.convertDataList(factoryPermissionRepository.findByEnabledTrueOrderBySortOrderAscPermissionCodeAsc());
    }

    @Override
    @Transactional
    public void deleteById(FactoryPermissionId permissionId) {
        FactoryPermission permission = findById(permissionId);
        if (permission != null) {
            factoryRolePermissionRepository.deleteByPermissionCode(permission.getPermissionCode());
            factoryPermissionRepository.deleteById(permissionId.getId());
            factoryRolePermissionRepository.flush();
            factoryPermissionRepository.flush();
        }
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.FACTORY_PERMISSION;
    }

}
