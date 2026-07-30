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
import org.thingsboard.server.common.data.id.FactoryRolePermissionId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.validation.Length;
import org.thingsboard.server.common.data.validation.NoXss;

@Data
@Schema
@EqualsAndHashCode(callSuper = true)
public class FactoryRolePermission extends BaseDataWithAdditionalInfo<FactoryRolePermissionId> implements HasTenantId {

    private static final long serialVersionUID = 1L;

    private TenantId tenantId;
    private FactoryRoleId roleId;
    @NoXss
    @Length(max = 150, fieldName = "permission code")
    private String permissionCode;

    public FactoryRolePermission() {
        super();
    }

    public FactoryRolePermission(FactoryRolePermissionId id) {
        super(id);
    }

    public FactoryRolePermission(FactoryRolePermission rolePermission) {
        super(rolePermission);
        this.tenantId = rolePermission.getTenantId();
        this.roleId = rolePermission.getRoleId();
        this.permissionCode = rolePermission.getPermissionCode();
    }

    @Schema(description = "Additional parameters of the role permission assignment.", implementation = JsonNode.class)
    @Override
    public JsonNode getAdditionalInfo() {
        return super.getAdditionalInfo();
    }

}
