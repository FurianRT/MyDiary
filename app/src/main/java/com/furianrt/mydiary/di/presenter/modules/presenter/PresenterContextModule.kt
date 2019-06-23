package com.furianrt.mydiary.di.presenter.modules.presenter

import android.content.Context
import com.furianrt.mydiary.di.presenter.component.PresenterScope
import dagger.Module
import dagger.Provides

@Module
class PresenterContextModule(private val context: Context) {

    @Provides
    @PresenterScope
    @PresenterContext
    fun provideContext(): Context = context
}