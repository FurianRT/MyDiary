/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.dialogs.tags.fragments.edit

import com.furianrt.mydiary.presentation.base.BaseView
import com.furianrt.mydiary.presentation.base.BasePresenter
import com.furianrt.mydiary.model.entity.MyTag

interface TagEditContract {

    interface View : BaseView {
        fun closeView()
        fun showErrorEmptyTagName()
        fun showErrorExistingTagName()
        fun showTagName(name: String)
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun init(tag: MyTag)
        abstract fun onButtonConfirmClick(newTagName: String)
        abstract fun onButtonCloseClick()
    }
}