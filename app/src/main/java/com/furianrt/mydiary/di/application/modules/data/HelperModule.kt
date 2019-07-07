package com.furianrt.mydiary.di.application.modules.data

import com.furianrt.mydiary.data.api.ApiServiceHelper
import com.furianrt.mydiary.data.api.ApiServiceHelperImp
import com.furianrt.mydiary.data.auth.AuthHelper
import com.furianrt.mydiary.data.auth.AuthHelperImp
import com.furianrt.mydiary.data.cloud.CloudHelper
import com.furianrt.mydiary.data.cloud.CloudHelperImp
import com.furianrt.mydiary.data.encryption.EncryptionHelper
import com.furianrt.mydiary.data.encryption.EncryptionHelperImp
import com.furianrt.mydiary.data.prefs.PreferencesHelper
import com.furianrt.mydiary.data.prefs.PreferencesHelperImp
import com.furianrt.mydiary.data.storage.StorageHelper
import com.furianrt.mydiary.data.storage.StorageHelperImp
import com.furianrt.mydiary.di.application.component.AppScope
import dagger.Binds
import dagger.Module

@Module
abstract class HelperModule {

    @Binds
    @AppScope
    abstract fun cloudHelper(imp: CloudHelperImp): CloudHelper

    @Binds
    @AppScope
    abstract fun authHelper(imp: AuthHelperImp): AuthHelper

    @Binds
    @AppScope
    abstract fun storageHelper(imp: StorageHelperImp): StorageHelper

    @Binds
    @AppScope
    abstract fun preferencesHelper(imp: PreferencesHelperImp): PreferencesHelper

    @Binds
    @AppScope
    abstract fun apiServiceHelper(imp: ApiServiceHelperImp): ApiServiceHelper

    @Binds
    @AppScope
    abstract fun encryptionHelper(imp: EncryptionHelperImp): EncryptionHelper
}