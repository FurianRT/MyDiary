/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.pin.fragments.backupemail

import com.furianrt.mydiary.presentation.base.BaseView
import com.furianrt.mydiary.presentation.base.BasePresenter

interface BackupEmailContract {

    interface View : BaseView {
        fun showEmailIsCorrect(email: String)
        fun showErrorEmailFormat()
        fun showEmail(email: String)
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onButtonDoneClick(email: String)
        abstract fun onViewCreated(email: String, firstLaunch: Boolean)

    }
}