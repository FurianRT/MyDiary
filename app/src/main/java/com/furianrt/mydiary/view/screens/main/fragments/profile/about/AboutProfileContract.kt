package com.furianrt.mydiary.view.screens.main.fragments.profile.about

import com.furianrt.mydiary.view.base.mvp.BaseMvpView
import com.furianrt.mydiary.view.base.mvp.BaseMvpPresenter
import com.furianrt.mydiary.data.model.MyProfile

interface AboutProfileContract {

    interface MvpView : BaseMvpView {
        fun showProfileInfo(profile: MyProfile)
        fun returnToMenuView()

    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun onViewStart()
        abstract fun onButtonBackClick()
    }
}