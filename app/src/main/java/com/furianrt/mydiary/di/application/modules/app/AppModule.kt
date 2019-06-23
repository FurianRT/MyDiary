package com.furianrt.mydiary.di.application.modules.app

import android.app.Application
import android.content.Context
import com.furianrt.mydiary.di.application.component.AppScope
import dagger.Module
import dagger.Provides

@Module
class AppModule(private val app: Application) {

    @Provides
    @AppScope
    fun provideApplication(): Application = app

    @Provides
    @AppScope
    fun provideContext(): Context = app
}