package com.furianrt.mydiary.main.fragments.profile.about

import com.furianrt.mydiary.BasePresenter
import com.furianrt.mydiary.BaseView
import com.furianrt.mydiary.data.model.MyProfile

interface AboutProfileContract {

    interface View : BaseView {
        fun showProfileInfo(profile: MyProfile)
        fun returnToMenuView()

    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onViewStart()
        abstract fun onButtonBackClick()
    }
}