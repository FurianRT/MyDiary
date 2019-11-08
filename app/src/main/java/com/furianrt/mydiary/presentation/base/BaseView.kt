/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.base

import android.content.Context
import com.furianrt.mydiary.MyApp
import com.furianrt.mydiary.di.presenter.component.PresenterComponent
import com.furianrt.mydiary.di.presenter.modules.presenter.PresenterContextModule

interface BaseView {

    fun getPresenterComponent(context: Context): PresenterComponent =
            (context.applicationContext as MyApp)
                    .component
                    .newPresenterComponent(PresenterContextModule(context))
}