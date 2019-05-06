package com.furianrt.mydiary.screens.main.fragments.profile.password

import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView

interface PasswordContract {

    interface View : BaseView {
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

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onButtonCancelClick()
        abstract fun onButtonSaveClick(oldPassword: String, newPassword: String, repeatPassword: String)
    }
}