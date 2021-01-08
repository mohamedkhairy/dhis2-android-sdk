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

package org.hisp.dhis.android.core.arch.repositories.filters.internal;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.arch.helpers.DateUtils;
import org.hisp.dhis.android.core.arch.repositories.collection.BaseRepository;
import org.hisp.dhis.android.core.common.DateFilterPeriod;
import org.hisp.dhis.android.core.common.DatePeriodType;
import org.hisp.dhis.android.core.period.DatePeriod;
import org.hisp.dhis.android.core.period.Period;

import java.util.Date;

public final class PeriodFilterConnector<R extends BaseRepository> {

    private final ScopedRepositoryFilterFactory<R, DateFilterPeriod> repositoryFactory;

    PeriodFilterConnector(ScopedRepositoryFilterFactory<R, DateFilterPeriod> repositoryFactory) {
        this.repositoryFactory = repositoryFactory;
    }

    /**
     * Returns a new repository whose scope is the one of the current repository plus the new filter being applied.
     * The before filter checks if the given field has a date value which is before to the one provided.
     * @param value value to compare with the target field
     * @return the new repository
     */
    public R before(Date value) {
        String dateStr = DateUtils.DATE_FORMAT.format(value);
        DateFilterPeriod filter = DateFilterPeriod.builder().endDate(dateStr).type(DatePeriodType.ABSOLUTE).build();
        return repositoryFactory.updated(filter);
    }

    /**
     * Returns a new repository whose scope is the one of the current repository plus the new filter being applied.
     * The after filter checks if the given field has a date value which is after to the one provided.
     * @param value value to compare with the target field
     * @return the new repository
     */
    public R after(Date value) {
        String dateStr = DateUtils.DATE_FORMAT.format(value);
        DateFilterPeriod filter = DateFilterPeriod.builder().startDate(dateStr).type(DatePeriodType.ABSOLUTE).build();
        return repositoryFactory.updated(filter);
    }

    /**
     * Returns a new repository whose scope is the one of the current repository plus the new filter being applied.
     * The inDatePeriod filter checks if the given field has a date value which is within the provided DatePeriod.
     * @param datePeriod date period to compare with the target field
     * @return the new repository
     */
    public R inDatePeriod(@NonNull DatePeriod datePeriod) {
        return inPeriod(datePeriod.startDate(), datePeriod.endDate());
    }

    /**
     * Returns a new repository whose scope is the one of the current repository plus the new filter being applied.
     * The inPeriod filter checks if the given field has a date value which is within the provided Period.
     * @param period period to compare with the target field
     * @return the new repository
     */
    public R inPeriod(@NonNull Period period) {
        return inPeriod(period.startDate(), period.endDate());
    }

    private R inPeriod(@NonNull Date startDate, @NonNull Date endDate) {
        String startDateStr = DateUtils.DATE_FORMAT.format(startDate);
        String endDateStr = DateUtils.DATE_FORMAT.format(endDate);
        DateFilterPeriod filter = DateFilterPeriod.builder()
                .startDate(startDateStr).endDate(endDateStr).type(DatePeriodType.ABSOLUTE).build();
        return repositoryFactory.updated(filter);
    }
}