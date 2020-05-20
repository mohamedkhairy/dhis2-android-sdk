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
package org.hisp.dhis.android.localanalytics

import org.hisp.dhis.android.core.arch.helpers.UidGeneratorImpl
import org.hisp.dhis.android.core.data.datavalue.DataValueSamples
import org.hisp.dhis.android.core.data.enrollment.EnrollmentSamples
import org.hisp.dhis.android.core.data.trackedentity.EventSamples
import org.hisp.dhis.android.core.data.trackedentity.TrackedEntityInstanceSamples
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import java.util.*
import kotlin.random.Random

internal class LocalAnalyticsDataGenerator(private val params: LocalAnalyticsDataParams) {

    private val random = Random(132214235)
    private val uidGenerator = UidGeneratorImpl()

    fun generateDataValues(metadata: MetadataForDataFilling): List<DataValue> {
        val level3OrgUnits = metadata.organisationUnits.filter { ou -> ou.level() == 3 }
        return (1..params.trackedEntityInstances).map { i ->
            val ou = level3OrgUnits[i % level3OrgUnits.size]
            val period = metadata.periods[i % metadata.periods.size]
            val coc = metadata.categoryOptionCombos[i % metadata.categoryOptionCombos.size]
            val dataElements = metadata.aggregatedDataElements.filter { de -> de.categoryCombo() == coc.categoryCombo() }
            DataValueSamples.getDataValue(ou.uid(), dataElements.random().uid(), period.periodId()!!, coc.uid(),
                    metadata.categoryOptionCombos.first().uid())
        }
    }

    fun generateTrackedEntityInstances(organisationUnits: List<OrganisationUnit>): List<TrackedEntityInstance> {
        val level3OrgUnits = organisationUnits.filter { ou -> ou.level() == 3 }
        return (1..params.trackedEntityInstances).map { i ->
            val ou = level3OrgUnits[i % level3OrgUnits.size]
            TrackedEntityInstanceSamples.get(ou.uid())
        }
    }

    fun generateEnrollments(teis: List<TrackedEntityInstance>, program: Program): List<Enrollment> {
        return teis.map { tei ->
            EnrollmentSamples.get(uidGenerator.generate(), tei.organisationUnit(), program.uid(), tei.uid(), getRandomDateInLastYear())
        }
    }

    fun generateEventsWithoutRegistration(metadata: MetadataForDataFilling): List<Event> {
        val level3OrgUnits = metadata.organisationUnits.filter { ou -> ou.level() == 3 }
        val program = metadata.programs[1]
        val programStages = metadata.programStages.filter { ps -> ps.program()!!.uid() == program.uid() }
        return (1..params.eventsWithoutRegistration).map { i ->
            val ou = level3OrgUnits[i % level3OrgUnits.size]
            val programStage = programStages[i % programStages.size]
            EventSamples.get(uidGenerator.generate(), null, ou.uid(), programStage.program()!!.uid(), programStage.uid(),
                    metadata.categoryOptionCombos.first().uid(), getRandomDateInLastYear())
        }
    }

    private fun getRandomDateInLastYear(): Date {
        val now = System.currentTimeMillis()
        val oneYearMillis = 365L * 24 * 60 * 60 * 1000
        val millis = now - random.nextDouble() * oneYearMillis
        return Date(millis.toLong())
    }
}