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

import com.furianrt.mydiary.presentation.base.mvp.BaseMvpView
import com.furianrt.mydiary.presentation.base.mvp.BaseMvpPresenter

interface BackupEmailContract {

    interface View : BaseMvpView {
        fun showEmailIsCorrect(email: String)
        fun showErrorEmailFormat()
        fun showEmail(email: String)
    }

    abstract class Presenter : BaseMvpPresenter<View>() {
        abstract fun onButtonDoneClick(email: String)
        abstract fun onViewCreated(email: String, firstLaunch: Boolean)

    }
}