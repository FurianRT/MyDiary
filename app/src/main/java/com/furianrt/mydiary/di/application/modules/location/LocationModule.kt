/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.di.application.modules.location

import android.content.Context
import android.location.Geocoder
import com.furianrt.mydiary.di.application.component.AppScope
import com.furianrt.mydiary.di.application.modules.app.AppContext
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import java.util.Locale

@Module
object LocationModule {

    @JvmStatic
    @Provides
    @AppScope
    fun provideFusedLocation(@AppContext context: Context): FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)

    @JvmStatic
    @Provides
    @AppScope
    fun provideGeocoder(@AppContext context: Context): Geocoder =
            Geocoder(context, Locale.getDefault())
}