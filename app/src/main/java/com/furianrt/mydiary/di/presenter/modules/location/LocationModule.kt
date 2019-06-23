package com.furianrt.mydiary.di.presenter.modules.location

import android.content.Context
import com.furianrt.mydiary.di.presenter.component.PresenterScope
import com.furianrt.mydiary.di.presenter.modules.presenter.PresenterContext
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides

@Module
class LocationModule {

    @Provides
    @PresenterScope
    fun provideFusedLocationProviderClient(@PresenterContext context: Context): FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)
}