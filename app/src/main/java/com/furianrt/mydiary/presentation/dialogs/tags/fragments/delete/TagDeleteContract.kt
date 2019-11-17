/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.dialogs.tags.fragments.delete

import com.furianrt.mydiary.presentation.base.BaseView
import com.furianrt.mydiary.presentation.base.BasePresenter
import com.furianrt.mydiary.model.entity.MyTag

interface TagDeleteContract {

    interface View : BaseView {
        fun closeView()
        fun showTagName(name: String)
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun init(tag: MyTag)
        abstract fun onButtonDeleteClick()
        abstract fun onButtonCancelClick()
    }
}