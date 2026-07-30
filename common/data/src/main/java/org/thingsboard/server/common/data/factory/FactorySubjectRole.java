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
package org.thingsboard.server.common.data.factory;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.BaseDataWithAdditionalInfo;
import org.thingsboard.server.common.data.HasTenantId;
import org.thingsboard.server.common.data.id.FactoryRoleId;
import org.thingsboard.server.common.data.id.FactorySubjectRoleId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;

import java.util.UUID;

@Data
@Schema
@EqualsAndHashCode(callSuper = true)
public class FactorySubjectRole extends BaseDataWithAdditionalInfo<FactorySubjectRoleId> implements HasTenantId {

    private static final long serialVersionUID = 1L;

    private TenantId tenantId;
    private FactorySubjectType subjectType;
    private UUID subjectId;
    private FactoryRoleId roleId;
    private boolean enabled = true;
    private UserId createdBy;

    public FactorySubjectRole() {
        super();
    }

    public FactorySubjectRole(FactorySubjectRoleId id) {
        super(id);
    }

    public FactorySubjectRole(FactorySubjectRole subjectRole) {
        super(subjectRole);
        this.tenantId = subjectRole.getTenantId();
        this.subjectType = subjectRole.getSubjectType();
        this.subjectId = subjectRole.getSubjectId();
        this.roleId = subjectRole.getRoleId();
        this.enabled = subjectRole.isEnabled();
        this.createdBy = subjectRole.getCreatedBy();
    }

    @Schema(description = "Additional parameters of the subject role assignment.", implementation = JsonNode.class)
    @Override
    public JsonNode getAdditionalInfo() {
        return super.getAdditionalInfo();
    }

}
