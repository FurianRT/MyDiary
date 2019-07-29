/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

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