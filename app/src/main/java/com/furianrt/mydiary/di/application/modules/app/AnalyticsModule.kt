package com.furianrt.mydiary.di.application.modules.app

import com.furianrt.mydiary.analytics.MyAnalytics
import com.furianrt.mydiary.analytics.MyAnalyticsImp
import com.furianrt.mydiary.di.application.component.AppScope
import dagger.Binds
import dagger.Module

@Module
abstract class AnalyticsModule {

    @Binds
    @AppScope
    abstract fun analytics(imp: MyAnalyticsImp): MyAnalytics
}