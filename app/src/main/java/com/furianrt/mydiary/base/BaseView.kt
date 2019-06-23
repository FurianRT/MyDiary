package com.furianrt.mydiary.base

import android.content.Context
import com.furianrt.mydiary.MyApp
import com.furianrt.mydiary.di.presenter.PresenterComponent
import com.furianrt.mydiary.di.presenter.modules.LocationModule
import com.furianrt.mydiary.di.presenter.modules.PresenterModule

interface BaseView {
    fun getPresenterComponent(context: Context): PresenterComponent =
            (context.applicationContext as MyApp)
                    .component
                    .newPresenterComponent(PresenterModule(), LocationModule(context))
}