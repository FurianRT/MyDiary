package com.furianrt.mydiary.view.base

import android.content.Context
import com.furianrt.mydiary.MyApp
import com.furianrt.mydiary.di.presenter.component.PresenterComponent
import com.furianrt.mydiary.di.presenter.modules.location.LocationModule
import com.furianrt.mydiary.di.presenter.modules.presenter.PresenterContextModule

interface BaseView {

    fun getPresenterComponent(context: Context): PresenterComponent =
            (context.applicationContext as MyApp)
                    .component
                    .newPresenterComponent(PresenterContextModule(context), LocationModule())
}