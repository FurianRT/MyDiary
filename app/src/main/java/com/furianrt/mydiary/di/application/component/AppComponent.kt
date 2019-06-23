package com.furianrt.mydiary.di.application.component

import com.furianrt.mydiary.MyApp
import com.furianrt.mydiary.di.application.modules.app.AppModule
import com.furianrt.mydiary.di.application.modules.data.DatabaseModule
import com.furianrt.mydiary.di.application.modules.data.HelperModule
import com.furianrt.mydiary.di.application.modules.data.ManagerModule
import com.furianrt.mydiary.di.application.modules.network.ApiModule
import com.furianrt.mydiary.di.application.modules.network.FirebaseModule
import com.furianrt.mydiary.di.application.modules.rx.RxModule
import com.furianrt.mydiary.di.presenter.component.PresenterComponent
import com.furianrt.mydiary.di.presenter.modules.location.LocationModule
import com.furianrt.mydiary.di.presenter.modules.presenter.PresenterModule
import dagger.Component

@AppScope
@Component(modules = [AppModule::class, ApiModule::class, HelperModule::class, ManagerModule::class,
    FirebaseModule::class, DatabaseModule::class, RxModule::class])
interface AppComponent {
    fun newPresenterComponent(presenterModule: PresenterModule, locationModule: LocationModule): PresenterComponent
    fun inject(application: MyApp)
}