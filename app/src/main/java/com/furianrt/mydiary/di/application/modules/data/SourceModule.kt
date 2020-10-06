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

import com.furianrt.mydiary.model.source.auth.AuthSource
import com.furianrt.mydiary.model.source.auth.AuthSourceImp
import com.furianrt.mydiary.model.source.cloud.CloudSource
import com.furianrt.mydiary.model.source.cloud.CloudSourceImp
import com.furianrt.mydiary.model.source.preferences.PreferencesSource
import com.furianrt.mydiary.model.source.preferences.PreferencesSourceImp
import com.furianrt.mydiary.model.source.storage.StorageSource
import com.furianrt.mydiary.model.source.storage.StorageSourceImp
import com.furianrt.mydiary.di.application.component.AppScope
import com.furianrt.mydiary.model.source.billing.inapp.InAppBillingSource
import com.furianrt.mydiary.model.source.billing.inapp.InAppBillingSourceImp
import dagger.Binds
import dagger.Module

@Module
interface SourceModule {

    @Binds
    @AppScope
    fun cloudSource(imp: CloudSourceImp): CloudSource

    @Binds
    @AppScope
    fun authSource(imp: AuthSourceImp): AuthSource

    @Binds
    @AppScope
    fun storageSource(imp: StorageSourceImp): StorageSource

    @Binds
    @AppScope
    fun preferencesSource(imp: PreferencesSourceImp): PreferencesSource

    @Binds
    @AppScope
    fun billingSource(imp: InAppBillingSourceImp): InAppBillingSource
}