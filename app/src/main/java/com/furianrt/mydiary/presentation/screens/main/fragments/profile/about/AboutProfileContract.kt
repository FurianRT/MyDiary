/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.main.fragments.profile.about

import com.furianrt.mydiary.presentation.base.mvp.BaseMvpView
import com.furianrt.mydiary.presentation.base.mvp.BaseMvpPresenter
import com.furianrt.mydiary.model.entity.MyProfile

interface AboutProfileContract {

    interface View : BaseMvpView {
        fun showProfileInfo(profile: MyProfile, is24TimeFormat: Boolean)
        fun returnToMenuView()

    }

    abstract class Presenter : BaseMvpPresenter<View>() {
        abstract fun onViewStart()
        abstract fun onButtonBackClick()
    }
}