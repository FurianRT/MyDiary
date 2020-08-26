/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.analytics

import android.os.Bundle
import com.furianrt.mydiary.BuildConfig
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject

class MyAnalyticsImp @Inject constructor(
        private val firebaseAnalytics: FirebaseAnalytics,
        private val firebaseCrashlytics: FirebaseCrashlytics
) : MyAnalytics {

    override fun init() {
        //FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
    }

    override fun sendEvent(event: String, bundle: Bundle?) {
        if (!BuildConfig.DEBUG) {
            firebaseAnalytics.logEvent(event, bundle)
        }
    }

    override fun logExceptionEvent(error: Throwable) {
        if (!BuildConfig.DEBUG) {
            firebaseCrashlytics.recordException(error)
        }
    }
}