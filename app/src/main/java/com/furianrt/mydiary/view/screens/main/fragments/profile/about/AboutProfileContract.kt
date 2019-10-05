/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.screens.main.fragments.profile.about

import com.furianrt.mydiary.view.base.mvp.BaseMvpView
import com.furianrt.mydiary.view.base.mvp.BaseMvpPresenter
import com.furianrt.mydiary.data.entity.MyProfile

interface AboutProfileContract {

    interface MvpView : BaseMvpView {
        fun showProfileInfo(profile: MyProfile, is24TimeFormat: Boolean)
        fun returnToMenuView()

    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun onViewStart()
        abstract fun onButtonBackClick()
    }
}