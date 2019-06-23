package com.furianrt.mydiary.di.application.modules.app

import com.furianrt.mydiary.analytics.MyAnalytics
import com.furianrt.mydiary.analytics.MyAnalyticsImp
import com.furianrt.mydiary.di.application.component.AppScope
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Module
import dagger.Provides

@Module
class AnalyticsModule {

    @Provides
    @AppScope
    fun provideMyAnalytics(firebaseAnalytics: FirebaseAnalytics): MyAnalytics =
            MyAnalyticsImp(firebaseAnalytics)
}