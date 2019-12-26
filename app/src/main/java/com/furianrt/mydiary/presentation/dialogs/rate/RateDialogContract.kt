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

import com.furianrt.mydiary.presentation.base.BaseView
import com.furianrt.mydiary.presentation.base.BasePresenter

interface RateDialogContract {

    interface View : BaseView {
        fun sendEmailToSupport(supportEmail: String)
        fun openAppPage()
        fun close()
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onButtonRateClick(rating: Int)
        abstract fun onButtonLaterClick()
        abstract fun onButtonNeverClick()
    }
}