package com.furianrt.mydiary.main.fragments.profile.password

import com.furianrt.mydiary.BasePresenter
import com.furianrt.mydiary.BaseView

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
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onButtonCancelClick()
        abstract fun onButtonSaveClick(oldPassword: String, newPassword: String, repeatPassword: String)
    }
}