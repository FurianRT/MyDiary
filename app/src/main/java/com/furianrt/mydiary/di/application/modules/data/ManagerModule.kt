package com.furianrt.mydiary.di.application.modules.data

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.DataManagerImp
import com.furianrt.mydiary.data.api.ApiServiceHelper
import com.furianrt.mydiary.data.auth.AuthHelper
import com.furianrt.mydiary.data.cloud.CloudHelper
import com.furianrt.mydiary.data.database.NoteDatabase
import com.furianrt.mydiary.data.encryption.EncryptionHelper
import com.furianrt.mydiary.data.prefs.PreferencesHelper
import com.furianrt.mydiary.data.storage.StorageHelper
import com.furianrt.mydiary.di.application.component.AppScope
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler

@Module
class ManagerModule {

    @Provides
    @AppScope
    fun provideDataManager(
            database: NoteDatabase,
            prefs: PreferencesHelper,
            storage: StorageHelper,
            apiService: ApiServiceHelper,
            cloudHelper: CloudHelper,
            authHelper: AuthHelper,
            rxScheduler: Scheduler,
            encryption: EncryptionHelper
    ): DataManager = DataManagerImp(
            database,
            prefs,
            storage,
            apiService,
            cloudHelper,
            authHelper,
            rxScheduler,
            encryption
    )
}