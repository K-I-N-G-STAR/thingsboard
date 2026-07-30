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
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.factory.FactoryRoleScope;
import org.thingsboard.server.common.data.factory.FactoryRoleScopeTargetType;
import org.thingsboard.server.common.data.factory.FactoryScopeType;
import org.thingsboard.server.common.data.id.FactoryRoleScopeId;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.util.mapping.JsonConverter;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "factory_role_scope")
public class FactoryRoleScopeEntity extends BaseSqlEntity<FactoryRoleScope> {

    @Column(name = "tenant_id")
    private UUID tenantId;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type")
    private FactoryRoleScopeTargetType targetType;

    @Column(name = "target_id")
    private UUID targetId;

    @Enumerated(EnumType.STRING)
    @Column(name = "scope_type")
    private FactoryScopeType scopeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type")
    private EntityType entityType;

    @Column(name = "entity_id")
    private UUID entityId;

    @Convert(converter = JsonConverter.class)
    @Column(name = "additional_info")
    private JsonNode additionalInfo;

    public FactoryRoleScopeEntity() {
    }

    public FactoryRoleScopeEntity(FactoryRoleScope scope) {
        super(scope);
        this.tenantId = getTenantUuid(scope.getTenantId());
        this.targetType = scope.getTargetType();
        this.targetId = scope.getTargetId();
        this.scopeType = scope.getScopeType();
        this.entityType = scope.getEntityType();
        this.entityId = scope.getEntityId();
        this.additionalInfo = scope.getAdditionalInfo();
    }

    @Override
    public FactoryRoleScope toData() {
        FactoryRoleScope scope = new FactoryRoleScope(new FactoryRoleScopeId(id));
        scope.setCreatedTime(createdTime);
        scope.setTenantId(getTenantId(tenantId));
        scope.setTargetType(targetType);
        scope.setTargetId(targetId);
        scope.setScopeType(scopeType);
        scope.setEntityType(entityType);
        scope.setEntityId(entityId);
        scope.setAdditionalInfo(additionalInfo);
        return scope;
    }

}
