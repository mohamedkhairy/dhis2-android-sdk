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
package org.hisp.dhis.android.core.period.internal;

import org.hisp.dhis.android.core.period.Period;
import org.hisp.dhis.android.core.period.PeriodType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Calendar;
import java.util.List;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class AbstractPeriodGeneratorShould {

    private final AbstractPeriodGenerator generator;

    public AbstractPeriodGeneratorShould() {
        this.generator = new AbstractPeriodGenerator(Calendar.getInstance(), "yyyy",
                PeriodType.Yearly) {

            @Override
            protected void moveToStartOfCurrentPeriod() {

            }

            @Override
            protected void movePeriods(int number) {

            }
        };
    }

    @Test
    public void generate_one_requested_period() {
        List<Period> periods = generator.generatePeriods(1, 0);
        assertThat(periods.size()).isEqualTo(1);
    }

    @Test
    public void generate_many_requested_period() {
        List<Period> periods = generator.generatePeriods(5, 0);
        assertThat(periods.size()).isEqualTo(5);
    }

    @Test
    public void throw_exception_for_negative_periods() {
        try {
            generator.generatePeriods(-12, 0);
            fail("Exception was expected, but nothing was thrown.");
        } catch (RuntimeException e) {
            // No operation.
        }
    }

    @Test
    public void empty_list_for_zero_periods() {
        List<Period> periods = generator.generatePeriods(0, 0);
        assertThat(periods).isEmpty();
    }
}