/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.dialogs.categories.fragments.delete

import com.furianrt.mydiary.presentation.base.BaseView
import com.furianrt.mydiary.presentation.base.BasePresenter
import com.furianrt.mydiary.model.entity.MyCategory

interface CategoryDeleteContract {

    interface View : BaseView {
        fun closeView()
        fun showDeleteMessage(name: String)
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun init(category: MyCategory)
        abstract fun onButtonDeleteClick()
        abstract fun onButtonCancelClick()
    }
}