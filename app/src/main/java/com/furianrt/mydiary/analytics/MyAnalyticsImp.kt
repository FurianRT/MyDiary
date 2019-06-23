package com.furianrt.mydiary.analytics

import android.os.Bundle
import com.furianrt.mydiary.BuildConfig
import com.google.firebase.analytics.FirebaseAnalytics

class MyAnalyticsImp(
        private val firebaseAnalytics: FirebaseAnalytics
) : MyAnalytics{

    override fun sendEvent(event: String, bundle: Bundle?) {
        if (!BuildConfig.DEBUG) {
            firebaseAnalytics.logEvent(event, bundle)
        }
    }
}