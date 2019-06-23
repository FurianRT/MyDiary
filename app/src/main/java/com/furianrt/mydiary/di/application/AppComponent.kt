package com.furianrt.mydiary.di.application

import com.furianrt.mydiary.MyApp
import com.furianrt.mydiary.di.application.modules.*
import com.furianrt.mydiary.di.presenter.PresenterComponent
import com.furianrt.mydiary.di.presenter.modules.LocationModule
import com.furianrt.mydiary.di.presenter.modules.PresenterModule
import dagger.Component

@AppScope
@Component(modules = [AppModule::class, ApiModule::class, DataModule::class, FirebaseModule::class, DatabaseModule::class])
interface AppComponent {
    fun newPresenterComponent(presenterModule: PresenterModule, locationModule: LocationModule): PresenterComponent
    fun inject(application: MyApp)
}