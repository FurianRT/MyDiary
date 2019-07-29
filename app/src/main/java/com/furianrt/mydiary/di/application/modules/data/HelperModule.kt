/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.di.application.modules.data

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
interface HelperModule {

    @Binds
    @AppScope
    fun cloudHelper(imp: CloudHelperImp): CloudHelper

    @Binds
    @AppScope
    fun authHelper(imp: AuthHelperImp): AuthHelper

    @Binds
    @AppScope
    fun storageHelper(imp: StorageHelperImp): StorageHelper

    @Binds
    @AppScope
    fun preferencesHelper(imp: PreferencesHelperImp): PreferencesHelper

    @Binds
    @AppScope
    fun encryptionHelper(imp: EncryptionHelperImp): EncryptionHelper
}