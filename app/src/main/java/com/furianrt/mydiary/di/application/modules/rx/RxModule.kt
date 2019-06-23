package com.furianrt.mydiary.di.application.modules.rx

import com.furianrt.mydiary.di.application.component.AppScope
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

@Module
class RxModule {

    @Provides
    @AppScope
    fun provideRxScheduler(): Scheduler = Schedulers.io()
}