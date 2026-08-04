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

public final class FactoryPermissionCodes {

    public static final String ROLE_READ = "role:read";
    public static final String ROLE_MANAGE = "role:manage";
    public static final String PERMISSION_MANAGE = "permission:manage";

    public static final String ATTRIBUTE_READ = "attribute:read";
    public static final String ATTRIBUTE_WRITE = "attribute:write";

    public static final String TELEMETRY_READ = "telemetry:read";
    public static final String TELEMETRY_WRITE = "telemetry:write";

    public static final String USER_READ = "user:read";
    public static final String USER_MANAGE = "user:manage";

    public static final String CUSTOMER_READ = "customer:read";
    public static final String CUSTOMER_WRITE = "customer:write";
    public static final String CUSTOMER_DELETE = "customer:delete";

    public static final String DEVICE_READ = "device:read";
    public static final String DEVICE_WRITE = "device:write";
    public static final String DEVICE_DELETE = "device:delete";
    public static final String DEVICE_ASSIGN = "device:assign";
    public static final String DEVICE_CREDENTIALS = "device:credentials";

    public static final String DEVICE_PROFILE_READ = "device_profile:read";
    public static final String DEVICE_PROFILE_WRITE = "device_profile:write";
    public static final String DEVICE_PROFILE_DELETE = "device_profile:delete";

    public static final String ASSET_READ = "asset:read";
    public static final String ASSET_WRITE = "asset:write";
    public static final String ASSET_DELETE = "asset:delete";
    public static final String ASSET_ASSIGN = "asset:assign";

    public static final String ASSET_PROFILE_READ = "asset_profile:read";
    public static final String ASSET_PROFILE_WRITE = "asset_profile:write";
    public static final String ASSET_PROFILE_DELETE = "asset_profile:delete";

    public static final String DASHBOARD_READ = "dashboard:read";
    public static final String DASHBOARD_WRITE = "dashboard:write";
    public static final String DASHBOARD_DELETE = "dashboard:delete";
    public static final String DASHBOARD_ASSIGN = "dashboard:assign";

    public static final String RELATION_READ = "relation:read";
    public static final String RELATION_WRITE = "relation:write";

    public static final String ALARM_READ = "alarm:read";
    public static final String ALARM_WRITE = "alarm:write";
    public static final String ALARM_DELETE = "alarm:delete";
    public static final String ALARM_ACK = "alarm:ack";
    public static final String ALARM_CLEAR = "alarm:clear";
    public static final String ALARM_ASSIGN = "alarm:assign";
    public static final String ALARM_COMMENT_READ = "alarm_comment:read";
    public static final String ALARM_COMMENT_WRITE = "alarm_comment:write";

    public static final String RECIPE_READ = "recipe:read";
    public static final String RECIPE_CREATE = "recipe:create";
    public static final String RECIPE_UPDATE = "recipe:update";
    public static final String RECIPE_DELETE = "recipe:delete";
    public static final String RECIPE_APPROVE = "recipe:approve";
    public static final String RECIPE_DISPATCH = "recipe:dispatch";

    public static final String BATCH_READ = "batch:read";
    public static final String BATCH_OPERATE = "batch:operate";

    public static final String PRODUCTION_READ = "production:read";
    public static final String PRODUCTION_SCHEDULE = "production:schedule";

    public static final String REPORT_READ = "report:read";

    public static final String RULECHAIN_READ = "rulechain:read";
    public static final String RULECHAIN_MANAGE = "rulechain:manage";
    public static final String RULEENGINE_CALL = "ruleengine:call";

    public static final String CALCULATED_FIELD_READ = "calculated_field:read";
    public static final String CALCULATED_FIELD_WRITE = "calculated_field:write";
    public static final String CALCULATED_FIELD_DELETE = "calculated_field:delete";
    public static final String CALCULATED_FIELD_TEST = "calculated_field:test";

    public static final String ALARM_RULE_READ = "alarm_rule:read";
    public static final String ALARM_RULE_WRITE = "alarm_rule:write";
    public static final String ALARM_RULE_DELETE = "alarm_rule:delete";
    public static final String ALARM_RULE_TEST = "alarm_rule:test";

    public static final String OTA_MANAGE = "ota:manage";
    public static final String API_KEY_MANAGE = "api_key:manage";

    public static final String MACHINE_CONTROL = "machine:control";
    public static final String MACHINE_PARAM_WRITE = "machine:param_write";
    public static final String MACHINE_EMERGENCY_STOP = "machine:emergency_stop";

    private FactoryPermissionCodes() {
    }

}
