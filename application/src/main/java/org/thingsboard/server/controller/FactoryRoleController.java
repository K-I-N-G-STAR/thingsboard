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
package org.thingsboard.server.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.factory.FactoryPermission;
import org.thingsboard.server.common.data.factory.FactoryRole;
import org.thingsboard.server.common.data.factory.FactoryRoleScope;
import org.thingsboard.server.common.data.factory.FactorySubjectRole;
import org.thingsboard.server.common.data.factory.FactorySubjectType;
import org.thingsboard.server.common.data.factory.FactoryUserPermissions;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.FactoryPermissionId;
import org.thingsboard.server.common.data.id.FactoryRoleId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.config.annotations.ApiOperation;
import org.thingsboard.server.dao.factory.FactoryPermissionService;
import org.thingsboard.server.dao.factory.FactoryRoleService;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.entitiy.TbLogEntityActionService;
import org.thingsboard.server.service.security.factory.FactoryAccessService;
import org.thingsboard.server.service.security.model.SecurityUser;

import java.util.List;
import java.util.UUID;

import static org.thingsboard.server.controller.ControllerConstants.PAGE_DATA_PARAMETERS;
import static org.thingsboard.server.controller.ControllerConstants.PAGE_NUMBER_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.PAGE_SIZE_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.SORT_ORDER_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.SORT_PROPERTY_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.TENANT_AUTHORITY_PARAGRAPH;
import static org.thingsboard.server.controller.ControllerConstants.TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH;

@RestController
@TbCoreComponent
@RequiredArgsConstructor
@RequestMapping("/api/factory")
public class FactoryRoleController extends BaseController {

    private final FactoryRoleService factoryRoleService;
    private final FactoryPermissionService factoryPermissionService;
    private final FactoryAccessService factoryAccessService;
    private final TbLogEntityActionService logEntityActionService;

