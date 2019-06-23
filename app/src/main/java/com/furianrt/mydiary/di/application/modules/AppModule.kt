package com.furianrt.mydiary.di.application.modules

import android.app.Application
import android.content.Context
import com.furianrt.mydiary.data.auth.AuthHelper
import com.furianrt.mydiary.data.auth.AuthHelperImp
import com.furianrt.mydiary.data.cloud.CloudHelper
import com.furianrt.mydiary.data.cloud.CloudHelperImp
import com.furianrt.mydiary.data.prefs.PreferencesHelper
import com.furianrt.mydiary.data.prefs.PreferencesHelperImp
import com.furianrt.mydiary.data.storage.StorageHelper
import com.furianrt.mydiary.data.storage.StorageHelperImp
import com.furianrt.mydiary.di.application.AppScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

@Module
class AppModule(private val app: Application) {

    @Provides
    @AppScope
    fun provideApplication(): Application = app

    @Provides
    @AppScope
    fun provideContext(): Context = app

    @Provides
    @AppScope
    fun provideRxScheduler(): Scheduler = Schedulers.io()
}