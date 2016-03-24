/*
 * Copyright (c) 2016, University of Oslo
 *
 * All rights reserved.
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

package org.hisp.dhis.client.sdk.android.common;

import com.raizlabs.android.dbflow.structure.Model;

import org.hisp.dhis.client.sdk.android.api.persistence.flow.DashboardElementFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.DashboardFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.DashboardItemFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.EnrollmentFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.EventFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.InterpretationCommentFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.InterpretationElementFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.InterpretationFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.StateFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.TrackedEntityInstanceFlow;
import org.hisp.dhis.client.sdk.models.common.base.IModel;
import org.hisp.dhis.client.sdk.models.common.state.State;
import org.hisp.dhis.client.sdk.models.dashboard.Dashboard;
import org.hisp.dhis.client.sdk.models.dashboard.DashboardElement;
import org.hisp.dhis.client.sdk.models.dashboard.DashboardItem;
import org.hisp.dhis.client.sdk.models.enrollment.Enrollment;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.interpretation.Interpretation;
import org.hisp.dhis.client.sdk.models.interpretation.InterpretationComment;
import org.hisp.dhis.client.sdk.models.interpretation.InterpretationElement;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityInstance;

import static org.hisp.dhis.client.sdk.models.utils.Preconditions.isNull;


public class StateMapper extends AbsMapper<State, StateFlow> implements IStateMapper {

    @Override
    public StateFlow mapToDatabaseEntity(State state) {
        if (state == null) {
            return null;
        }

        StateFlow stateFlow = new StateFlow();
        stateFlow.setItemId(state.getItemId());
        stateFlow.setItemType(getRelatedModelClass(state.getItemType()));
        stateFlow.setAction(state.getAction());

        return stateFlow;
    }

    @Override
    public State mapToModel(StateFlow stateFlow) {
        if (stateFlow == null) {
            return null;
        }

        State state = new State();
        state.setItemId(stateFlow.getItemId());
        state.setItemType(getRelatedModelClass(stateFlow.getItemType()));
        state.setAction(stateFlow.getAction());

        return state;
    }

    @Override
    public Class<State> getModelTypeClass() {
        return State.class;
    }

    @Override
    public Class<StateFlow> getDatabaseEntityTypeClass() {
        return StateFlow.class;
    }

    @Override
    public Class<? extends IModel> getRelatedModelClass(String type) {
        isNull(type, "type must not be null");

        if (Dashboard.class.getSimpleName().equals(type)) {
            return Dashboard.class;
        }

        if (DashboardItem.class.getSimpleName().equals(type)) {
            return DashboardItem.class;
        }

        if (DashboardElement.class.getSimpleName().equals(type)) {
            return DashboardElement.class;
        }

        if (Interpretation.class.getSimpleName().equals(type)) {
            return Interpretation.class;
        }

        if (InterpretationElement.class.getSimpleName().equals(type)) {
            return InterpretationElement.class;
        }

        if (InterpretationComment.class.getSimpleName().equals(type)) {
            return InterpretationComment.class;
        }

        if (TrackedEntityInstance.class.getSimpleName().equals(type)) {
            return TrackedEntityInstance.class;
        }

        if (Enrollment.class.getSimpleName().equals(type)) {
            return Enrollment.class;
        }

        if (Event.class.getSimpleName().equals(type)) {
            return Event.class;
        }

        throw new IllegalArgumentException("Unsupported type: " + type);
    }

    @Override
    public String getRelatedModelClass(Class<? extends IModel> clazz) {
        isNull(clazz, "clazz must not be null");
        return clazz.toString();
    }

    @Override
    public Class<? extends Model> getRelatedDatabaseEntityClass(Class<? extends IModel>
                                                                            objectClass) {
        isNull(objectClass, "Class object must not be null");

        if (Dashboard.class.equals(objectClass)) {
            return DashboardFlow.class;
        }

        if (DashboardItem.class.equals(objectClass)) {
            return DashboardItemFlow.class;
        }

        if (DashboardElement.class.equals(objectClass)) {
            return DashboardElementFlow.class;
        }

        if (Interpretation.class.equals(objectClass)) {
            return InterpretationFlow.class;
        }

        if (InterpretationElement.class.equals(objectClass)) {
            return InterpretationElementFlow.class;
        }

        if (InterpretationComment.class.equals(objectClass)) {
            return InterpretationCommentFlow.class;
        }

        if (TrackedEntityInstance.class.equals(objectClass)) {
            return TrackedEntityInstanceFlow.class;
        }

        if (Enrollment.class.equals(objectClass)) {
            return EnrollmentFlow.class;
        }

        if (Event.class.equals(objectClass)) {
            return EventFlow.class;
        }

        throw new IllegalArgumentException("Unsupported type: " + objectClass.getSimpleName());
    }
}