    @ApiOperation(value = "Get current factory permissions (getMyFactoryPermissions)",
            notes = "Returns factory RBAC roles, permission codes and data scopes for current user. " +
                    TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @GetMapping("/me/permissions")
    public FactoryUserPermissions getMyPermissions() throws ThingsboardException {
        SecurityUser user = getCurrentUser();
        return factoryRoleService.findUserPermissions(user.getTenantId(), user.getId(), user.getCustomerId());
    }

    @ApiOperation(value = "Get factory permission dictionary (getFactoryPermissions)",
            notes = "Returns enabled factory permission dictionary. " + TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @GetMapping("/permissions")
    public List<FactoryPermission> getPermissions() {
        return factoryPermissionService.findEnabledPermissions();
    }

    @ApiOperation(value = "Get factory permission page (getFactoryPermissionPage)",
            notes = "Returns a page of factory permission dictionary records. " + PAGE_DATA_PARAMETERS)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @GetMapping("/permissions/page")
    public PageData<FactoryPermission> getPermissionPage(
            @Parameter(description = PAGE_SIZE_DESCRIPTION, required = true)
            @RequestParam int pageSize,
            @Parameter(description = PAGE_NUMBER_DESCRIPTION, required = true)
            @RequestParam int page,
            @RequestParam(required = false) String textSearch,
            @Parameter(description = SORT_PROPERTY_DESCRIPTION, schema = @Schema(allowableValues = {"createdTime", "permissionCode", "permissionName", "module", "sortOrder"}))
            @RequestParam(required = false) String sortProperty,
            @Parameter(description = SORT_ORDER_DESCRIPTION, schema = @Schema(allowableValues = {"ASC", "DESC"}))
            @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
        return factoryPermissionService.findPermissions(pageLink);
    }

    @ApiOperation(value = "Save factory permission (saveFactoryPermission)",
            notes = "Creates or updates factory permission dictionary records.")
    @PreAuthorize("hasAuthority('SYS_ADMIN')")
    @PostMapping("/permissions")
    public FactoryPermission savePermission(@RequestBody @Valid FactoryPermission permission) throws ThingsboardException {
        SecurityUser user = getCurrentUser();
        factoryAccessService.checkPermission(user, "permission:manage");
        ActionType actionType = permission.getId() == null ? ActionType.ADDED : ActionType.UPDATED;
        FactoryPermission savedPermission = factoryPermissionService.savePermission(permission);
        logEntityActionService.logEntityAction(user.getTenantId(), savedPermission.getId(), savedPermission, actionType, user);
        return savedPermission;
    }

    @ApiOperation(value = "Delete factory permission (deleteFactoryPermission)",
            notes = "Deletes a factory permission dictionary record.")
    @PreAuthorize("hasAuthority('SYS_ADMIN')")
    @DeleteMapping("/permissions/{permissionId}")
    @ResponseStatus(HttpStatus.OK)
    public void deletePermission(@PathVariable UUID permissionId) throws ThingsboardException {
        SecurityUser user = getCurrentUser();
        factoryAccessService.checkPermission(user, "permission:manage");
        FactoryPermissionId factoryPermissionId = new FactoryPermissionId(permissionId);
        FactoryPermission permission = checkNotNull(factoryPermissionService.findPermissionById(factoryPermissionId));
        factoryPermissionService.deletePermission(factoryPermissionId);
        logEntityActionService.logEntityAction(user.getTenantId(), factoryPermissionId, permission, ActionType.DELETED, user, factoryPermissionId.toString());
    }

    @ApiOperation(value = "Get factory role by id (getFactoryRoleById)",
            notes = "Fetches a factory role by id. " + TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @GetMapping("/roles/{roleId}")
    public FactoryRole getRoleById(@PathVariable UUID roleId) throws ThingsboardException {
        SecurityUser user = getCurrentUser();
        return checkNotNull(factoryRoleService.findRoleById(user.getTenantId(), new FactoryRoleId(roleId)));
    }

    @ApiOperation(value = "Get factory roles (getFactoryRoles)",
            notes = "Returns a page of factory roles. " + PAGE_DATA_PARAMETERS + TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @GetMapping("/roles")
    public PageData<FactoryRole> getRoles(
            @Parameter(description = PAGE_SIZE_DESCRIPTION, required = true)
            @RequestParam int pageSize,
            @Parameter(description = PAGE_NUMBER_DESCRIPTION, required = true)
            @RequestParam int page,
            @RequestParam(required = false) String textSearch,
            @Parameter(description = SORT_PROPERTY_DESCRIPTION, schema = @Schema(allowableValues = {"createdTime", "updatedTime", "roleCode", "roleName", "roleType", "enabled"}))
            @RequestParam(required = false) String sortProperty,
            @Parameter(description = SORT_ORDER_DESCRIPTION, schema = @Schema(allowableValues = {"ASC", "DESC"}))
            @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        SecurityUser user = getCurrentUser();
        PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
        return factoryRoleService.findRoles(user.getTenantId(), pageLink);
    }

    @ApiOperation(value = "Save factory role (saveFactoryRole)",
            notes = "Creates or updates a factory role. " + TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @PostMapping("/roles")
    public FactoryRole saveRole(@RequestBody @Valid FactoryRole role) throws ThingsboardException {
        SecurityUser user = getCurrentUser();
        factoryAccessService.checkPermission(user, "role:manage");
        ActionType actionType = role.getId() == null ? ActionType.ADDED : ActionType.UPDATED;
        role.setTenantId(user.getTenantId());
        FactoryRole savedRole = factoryRoleService.saveRole(user.getTenantId(), role);
        logEntityActionService.logEntityAction(user.getTenantId(), savedRole.getId(), savedRole, actionType, user);
        return savedRole;
    }

    @ApiOperation(value = "Delete factory role (deleteFactoryRole)",
            notes = "Deletes a factory role and its permission, subject and scope relations. " + TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @DeleteMapping("/roles/{roleId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteRole(@PathVariable UUID roleId) throws ThingsboardException {
        SecurityUser user = getCurrentUser();
        factoryAccessService.checkPermission(user, "role:manage");
        FactoryRoleId factoryRoleId = new FactoryRoleId(roleId);
        FactoryRole role = checkNotNull(factoryRoleService.findRoleById(user.getTenantId(), factoryRoleId));
        factoryRoleService.deleteRole(user.getTenantId(), factoryRoleId);
        logEntityActionService.logEntityAction(user.getTenantId(), factoryRoleId, role, ActionType.DELETED, user, factoryRoleId.toString());
    }

    @ApiOperation(value = "Get factory role permission codes (getFactoryRolePermissionCodes)",
            notes = "Returns permission codes assigned to a factory role. " + TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @GetMapping("/roles/{roleId}/permissions")
    public List<String> getRolePermissionCodes(@PathVariable UUID roleId) throws ThingsboardException {
        SecurityUser user = getCurrentUser();
        FactoryRoleId factoryRoleId = new FactoryRoleId(roleId);
        checkFactoryRole(user, factoryRoleId);
        return factoryRoleService.findRolePermissionCodes(user.getTenantId(), factoryRoleId);
    }

    @ApiOperation(value = "Save factory role permissions (saveFactoryRolePermissions)",
            notes = "Replaces all permission codes assigned to a factory role. " + TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @PostMapping("/roles/{roleId}/permissions")
    @ResponseStatus(HttpStatus.OK)
    public void saveRolePermissions(@PathVariable UUID roleId, @RequestBody List<String> permissionCodes) throws ThingsboardException {
        SecurityUser user = getCurrentUser();
        factoryAccessService.checkPermission(user, "role:manage");
        FactoryRoleId factoryRoleId = new FactoryRoleId(roleId);
        checkFactoryRole(user, factoryRoleId);
        factoryRoleService.saveRolePermissions(user.getTenantId(), factoryRoleId, permissionCodes);
        logRoleUpdated(user, factoryRoleId, "permissions");
    }

    @ApiOperation(value = "Get factory role subjects (getFactoryRoleSubjects)",
            notes = "Returns users or customers assigned to a factory role. " + PAGE_DATA_PARAMETERS + TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @GetMapping("/roles/{roleId}/subjects")
    public PageData<FactorySubjectRole> getRoleSubjects(
            @PathVariable UUID roleId,
            @Parameter(description = PAGE_SIZE_DESCRIPTION, required = true)
            @RequestParam int pageSize,
            @Parameter(description = PAGE_NUMBER_DESCRIPTION, required = true)
            @RequestParam int page,
            @RequestParam(required = false) String textSearch,
            @Parameter(description = SORT_PROPERTY_DESCRIPTION, schema = @Schema(allowableValues = {"createdTime", "subjectType", "subjectId", "enabled"}))
            @RequestParam(required = false) String sortProperty,
            @Parameter(description = SORT_ORDER_DESCRIPTION, schema = @Schema(allowableValues = {"ASC", "DESC"}))
            @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        SecurityUser user = getCurrentUser();
        FactoryRoleId factoryRoleId = new FactoryRoleId(roleId);
        checkFactoryRole(user, factoryRoleId);
        PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
        return factoryRoleService.findRoleSubjects(user.getTenantId(), factoryRoleId, pageLink);
    }

    @ApiOperation(value = "Assign subjects to factory role (assignFactoryRoleSubjects)",
            notes = "Assigns users or customers to a factory role. " + TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @PostMapping("/roles/{roleId}/subjects")
    @ResponseStatus(HttpStatus.OK)
    public void assignSubjects(@PathVariable UUID roleId, @RequestBody List<FactorySubjectRole> subjects) throws ThingsboardException {
        SecurityUser user = getCurrentUser();
        factoryAccessService.checkPermission(user, "role:manage");
        FactoryRoleId factoryRoleId = new FactoryRoleId(roleId);
        checkFactoryRole(user, factoryRoleId);
        if (subjects != null) {
            subjects.forEach(subject -> subject.setCreatedBy(user.getId()));
        }
        factoryRoleService.assignSubjects(user.getTenantId(), factoryRoleId, subjects);
        logRoleUpdated(user, factoryRoleId, "subjects");
    }

    @ApiOperation(value = "Unassign subjects from factory role (unassignFactoryRoleSubjects)",
            notes = "Removes user or customer assignments from a factory role. " + TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @PostMapping("/roles/{roleId}/subjects/{subjectType}/unassign")
    @ResponseStatus(HttpStatus.OK)
    public void unassignSubjects(@PathVariable UUID roleId,
                                 @PathVariable FactorySubjectType subjectType,
                                 @RequestBody List<UUID> subjectIds) throws ThingsboardException {
        SecurityUser user = getCurrentUser();
        factoryAccessService.checkPermission(user, "role:manage");
        FactoryRoleId factoryRoleId = new FactoryRoleId(roleId);
        checkFactoryRole(user, factoryRoleId);
        factoryRoleService.unassignSubjects(user.getTenantId(), factoryRoleId, subjectType, subjectIds);
        logRoleUpdated(user, factoryRoleId, "subjects");
    }

    @ApiOperation(value = "Get factory role scopes (getFactoryRoleScopes)",
            notes = "Returns data scopes assigned to a factory role. " + TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @GetMapping("/roles/{roleId}/scopes")
    public List<FactoryRoleScope> getRoleScopes(@PathVariable UUID roleId) throws ThingsboardException {
        SecurityUser user = getCurrentUser();
        FactoryRoleId factoryRoleId = new FactoryRoleId(roleId);
        checkFactoryRole(user, factoryRoleId);
        return factoryRoleService.findRoleScopes(user.getTenantId(), factoryRoleId);
    }

    @ApiOperation(value = "Save factory role scopes (saveFactoryRoleScopes)",
            notes = "Replaces data scopes assigned to a factory role. " + TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @PostMapping("/roles/{roleId}/scopes")
    @ResponseStatus(HttpStatus.OK)
    public void saveRoleScopes(@PathVariable UUID roleId, @RequestBody List<FactoryRoleScope> scopes) throws ThingsboardException {
        SecurityUser user = getCurrentUser();
        factoryAccessService.checkPermission(user, "role:manage");
        FactoryRoleId factoryRoleId = new FactoryRoleId(roleId);
        checkFactoryRole(user, factoryRoleId);
        factoryRoleService.saveRoleScopes(user.getTenantId(), factoryRoleId, scopes);
        logRoleUpdated(user, factoryRoleId, "scopes");
    }

    @ApiOperation(value = "Get subject factory roles (getSubjectFactoryRoles)",
            notes = "Returns factory role assignments for a user or customer. " + TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @GetMapping("/subjects/{subjectType}/{subjectId}/roles")
    public List<FactorySubjectRole> getSubjectRoles(@PathVariable FactorySubjectType subjectType,
                                                    @PathVariable UUID subjectId) throws ThingsboardException {
        SecurityUser user = getCurrentUser();
        return factoryRoleService.findSubjectRoles(user.getTenantId(), subjectType, subjectId);
    }

    @ApiOperation(value = "Get subject factory scopes (getSubjectFactoryScopes)",
            notes = "Returns direct data scopes assigned to a user or customer. " + TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @GetMapping("/subjects/{subjectType}/{subjectId}/scopes")
    public List<FactoryRoleScope> getSubjectScopes(@PathVariable FactorySubjectType subjectType,
                                                   @PathVariable UUID subjectId) throws ThingsboardException {
        SecurityUser user = getCurrentUser();
        return factoryRoleService.findSubjectScopes(user.getTenantId(), subjectType, subjectId);
    }

    @ApiOperation(value = "Save subject factory scopes (saveSubjectFactoryScopes)",
            notes = "Replaces direct data scopes assigned to a user or customer. " + TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @PostMapping("/subjects/{subjectType}/{subjectId}/scopes")
    @ResponseStatus(HttpStatus.OK)
    public void saveSubjectScopes(@PathVariable FactorySubjectType subjectType,
                                  @PathVariable UUID subjectId,
                                  @RequestBody List<FactoryRoleScope> scopes) throws ThingsboardException {
        SecurityUser user = getCurrentUser();
        factoryAccessService.checkPermission(user, "role:manage");
        factoryRoleService.saveSubjectScopes(user.getTenantId(), subjectType, subjectId, scopes);
        logSubjectScopesUpdated(user, subjectType, subjectId);
    }

    private void logRoleUpdated(SecurityUser user, FactoryRoleId roleId, String updatedSection) throws ThingsboardException {
        FactoryRole role = checkFactoryRole(user, roleId);
        logEntityActionService.logEntityAction(user.getTenantId(), roleId, role, ActionType.UPDATED, user, roleId.toString(), updatedSection);
    }

    private FactoryRole checkFactoryRole(SecurityUser user, FactoryRoleId roleId) throws ThingsboardException {
        return checkNotNull(factoryRoleService.findRoleById(user.getTenantId(), roleId));
    }

    private void logSubjectScopesUpdated(SecurityUser user, FactorySubjectType subjectType, UUID subjectId) {
        logEntityActionService.logEntityAction(user.getTenantId(), toSubjectEntityId(subjectType, subjectId),
                ActionType.UPDATED, user, null, "scopes");
    }

    private EntityId toSubjectEntityId(FactorySubjectType subjectType, UUID subjectId) {
        return switch (subjectType) {
            case USER -> new UserId(subjectId);
            case CUSTOMER -> new CustomerId(subjectId);
        };
    }

}
