/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.main.fragments.profile.menu

import com.furianrt.mydiary.presentation.base.mvp.BaseMvpView
import com.furianrt.mydiary.presentation.base.mvp.BaseMvpPresenter

interface MenuProfileContract {

    interface MvpView : BaseMvpView {
        fun showSignOutView()
        fun showPasswordView()
        fun showAboutView()
        fun disableSignOut(disable: Boolean)
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun onButtonSignOutClick()
        abstract fun onButtonChangePasswordClick()
        abstract fun onButtonAboutClick()
    }
}