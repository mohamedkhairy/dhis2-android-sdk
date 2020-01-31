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
package org.hisp.dhis.android.core.settings.internal;

import org.hisp.dhis.android.core.arch.api.executors.internal.RxAPICallExecutor;
import org.hisp.dhis.android.core.arch.call.internal.CompletableProvider;
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.access.Transaction;
import org.hisp.dhis.android.core.arch.handlers.internal.Handler;
import org.hisp.dhis.android.core.settings.AndroidSetting;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Completable;
import io.reactivex.Single;

@Reusable
public class AndroidSettingCall implements CompletableProvider {

    private final DatabaseAdapter databaseAdapter;
    private final Handler<AndroidSetting> androidSettingHandler;
    private final AndroidSettingAppService androidSettingAppService;
    private final RxAPICallExecutor apiCallExecutor;

    @Inject
    AndroidSettingCall(DatabaseAdapter databaseAdapter,
                        Handler<AndroidSetting> androidSettingHandler,
                        AndroidSettingAppService androidSettingAppService,
                        RxAPICallExecutor apiCallExecutor) {
        this.databaseAdapter = databaseAdapter;
        this.androidSettingHandler = androidSettingHandler;
        this.androidSettingAppService = androidSettingAppService;
        this.apiCallExecutor = apiCallExecutor;
    }

    @Override
    public Completable getCompletable(boolean storeError) {
        return Completable
                .fromSingle(downloadAndPersist(storeError))
                .onErrorComplete();
    }

    private Single<AndroidSetting> downloadAndPersist(boolean storeError) {
        return apiCallExecutor.wrapSingle(androidSettingAppService.getAndroidSettings(), storeError)
                .map(androidSetting -> {
                    Transaction transaction = databaseAdapter.beginNewTransaction();
                    try {
                        List<AndroidSetting> androidSettingList = Collections.singletonList(androidSetting);
                        androidSettingHandler.handleMany(androidSettingList);
                        transaction.setSuccessful();
                    } finally {
                        transaction.end();
                    }
                    return androidSetting;
                });
    }
}
