/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.di.application.modules.rx

import com.furianrt.mydiary.di.application.component.AppScope
import com.furianrt.mydiary.utils.MyRxUtils
import dagger.Module
import dagger.Provides

@Module
object RxModule {

    @JvmStatic
    @Provides
    @AppScope
    fun provideRxScheduler(): MyRxUtils.BaseSchedulerProvider = MyRxUtils.SchedulerProvider()
}