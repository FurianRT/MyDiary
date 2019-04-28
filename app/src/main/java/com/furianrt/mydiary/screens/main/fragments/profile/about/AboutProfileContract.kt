package com.furianrt.mydiary.screens.main.fragments.profile.about

import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView
import com.furianrt.mydiary.data.model.MyProfile

interface AboutProfileContract {

    interface View : BaseView {
        fun showProfileInfo(profile: MyProfile)
        fun returnToMenuView()

    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onViewResume()
        abstract fun onButtonBackClick()
    }
}