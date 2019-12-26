/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.main.fragments.profile.signout

import com.furianrt.mydiary.presentation.base.BaseView
import com.furianrt.mydiary.presentation.base.BasePresenter

interface SignOutContract {

    interface View : BaseView {
        fun close()
        fun returnToMenuView()
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onButtonSignOutClick()
        abstract fun onButtonSignOutCancelClick()
    }
}