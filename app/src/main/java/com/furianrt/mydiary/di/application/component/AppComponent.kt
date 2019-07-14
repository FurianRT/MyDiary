package com.furianrt.mydiary.di.application.component

import com.furianrt.mydiary.MyApp
import com.furianrt.mydiary.di.application.modules.app.AnalyticsModule
import com.furianrt.mydiary.di.application.modules.app.AppContextModule
import com.furianrt.mydiary.di.application.modules.data.DatabaseModule
import com.furianrt.mydiary.di.application.modules.data.HelperModule
import com.furianrt.mydiary.di.application.modules.data.RepositoryModule
import com.furianrt.mydiary.di.application.modules.network.ApiModule
import com.furianrt.mydiary.di.application.modules.network.FirebaseModule
import com.furianrt.mydiary.di.application.modules.rx.RxModule
import com.furianrt.mydiary.di.presenter.component.PresenterComponent
import com.furianrt.mydiary.di.presenter.modules.location.LocationModule
import com.furianrt.mydiary.di.presenter.modules.presenter.PresenterContextModule
import dagger.Component

@AppScope
@Component(modules = [AppContextModule::class, ApiModule::class, HelperModule::class, RepositoryModule::class,
    FirebaseModule::class, DatabaseModule::class, RxModule::class, AnalyticsModule::class])
interface AppComponent {

    fun newPresenterComponent(
            presenterContextModule: PresenterContextModule,
            locationModule: LocationModule
    ): PresenterComponent

    fun inject(application: MyApp)
}