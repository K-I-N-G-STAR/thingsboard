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
import org.thingsboard.server.common.data.id.FactoryPermissionId;
import org.thingsboard.server.common.data.validation.Length;
import org.thingsboard.server.common.data.validation.NoXss;

@Data
@Schema
@EqualsAndHashCode(callSuper = true)
public class FactoryPermission extends BaseDataWithAdditionalInfo<FactoryPermissionId> implements HasName {

    private static final long serialVersionUID = 1L;

    @NoXss
    @Length(max = 150, fieldName = "permission code")
    private String permissionCode;
    @NoXss
    @Length(max = 200, fieldName = "permission name")
    private String permissionName;
    @NoXss
    @Length(max = 100, fieldName = "module")
    private String module;
    @NoXss
    @Length(max = 100, fieldName = "resource")
    private String resource;
    @NoXss
    @Length(max = 100, fieldName = "operation")
    private String operation;
    @NoXss
    @Length(max = 1000, fieldName = "description")
    private String description;
    private boolean enabled = true;
    private Integer sortOrder = 0;

    public FactoryPermission() {
        super();
    }

    public FactoryPermission(FactoryPermissionId id) {
        super(id);
    }

    public FactoryPermission(FactoryPermission permission) {
        super(permission);
        this.permissionCode = permission.getPermissionCode();
        this.permissionName = permission.getPermissionName();
        this.module = permission.getModule();
        this.resource = permission.getResource();
        this.operation = permission.getOperation();
        this.description = permission.getDescription();
        this.enabled = permission.isEnabled();
        this.sortOrder = permission.getSortOrder();
    }

    @Schema(description = "Additional parameters of the factory permission.", implementation = JsonNode.class)
    @Override
    public JsonNode getAdditionalInfo() {
        return super.getAdditionalInfo();
    }

    @Override
    public String getName() {
        return permissionName;
    }

}
