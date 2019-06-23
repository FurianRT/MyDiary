package com.furianrt.mydiary.di.application.modules.data

import android.content.Context
import com.furianrt.mydiary.data.api.ApiServiceHelper
import com.furianrt.mydiary.data.api.ApiServiceHelperImp
import com.furianrt.mydiary.data.api.forecast.WeatherApiService
import com.furianrt.mydiary.data.api.images.ImageApiService
import com.furianrt.mydiary.data.auth.AuthHelper
import com.furianrt.mydiary.data.auth.AuthHelperImp
import com.furianrt.mydiary.data.cloud.CloudHelper
import com.furianrt.mydiary.data.cloud.CloudHelperImp
import com.furianrt.mydiary.data.prefs.PreferencesHelper
import com.furianrt.mydiary.data.prefs.PreferencesHelperImp
import com.furianrt.mydiary.data.storage.StorageHelper
import com.furianrt.mydiary.data.storage.StorageHelperImp
import com.furianrt.mydiary.di.application.component.AppScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides

@Module
class HelperModule {

    @Provides
    @AppScope
    fun provideCloudHelper(
            firestore: FirebaseFirestore,
            firebaseStorage: FirebaseStorage
    ): CloudHelper = CloudHelperImp(firestore, firebaseStorage)

    @Provides
    @AppScope
    fun provideAuthHelper(context: Context, firebaseAuth: FirebaseAuth): AuthHelper =
            AuthHelperImp(context, firebaseAuth)

    @Provides
    @AppScope
    fun provideStorageHelper(context: Context): StorageHelper = StorageHelperImp(context)

    @Provides
    @AppScope
    fun providePreferencesHelper(context: Context): PreferencesHelper = PreferencesHelperImp(context)

    @Provides
    @AppScope
    fun provideApiServiceHelper(weatherApi: WeatherApiService, imageApi: ImageApiService): ApiServiceHelper =
            ApiServiceHelperImp(weatherApi, imageApi)
}