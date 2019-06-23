package com.furianrt.mydiary.di.presenter.modules

import android.content.Context
import com.furianrt.mydiary.di.presenter.PresenterScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides

@Module
class LocationModule(private val context: Context) {

    @Provides
    @PresenterScope
    fun provideFusedLocationProviderClient(): FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)
}