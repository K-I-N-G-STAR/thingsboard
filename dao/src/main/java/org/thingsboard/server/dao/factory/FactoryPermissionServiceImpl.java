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
import org.thingsboard.server.common.data.id.FactoryPermissionId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.eventsourcing.DeleteEntityEvent;
import org.thingsboard.server.dao.eventsourcing.SaveEntityEvent;
import org.thingsboard.server.exception.DataValidationException;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class FactoryPermissionServiceImpl implements FactoryPermissionService {

    private final FactoryPermissionDao factoryPermissionDao;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public FactoryPermission savePermission(FactoryPermission permission) {
        log.trace("Executing savePermission [{}]", permission);
        FactoryPermission oldPermission = permission.getId() != null ? findPermissionById(permission.getId()) : null;
        if (oldPermission != null && !Objects.equals(oldPermission.getPermissionCode(), permission.getPermissionCode())) {
            throw new DataValidationException("Factory permission code can't be changed!");
        }
        FactoryPermission savedPermission = factoryPermissionDao.save(permission);
        eventPublisher.publishEvent(SaveEntityEvent.builder()
                .tenantId(TenantId.SYS_TENANT_ID)
                .entityId(savedPermission.getId())
                .entity(savedPermission)
                .oldEntity(oldPermission)
                .created(oldPermission == null)
                .build());
        return savedPermission;
    }

    @Override
    public FactoryPermission findPermissionById(FactoryPermissionId permissionId) {
        log.trace("Executing findPermissionById [{}]", permissionId);
        return factoryPermissionDao.findById(permissionId);
    }

    @Override
    public FactoryPermission findPermissionByCode(String permissionCode) {
        log.trace("Executing findPermissionByCode [{}]", permissionCode);
        return factoryPermissionDao.findByCode(permissionCode);
    }

    @Override
    public PageData<FactoryPermission> findPermissions(PageLink pageLink) {
        log.trace("Executing findPermissions [{}]", pageLink);
        return factoryPermissionDao.findPermissions(pageLink);
    }

    @Override
    public List<FactoryPermission> findAllPermissions() {
        log.trace("Executing findAllPermissions");
        return factoryPermissionDao.findAll();
    }

    @Override
    public List<FactoryPermission> findEnabledPermissions() {
        log.trace("Executing findEnabledPermissions");
        return factoryPermissionDao.findEnabled();
    }

    @Override
    @Transactional
    public void deletePermission(FactoryPermissionId permissionId) {
        log.trace("Executing deletePermission [{}]", permissionId);
        FactoryPermission permission = findPermissionById(permissionId);
        if (permission != null) {
            factoryPermissionDao.deleteById(permissionId);
            eventPublisher.publishEvent(DeleteEntityEvent.builder()
                    .tenantId(TenantId.SYS_TENANT_ID)
                    .entityId(permissionId)
                    .entity(permission)
                    .build());
        }
    }

}
