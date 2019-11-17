/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.main.fragments.profile

import com.furianrt.mydiary.presentation.base.BaseView
import com.furianrt.mydiary.presentation.base.BasePresenter
import com.furianrt.mydiary.model.entity.MyProfile

interface ProfileContract {

    interface View : BaseView {
        fun close()
        fun showProfile(profile: MyProfile)
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onButtonCloseClick()
    }
}