/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.organisationunit;

import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyIdentifiableCollectionRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.DateFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.IntegerFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkTableInfo;

import java.util.Collections;
import java.util.Map;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public final class OrganisationUnitCollectionRepository
        extends ReadOnlyIdentifiableCollectionRepositoryImpl<OrganisationUnit, OrganisationUnitCollectionRepository> {

    @Inject
    OrganisationUnitCollectionRepository(final IdentifiableObjectStore<OrganisationUnit> store,
                                         final Map<String, ChildrenAppender<OrganisationUnit>> childrenAppenders,
                                         final RepositoryScope scope) {
        super(store, childrenAppenders, scope, new FilterConnectorFactory<>(scope,
                s -> new OrganisationUnitCollectionRepository(store, childrenAppenders, s)));
    }

    public StringFilterConnector<OrganisationUnitCollectionRepository> byParentUid() {
        return cf.string(OrganisationUnitFields.PARENT);
    }

    public StringFilterConnector<OrganisationUnitCollectionRepository> byPath() {
        return cf.string(OrganisationUnitFields.PATH);
    }

    public DateFilterConnector<OrganisationUnitCollectionRepository> byOpeningDate() {
        return cf.date(OrganisationUnitFields.OPENING_DATE);
    }

    public DateFilterConnector<OrganisationUnitCollectionRepository> byClosedDate() {
        return cf.date(OrganisationUnitFields.CLOSED_DATE);
    }

    public IntegerFilterConnector<OrganisationUnitCollectionRepository> byLevel() {
        return cf.integer(OrganisationUnitFields.LEVEL);
    }

    public StringFilterConnector<OrganisationUnitCollectionRepository> byDisplayNamePath() {
        return cf.string(OrganisationUnitTableInfo.Columns.DISPLAY_NAME_PATH);
    }

    public OrganisationUnitCollectionRepository byOrganisationUnitScope(OrganisationUnit.Scope scope) {
        return cf.subQuery(BaseIdentifiableObjectModel.Columns.UID).inLinkTable(
                UserOrganisationUnitLinkTableInfo.TABLE_INFO.name(),
                UserOrganisationUnitLinkTableInfo.Columns.ORGANISATION_UNIT,
                UserOrganisationUnitLinkTableInfo.Columns.ORGANISATION_UNIT_SCOPE,
                Collections.singletonList(scope.name()));
    }

    public OrganisationUnitCollectionRepository byRootOrganisationUnit(Boolean isRoot) {
        return cf.subQuery(BaseIdentifiableObjectModel.Columns.UID).inLinkTable(
                UserOrganisationUnitLinkTableInfo.TABLE_INFO.name(),
                UserOrganisationUnitLinkTableInfo.Columns.ORGANISATION_UNIT,
                UserOrganisationUnitLinkTableInfo.Columns.ROOT,
                Collections.singletonList(isRoot ? "1" : "0"));
    }

    public OrganisationUnitCollectionRepository withPrograms() {
        return cf.withChild(OrganisationUnitFields.PROGRAMS);
    }

    public OrganisationUnitCollectionRepository withDataSets() {
        return cf.withChild(OrganisationUnitFields.DATA_SETS);
    }

    public OrganisationUnitCollectionRepository withOrganisationUnitGroups() {
        return cf.withChild(OrganisationUnitFields.ORGANISATION_UNIT_GROUPS);
    }
}