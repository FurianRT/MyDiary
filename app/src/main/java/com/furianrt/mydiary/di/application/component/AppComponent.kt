/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.di.application.component

import android.content.Context
import com.furianrt.mydiary.MyApp
import com.furianrt.mydiary.di.application.modules.app.AnalyticsModule
import com.furianrt.mydiary.di.application.modules.app.AppContext
import com.furianrt.mydiary.di.application.modules.data.DatabaseModule
import com.furianrt.mydiary.di.application.modules.data.EncryptionModule
import com.furianrt.mydiary.di.application.modules.data.SourceModule
import com.furianrt.mydiary.di.application.modules.data.GatewayModule
import com.furianrt.mydiary.di.application.modules.location.LocationModule
import com.furianrt.mydiary.di.application.modules.network.ApiModule
import com.furianrt.mydiary.di.application.modules.network.FirebaseModule
import com.furianrt.mydiary.di.application.modules.rx.RxModule
import com.furianrt.mydiary.di.presenter.component.PresenterComponent
import dagger.BindsInstance
import dagger.Component

@AppScope
@Component(modules = [ApiModule::class, SourceModule::class,
    GatewayModule::class, FirebaseModule::class, DatabaseModule::class, RxModule::class,
    AnalyticsModule::class, LocationModule::class, EncryptionModule::class])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance @AppContext context: Context): AppComponent
    }

    fun presenterComponent(): PresenterComponent.Factory

    fun inject(application: MyApp)
}