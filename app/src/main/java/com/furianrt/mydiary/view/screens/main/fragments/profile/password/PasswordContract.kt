/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.screens.main.fragments.profile.password

import com.furianrt.mydiary.view.base.mvp.BaseMvpView
import com.furianrt.mydiary.view.base.mvp.BaseMvpPresenter

interface PasswordContract {

    interface MvpView : BaseMvpView {
        fun returnToMenuView()
        fun showLoading()
        fun hideLoading()
        fun showErrorEmptyOldPassword()
        fun showErrorEmptyNewPassword()
        fun showErrorEmptyRepeatPassword()
        fun showErrorWrongOldPassword()
        fun showErrorWrongPasswordRepeat()
        fun showErrorNetworkConnection()
        fun showErrorShortNewPassword()
        fun showSuccessPasswordChange()
        fun clearErrorMessage()
        fun close()
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun onButtonCancelClick()
        abstract fun onButtonSaveClick(oldPassword: String, newPassword: String, repeatPassword: String)
    }
}