package com.furianrt.mydiary.screens.main.fragments.profile

import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView
import com.furianrt.mydiary.data.model.MyProfile

interface ProfileContract {

    interface View : BaseView {
        fun close()
        fun showProfile(profile: MyProfile)
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onButtonCloseClick()
    }
}