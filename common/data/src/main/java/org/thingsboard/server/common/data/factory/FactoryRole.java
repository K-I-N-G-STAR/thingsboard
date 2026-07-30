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
import org.thingsboard.server.common.data.HasName;
import org.thingsboard.server.common.data.HasTenantId;
import org.thingsboard.server.common.data.HasVersion;
import org.thingsboard.server.common.data.id.FactoryRoleId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.common.data.validation.Length;
import org.thingsboard.server.common.data.validation.NoXss;

@Data
@Schema
@EqualsAndHashCode(callSuper = true)
public class FactoryRole extends BaseDataWithAdditionalInfo<FactoryRoleId> implements HasName, HasTenantId, HasVersion {

    private static final long serialVersionUID = 1L;

    private Long updatedTime;
    private TenantId tenantId;
    @NoXss
    @Length(max = 100, fieldName = "role code")
    private String roleCode;
    @NoXss
    @Length(max = 200, fieldName = "role name")
    private String roleName;
    private FactoryRoleType roleType = FactoryRoleType.CUSTOM;
    private Authority baseAuthority;
    @NoXss
    @Length(max = 1000, fieldName = "description")
    private String description;
    private boolean enabled = true;
    private Long version = 1L;

    public FactoryRole() {
        super();
    }

    public FactoryRole(FactoryRoleId id) {
        super(id);
    }

    public FactoryRole(FactoryRole role) {
        super(role);
        this.updatedTime = role.getUpdatedTime();
        this.tenantId = role.getTenantId();
        this.roleCode = role.getRoleCode();
        this.roleName = role.getRoleName();
        this.roleType = role.getRoleType();
        this.baseAuthority = role.getBaseAuthority();
        this.description = role.getDescription();
        this.enabled = role.isEnabled();
        this.version = role.getVersion();
    }

    @Schema(description = "Additional parameters of the factory role.", implementation = JsonNode.class)
    @Override
    public JsonNode getAdditionalInfo() {
        return super.getAdditionalInfo();
    }

    @Override
    public String getName() {
        return roleName;
    }

}
