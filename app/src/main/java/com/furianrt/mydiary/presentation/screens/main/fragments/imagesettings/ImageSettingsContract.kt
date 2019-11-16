/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.main.fragments.imagesettings

import com.furianrt.mydiary.presentation.base.mvp.BaseMvpView
import com.furianrt.mydiary.presentation.base.mvp.BaseMvpPresenter

interface ImageSettingsContract {

    interface View : BaseMvpView {
        fun close()
    }

    abstract class Presenter : BaseMvpPresenter<View>() {
        abstract fun onButtonCloseClick()
    }
}