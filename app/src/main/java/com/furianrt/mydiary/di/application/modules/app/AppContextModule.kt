/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.di.application.modules.app

import android.app.Application
import android.content.Context
import com.furianrt.mydiary.di.application.component.AppScope
import dagger.Module
import dagger.Provides

@Module
class AppContextModule(private val app: Application) {

    @Provides
    @AppScope
    @AppContext
    fun provideContext(): Context = app
}