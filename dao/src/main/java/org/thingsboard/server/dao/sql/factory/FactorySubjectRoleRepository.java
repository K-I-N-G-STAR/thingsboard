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
package org.thingsboard.server.dao.sql.factory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.factory.FactorySubjectType;
import org.thingsboard.server.dao.model.sql.factory.FactorySubjectRoleEntity;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface FactorySubjectRoleRepository extends JpaRepository<FactorySubjectRoleEntity, UUID> {

    Page<FactorySubjectRoleEntity> findByTenantIdAndRoleId(UUID tenantId, UUID roleId, Pageable pageable);

    List<FactorySubjectRoleEntity> findByTenantIdAndSubjectTypeAndSubjectIdAndEnabledTrue(UUID tenantId,
                                                                                          FactorySubjectType subjectType,
                                                                                          UUID subjectId);

    @Modifying
    @Query("DELETE FROM FactorySubjectRoleEntity sr WHERE sr.tenantId = :tenantId AND sr.roleId = :roleId " +
            "AND sr.subjectType = :subjectType AND sr.subjectId IN :subjectIds")
    void deleteByTenantIdAndRoleIdAndSubjectTypeAndSubjectIdIn(@Param("tenantId") UUID tenantId,
                                                               @Param("roleId") UUID roleId,
                                                               @Param("subjectType") FactorySubjectType subjectType,
                                                               @Param("subjectIds") Collection<UUID> subjectIds);

    @Modifying
    @Query("DELETE FROM FactorySubjectRoleEntity sr WHERE sr.tenantId = :tenantId AND sr.roleId = :roleId")
    void deleteByTenantIdAndRoleId(@Param("tenantId") UUID tenantId, @Param("roleId") UUID roleId);

}
