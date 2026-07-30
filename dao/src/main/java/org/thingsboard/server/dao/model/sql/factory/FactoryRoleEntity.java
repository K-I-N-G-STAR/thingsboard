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
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.factory.FactoryRole;
import org.thingsboard.server.common.data.factory.FactoryRoleType;
import org.thingsboard.server.common.data.id.FactoryRoleId;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.dao.model.BaseVersionedEntity;
import org.thingsboard.server.dao.util.mapping.JsonConverter;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "factory_role")
public class FactoryRoleEntity extends BaseVersionedEntity<FactoryRole> {

    @Column(name = "updated_time")
    private Long updatedTime;

    @Column(name = "tenant_id")
    private UUID tenantId;

    @Column(name = "role_code")
    private String roleCode;

    @Column(name = "role_name")
    private String roleName;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_type")
    private FactoryRoleType roleType;

    @Enumerated(EnumType.STRING)
    @Column(name = "base_authority")
    private Authority baseAuthority;

    @Column(name = "description")
    private String description;

    @Column(name = "enabled")
    private boolean enabled;

    @Convert(converter = JsonConverter.class)
    @Column(name = "additional_info")
    private JsonNode additionalInfo;

    public FactoryRoleEntity() {
    }

    public FactoryRoleEntity(FactoryRole role) {
        super(role);
        this.updatedTime = role.getUpdatedTime();
        this.tenantId = getTenantUuid(role.getTenantId());
        this.roleCode = role.getRoleCode();
        this.roleName = role.getRoleName();
        this.roleType = role.getRoleType();
        this.baseAuthority = role.getBaseAuthority();
        this.description = role.getDescription();
        this.enabled = role.isEnabled();
        this.additionalInfo = role.getAdditionalInfo();
    }

    @Override
    public FactoryRole toData() {
        FactoryRole role = new FactoryRole(new FactoryRoleId(id));
        role.setCreatedTime(createdTime);
        role.setVersion(version);
        role.setUpdatedTime(updatedTime);
        role.setTenantId(getTenantId(tenantId));
        role.setRoleCode(roleCode);
        role.setRoleName(roleName);
        role.setRoleType(roleType);
        role.setBaseAuthority(baseAuthority);
        role.setDescription(description);
        role.setEnabled(enabled);
        role.setAdditionalInfo(additionalInfo);
        return role;
    }

}
