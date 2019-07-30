/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.dialogs.tags.fragments.edit

import com.furianrt.mydiary.view.base.mvp.BaseMvpView
import com.furianrt.mydiary.view.base.mvp.BaseMvpPresenter
import com.furianrt.mydiary.data.model.MyTag

interface TagEditContract {

    interface MvpView : BaseMvpView {
        fun closeView()
        fun showErrorEmptyTagName()
        fun showErrorExistingTagName()
        fun showTagName(name: String)
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun init(tag: MyTag)
        abstract fun onButtonConfirmClick(newTagName: String)
        abstract fun onButtonCloseClick()
    }
}