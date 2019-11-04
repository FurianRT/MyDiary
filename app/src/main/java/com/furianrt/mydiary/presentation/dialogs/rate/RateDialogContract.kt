/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.dialogs.rate

import com.furianrt.mydiary.presentation.base.mvp.BaseMvpView
import com.furianrt.mydiary.presentation.base.mvp.BaseMvpPresenter

interface RateDialogContract {
    interface MvpView : BaseMvpView {
        fun sendEmailToSupport(supportEmail: String)
        fun openAppPage()
        fun close()
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun onButtonRateClick(rating: Int)
        abstract fun onButtonLaterClick()
        abstract fun onButtonNeverClick()
    }
}