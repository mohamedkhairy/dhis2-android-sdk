package org.hisp.dhis.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Update;

import org.hisp.dhis.android.sdk.controllers.datavalues.DataValueController;
import org.hisp.dhis.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis.android.sdk.persistence.Dhis2Database;
import org.hisp.dhis.android.sdk.utils.Utils;

import java.io.Serializable;

/**
 * @author Simen Skogly Russnes on 03.03.15.
 */
@Table(databaseName = Dhis2Database.NAME)
public class TrackedEntityAttributeValue extends BaseValue implements Serializable {
    private static final String CLASS_TAG = TrackedEntityAttributeValue.class.getSimpleName();

    @JsonProperty("attribute")
    @Column(name = "trackedEntityAttributeId")
    @PrimaryKey
    String trackedEntityAttributeId;

    @JsonIgnore
    @Column(name = "trackedEntityInstanceId")
    @PrimaryKey
    String trackedEntityInstanceId;

    @JsonIgnore
    @Column(name = "localTrackedEntityInstanceId")
    long localTrackedEntityInstanceId;

    /**
     * workaround for sending code if attribute is option set.
     *
     * @return String value.
     */
    @JsonProperty("value")
    public String getValue() {
        TrackedEntityAttribute tea = MetaDataController.
                getTrackedEntityAttribute(trackedEntityAttributeId);
        if (tea.getValueType().equals(TrackedEntityAttribute.TYPE_OPTION_SET)) {
            OptionSet optionSet = MetaDataController.getOptionSet(tea.getOptionSet());
            if (optionSet == null) return "";
            for (Option o : optionSet.getOptions()) {
                if (o.name.equals(value)) {
                    return o.getCode();
                }
            }
        } else return value;
        return null;
    }

    @Override
    public void save() {
        if (Utils.isLocal(trackedEntityInstanceId) && DataValueController.
                getTrackedEntityAttributeValue(trackedEntityAttributeId,
                        localTrackedEntityInstanceId) != null) {
            //to avoid overwriting UID from server due to race conditions with autosyncing with server
            //we only update the value (ie and not the other fields) if the currently in-memory event UID is locally created
            updateManually();
        } else {
            super.save();
        }
    }


    public void updateManually() {
        new Update(TrackedEntityAttributeValue.class).set(
                Condition.column(TrackedEntityAttributeValue$Table.VALUE).is(value))
                .where(Condition.column(TrackedEntityAttributeValue$Table.LOCALTRACKEDENTITYINSTANCEID).is(localTrackedEntityInstanceId),
                        Condition.column(TrackedEntityAttributeValue$Table.TRACKEDENTITYATTRIBUTEID).is(trackedEntityAttributeId)).queryClose();
    }

    @Override
    public void update() {
        save();
    }

    public String getTrackedEntityAttributeId() {
        return trackedEntityAttributeId;
    }

    public void setTrackedEntityAttributeId(String trackedEntityAttributeId) {
        this.trackedEntityAttributeId = trackedEntityAttributeId;
    }

    public String getTrackedEntityInstanceId() {
        return trackedEntityInstanceId;
    }

    public void setTrackedEntityInstanceId(String trackedEntityInstanceId) {
        this.trackedEntityInstanceId = trackedEntityInstanceId;
    }

    public long getLocalTrackedEntityInstanceId() {
        return localTrackedEntityInstanceId;
    }

    public void setLocalTrackedEntityInstanceId(long localTrackedEntityInstanceId) {
        this.localTrackedEntityInstanceId = localTrackedEntityInstanceId;
    }
}
