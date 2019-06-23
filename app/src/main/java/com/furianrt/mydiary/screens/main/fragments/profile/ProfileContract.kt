package com.furianrt.mydiary.screens.main.fragments.profile

import com.furianrt.mydiary.base.BaseMvpView
import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.data.model.MyProfile

interface ProfileContract {

    interface MvpView : BaseMvpView {
        fun close()
        fun showProfile(profile: MyProfile)
    }

    abstract class Presenter : BasePresenter<MvpView>() {
        abstract fun onButtonCloseClick()
    }
}