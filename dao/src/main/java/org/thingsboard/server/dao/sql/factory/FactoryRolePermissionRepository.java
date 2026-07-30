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

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.dao.model.sql.factory.FactoryRolePermissionEntity;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface FactoryRolePermissionRepository extends JpaRepository<FactoryRolePermissionEntity, UUID> {

    List<FactoryRolePermissionEntity> findByTenantIdAndRoleId(UUID tenantId, UUID roleId);

    List<FactoryRolePermissionEntity> findByTenantIdAndRoleIdIn(UUID tenantId, Collection<UUID> roleIds);

    @Modifying
    @Query("DELETE FROM FactoryRolePermissionEntity rp WHERE rp.tenantId = :tenantId AND rp.roleId = :roleId")
    void deleteByTenantIdAndRoleId(@Param("tenantId") UUID tenantId, @Param("roleId") UUID roleId);

    @Modifying
    @Query("DELETE FROM FactoryRolePermissionEntity rp WHERE rp.permissionCode = :permissionCode")
    void deleteByPermissionCode(@Param("permissionCode") String permissionCode);

}
