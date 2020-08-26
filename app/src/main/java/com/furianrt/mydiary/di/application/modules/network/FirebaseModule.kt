/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.di.application.modules.network

import android.content.Context
import com.furianrt.mydiary.di.application.component.AppScope
import com.furianrt.mydiary.di.application.modules.app.AppContext
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides

@Module
object FirebaseModule {

    @JvmStatic
    @Provides
    @AppScope
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @JvmStatic
    @Provides
    @AppScope
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
            .apply {
                firestoreSettings = FirebaseFirestoreSettings.Builder(firestoreSettings)
                        .setPersistenceEnabled(false)
                        .build()
            }

    @JvmStatic
    @Provides
    @AppScope
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @JvmStatic
    @Provides
    @AppScope
    fun provideFirebaseAnalytics(@AppContext context: Context): FirebaseAnalytics =
            FirebaseAnalytics.getInstance(context)

    @JvmStatic
    @Provides
    @AppScope
    fun provideFirebaseCrashlytics(): FirebaseCrashlytics = FirebaseCrashlytics.getInstance()
}