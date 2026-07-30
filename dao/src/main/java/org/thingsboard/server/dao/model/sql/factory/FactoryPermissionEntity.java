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
import org.thingsboard.server.common.data.factory.FactoryPermission;
import org.thingsboard.server.common.data.id.FactoryPermissionId;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.util.mapping.JsonConverter;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "factory_permission")
public class FactoryPermissionEntity extends BaseSqlEntity<FactoryPermission> {

    @Column(name = "permission_code")
    private String permissionCode;

    @Column(name = "permission_name")
    private String permissionName;

    @Column(name = "module")
    private String module;

    @Column(name = "resource")
    private String resource;

    @Column(name = "operation")
    private String operation;

    @Column(name = "description")
    private String description;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Convert(converter = JsonConverter.class)
    @Column(name = "additional_info")
    private JsonNode additionalInfo;

    public FactoryPermissionEntity() {
    }

    public FactoryPermissionEntity(FactoryPermission permission) {
        super(permission);
        this.permissionCode = permission.getPermissionCode();
        this.permissionName = permission.getPermissionName();
        this.module = permission.getModule();
        this.resource = permission.getResource();
        this.operation = permission.getOperation();
        this.description = permission.getDescription();
        this.enabled = permission.isEnabled();
        this.sortOrder = permission.getSortOrder();
        this.additionalInfo = permission.getAdditionalInfo();
    }

    @Override
    public FactoryPermission toData() {
        FactoryPermission permission = new FactoryPermission(new FactoryPermissionId(id));
        permission.setCreatedTime(createdTime);
        permission.setPermissionCode(permissionCode);
        permission.setPermissionName(permissionName);
        permission.setModule(module);
        permission.setResource(resource);
        permission.setOperation(operation);
        permission.setDescription(description);
        permission.setEnabled(enabled);
        permission.setSortOrder(sortOrder);
        permission.setAdditionalInfo(additionalInfo);
        return permission;
    }

}
