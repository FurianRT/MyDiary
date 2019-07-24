package com.furianrt.mydiary.view.screens.main.fragments.profile

import com.furianrt.mydiary.view.base.mvp.BaseMvpView
import com.furianrt.mydiary.view.base.mvp.BaseMvpPresenter
import com.furianrt.mydiary.data.model.MyProfile

interface ProfileContract {

    interface MvpView : BaseMvpView {
        fun close()
        fun showProfile(profile: MyProfile)
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun onButtonCloseClick()
    }
}