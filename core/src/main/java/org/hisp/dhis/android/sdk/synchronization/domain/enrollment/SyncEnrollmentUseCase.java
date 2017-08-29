package org.hisp.dhis.android.sdk.synchronization.domain.enrollment;


import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.synchronization.domain.event.EventSynchronizer;
import org.hisp.dhis.android.sdk.synchronization.domain.event.IEventRepository;
import org.hisp.dhis.android.sdk.synchronization.domain.faileditem.IFailedItemRepository;

public class SyncEnrollmentUseCase {
    //coordinate items to sync

    IEnrollmentRepository mEnrollmentRepository;
    IEventRepository mEventRepository;
    IFailedItemRepository mFailedItemRepository;
    EnrollmentSynchronizer mEnrollmentSynchronizer;
    EventSynchronizer mEventSynchronizer;


    public SyncEnrollmentUseCase(IEnrollmentRepository enrollmentRepository, IEventRepository eventRepository,
            IFailedItemRepository failedItemRepository) {
        mEnrollmentRepository = enrollmentRepository;
        mFailedItemRepository = failedItemRepository;
        mEventRepository = eventRepository;
        mEnrollmentSynchronizer = new EnrollmentSynchronizer(mEnrollmentRepository, eventRepository, mFailedItemRepository);
        mEventSynchronizer = new EventSynchronizer(mEventRepository, mFailedItemRepository);
    }

    public void execute(Enrollment enrollment) {
        if (enrollment == null) {
            return;
        }

        //if (Do you have to synchronize TEI?)
        //TrackedEntityInstanceSynchronizer.sync(TEI);

        //else
        mEnrollmentSynchronizer.sync(enrollment);
    }
}