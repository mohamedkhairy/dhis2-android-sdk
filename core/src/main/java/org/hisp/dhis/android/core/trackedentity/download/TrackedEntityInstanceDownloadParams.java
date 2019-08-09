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

package org.hisp.dhis.android.core.trackedentity.download;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.dateformat.internal.SafeDateFormat;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeFilterItem;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@AutoValue
public abstract class TrackedEntityInstanceDownloadParams {

    public static class QueryParams {
        public static final String PROGRAM = "program";
        public static final String PROGRAM_STATUS = "programStatus";
        public static final String PROGRAM_START_DATE = "programStartDate";
        public static final String PROGRAM_END_DATE = "programEndDate";
        public static final String ORG_UNITS = "ou";
        public static final String ORG_UNIT_MODE = "ouMode";
        public static final String TRACKED_ENTITY_TYPE = "trackedEntityType";
        public static final String LIMIT_BY_ORGUNIT = "limitByOrgunit";
        public static final String LIMIT_BY_PROGRAM = "limitByProgram";
        public static final String LIMIT = "limit";
    }

    private static Integer DEFAULT_LIMIT = 500;

    @NonNull
    public abstract List<String> orgUnits();

    @Nullable
    public abstract OrganisationUnitMode orgUnitMode();

    @Nullable
    public abstract String program();

    @Nullable
    public abstract EnrollmentStatus programStatus();

    @Nullable
    public abstract Date programStartDate();

    @Nullable
    public abstract Date programEndDate();

    @Nullable
    public abstract String trackedEntityType();

    @NonNull
    public abstract Boolean limitByOrgunit();

    @NonNull
    public abstract Boolean limitByProgram();

    @NonNull
    public abstract Integer limit();

    public static TrackedEntityInstanceDownloadParams fromRepositoryScope(RepositoryScope scope) {
        Builder builder = builder();
        for (RepositoryScopeFilterItem item : scope.filters()) {
            switch (item.key()) {
                case QueryParams.PROGRAM:
                    builder.program(item.value());
                    break;
                case QueryParams.LIMIT_BY_ORGUNIT:
                    builder.limitByOrgunit(item.value().equals("1"));
                    break;
                case QueryParams.LIMIT_BY_PROGRAM:
                    builder.limitByProgram(item.value().equals("1"));
                    break;
                case QueryParams.LIMIT:
                    builder.limit(Integer.parseInt(item.value()));
                    break;
                default:
            }
        }
        return builder.build();
    }

    public static Builder builder() {
        return new AutoValue_TrackedEntityInstanceDownloadParams.Builder()
                .limitByOrgunit(false).limitByProgram(false).limit(DEFAULT_LIMIT)
                .orgUnits(Collections.emptyList());
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder orgUnits(List<String> orgUnits);

        public abstract Builder orgUnitMode(OrganisationUnitMode orgUnitMode);

        public abstract Builder program(String program);

        public abstract Builder programStatus(EnrollmentStatus enrollmentStatus);

        public abstract Builder programStartDate(Date programStartDate);

        public abstract Builder programEndDate(Date programEndDate);

        public abstract Builder trackedEntityType(String trackedEntityType);

        public abstract Builder limitByProgram(Boolean limitByProgram);

        public abstract Builder limitByOrgunit(Boolean limitByOrgunit);

        public abstract Builder limit(Integer limit);

        public abstract TrackedEntityInstanceDownloadParams build();
    }
}