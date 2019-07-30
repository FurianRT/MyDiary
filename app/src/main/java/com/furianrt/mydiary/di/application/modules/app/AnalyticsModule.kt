/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.di.application.modules.app

import com.furianrt.mydiary.analytics.MyAnalytics
import com.furianrt.mydiary.analytics.MyAnalyticsImp
import com.furianrt.mydiary.di.application.component.AppScope
import dagger.Binds
import dagger.Module

@Module
interface AnalyticsModule {

    @Binds
    @AppScope
    fun analytics(imp: MyAnalyticsImp): MyAnalytics
}