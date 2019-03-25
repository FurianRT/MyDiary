package com.furianrt.mydiary.base

import android.content.Context
import com.furianrt.mydiary.MyApp
import com.furianrt.mydiary.di.presenter.PresenterComponent
import com.furianrt.mydiary.di.presenter.PresenterModule

interface BaseView {

    fun getPresenterComponent(context: Context): PresenterComponent {
        return (context.applicationContext as MyApp)
                .component
                .newPresenterComponent(PresenterModule(context))
    }
}