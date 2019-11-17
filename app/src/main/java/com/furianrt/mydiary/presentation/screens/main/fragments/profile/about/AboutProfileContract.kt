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

import com.furianrt.mydiary.presentation.base.BaseView
import com.furianrt.mydiary.presentation.base.BasePresenter
import com.furianrt.mydiary.model.entity.MyProfile

interface AboutProfileContract {

    interface View : BaseView {
        fun showProfileInfo(profile: MyProfile, is24TimeFormat: Boolean)
        fun returnToMenuView()

    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onViewStart()
        abstract fun onButtonBackClick()
    }
}