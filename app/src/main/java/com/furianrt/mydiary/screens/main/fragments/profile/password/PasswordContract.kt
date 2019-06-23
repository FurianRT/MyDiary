package com.furianrt.mydiary.screens.main.fragments.profile.password

import com.furianrt.mydiary.base.BaseMvpView
import com.furianrt.mydiary.base.BasePresenter

interface PasswordContract {

    interface MvpView : BaseMvpView {
        fun returnToMenuView()
        fun showLoading()
        fun hideLoading()
        fun isNetworkAvailable(): Boolean
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

    abstract class Presenter : BasePresenter<MvpView>() {
        abstract fun onButtonCancelClick()
        abstract fun onButtonSaveClick(oldPassword: String, newPassword: String, repeatPassword: String)
    }
}