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
package org.hisp.dhis.android.core.arch.db.access.internal

import android.content.Context
import dagger.Reusable
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.access.DatabaseImportExport
import org.hisp.dhis.android.core.configuration.internal.DatabaseNameGenerator
import org.hisp.dhis.android.core.systeminfo.internal.SystemInfoStore
import org.hisp.dhis.android.core.user.internal.UserCredentialsStoreImpl
import java.io.File
import javax.inject.Inject

@Reusable
class DatabaseImportExportImpl @Inject constructor(
    private val context: Context,
    private val nameGenerator: DatabaseNameGenerator) : DatabaseImportExport {

    companion object {
        const val TmpDatabase = "tmp-database.db"
    }

    override fun importDatabase(file: File) {
        var databaseAdapter: DatabaseAdapter? = null
        try {
            val tmpDatabase = context.getDatabasePath(TmpDatabase)
            file.copyTo(tmpDatabase)

            val openHelper = UnencryptedDatabaseOpenHelper(context, TmpDatabase, BaseDatabaseOpenHelper.VERSION)
            databaseAdapter = UnencryptedDatabaseAdapter(openHelper.writableDatabase)

            val userCredentialsStore = UserCredentialsStoreImpl.create(databaseAdapter)
            val userCredentials = userCredentialsStore.selectFirst()

            val systemInfoStore = SystemInfoStore.create(databaseAdapter)
            val systemInfo = systemInfoStore.selectFirst()

            val databaseName = nameGenerator.getDatabaseName(systemInfo.contextPath(),
                userCredentials.username(), false)

            if (!context.databaseList().contains(databaseName)) {
                // Copy database
                // Ensure entry is kept in store
            }
        } finally {
            databaseAdapter?.close()
            context.deleteDatabase(TmpDatabase)
        }
    }
}