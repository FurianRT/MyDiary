/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.dialogs.tags.fragments.delete

import com.furianrt.mydiary.view.base.mvp.BaseMvpView
import com.furianrt.mydiary.view.base.mvp.BaseMvpPresenter
import com.furianrt.mydiary.model.entity.MyTag

interface TagDeleteContract {

    interface MvpView : BaseMvpView {
        fun closeView()
        fun showTagName(name: String)
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun init(tag: MyTag)
        abstract fun onButtonDeleteClick()
        abstract fun onButtonCancelClick()
    }
}