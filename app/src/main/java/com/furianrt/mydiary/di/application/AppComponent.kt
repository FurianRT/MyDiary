package com.furianrt.mydiary.di.application

import android.app.Application
import com.furianrt.mydiary.di.presenter.PresenterComponent
import com.furianrt.mydiary.di.presenter.PresenterModule
import dagger.Component

@AppScope
@Component(modules = [AppModule::class])
interface AppComponent {

    fun newPresenterComponent(module: PresenterModule): PresenterComponent
    fun inject(application: Application)
}