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
package org.thingsboard.server.dao.model.sql.factory;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.factory.FactoryRolePermission;
import org.thingsboard.server.common.data.id.FactoryRoleId;
import org.thingsboard.server.common.data.id.FactoryRolePermissionId;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.util.mapping.JsonConverter;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "factory_role_permission")
public class FactoryRolePermissionEntity extends BaseSqlEntity<FactoryRolePermission> {

    @Column(name = "tenant_id")
    private UUID tenantId;

    @Column(name = "role_id")
    private UUID roleId;

    @Column(name = "permission_code")
    private String permissionCode;

    @Convert(converter = JsonConverter.class)
    @Column(name = "additional_info")
    private JsonNode additionalInfo;

    public FactoryRolePermissionEntity() {
    }

    public FactoryRolePermissionEntity(FactoryRolePermission rolePermission) {
        super(rolePermission);
        this.tenantId = getTenantUuid(rolePermission.getTenantId());
        this.roleId = getUuid(rolePermission.getRoleId());
        this.permissionCode = rolePermission.getPermissionCode();
        this.additionalInfo = rolePermission.getAdditionalInfo();
    }

    @Override
    public FactoryRolePermission toData() {
        FactoryRolePermission rolePermission = new FactoryRolePermission(new FactoryRolePermissionId(id));
        rolePermission.setCreatedTime(createdTime);
        rolePermission.setTenantId(getTenantId(tenantId));
        rolePermission.setRoleId(getEntityId(roleId, FactoryRoleId::new));
        rolePermission.setPermissionCode(permissionCode);
        rolePermission.setAdditionalInfo(additionalInfo);
        return rolePermission;
    }

}
