package com.furianrt.mydiary.base

import android.content.Context
import com.furianrt.mydiary.MyApp
import com.furianrt.mydiary.di.presenter.component.PresenterComponent
import com.furianrt.mydiary.di.presenter.modules.location.LocationModule
import com.furianrt.mydiary.di.presenter.modules.presenter.PresenterModule

interface BaseView {
    fun getPresenterComponent(context: Context): PresenterComponent =
            (context.applicationContext as MyApp)
                    .component
                    .newPresenterComponent(PresenterModule(), LocationModule(context))
}