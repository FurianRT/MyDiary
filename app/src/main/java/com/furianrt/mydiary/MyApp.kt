package com.furianrt.mydiary

import android.app.Application
import com.furianrt.mydiary.di.application.AppComponent
import com.furianrt.mydiary.di.application.AppModule
import com.furianrt.mydiary.di.application.DaggerAppComponent

const val LOG_TAG = "myTag"

class MyApp : Application() {

    val component: AppComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
}