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
import org.thingsboard.server.common.data.factory.FactorySubjectRole;
import org.thingsboard.server.common.data.factory.FactorySubjectType;
import org.thingsboard.server.common.data.id.FactoryRoleId;
import org.thingsboard.server.common.data.id.FactorySubjectRoleId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.util.mapping.JsonConverter;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "factory_subject_role")
public class FactorySubjectRoleEntity extends BaseSqlEntity<FactorySubjectRole> {

    @Column(name = "tenant_id")
    private UUID tenantId;

    @Enumerated(EnumType.STRING)
    @Column(name = "subject_type")
    private FactorySubjectType subjectType;

    @Column(name = "subject_id")
    private UUID subjectId;

    @Column(name = "role_id")
    private UUID roleId;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "created_by")
    private UUID createdBy;

    @Convert(converter = JsonConverter.class)
    @Column(name = "additional_info")
    private JsonNode additionalInfo;

    public FactorySubjectRoleEntity() {
    }

    public FactorySubjectRoleEntity(FactorySubjectRole subjectRole) {
        super(subjectRole);
        this.tenantId = getTenantUuid(subjectRole.getTenantId());
        this.subjectType = subjectRole.getSubjectType();
        this.subjectId = subjectRole.getSubjectId();
        this.roleId = getUuid(subjectRole.getRoleId());
        this.enabled = subjectRole.isEnabled();
        this.createdBy = getUuid(subjectRole.getCreatedBy());
        this.additionalInfo = subjectRole.getAdditionalInfo();
    }

    @Override
    public FactorySubjectRole toData() {
        FactorySubjectRole subjectRole = new FactorySubjectRole(new FactorySubjectRoleId(id));
        subjectRole.setCreatedTime(createdTime);
        subjectRole.setTenantId(getTenantId(tenantId));
        subjectRole.setSubjectType(subjectType);
        subjectRole.setSubjectId(subjectId);
        subjectRole.setRoleId(getEntityId(roleId, FactoryRoleId::new));
        subjectRole.setEnabled(enabled);
        subjectRole.setCreatedBy(getEntityId(createdBy, UserId::new));
        subjectRole.setAdditionalInfo(additionalInfo);
        return subjectRole;
    }

}
