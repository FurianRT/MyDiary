package com.furianrt.mydiary.di.application.modules.network

import android.content.Context
import com.furianrt.mydiary.di.application.component.AppScope
import com.furianrt.mydiary.di.application.modules.app.AppContext
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides

@Module
class FirebaseModule {

    @Provides
    @AppScope
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @AppScope
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
            .apply {
                firestoreSettings = FirebaseFirestoreSettings.Builder(firestoreSettings)
                        .setPersistenceEnabled(false)
                        .build()
            }

    @Provides
    @AppScope
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Provides
    @AppScope
    fun provideFirebaseAnalytics(@AppContext context: Context): FirebaseAnalytics =
            FirebaseAnalytics.getInstance(context)
}