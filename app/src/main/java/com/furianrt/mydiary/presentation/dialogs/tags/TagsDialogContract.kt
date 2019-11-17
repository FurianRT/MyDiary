/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.dialogs.tags

import com.furianrt.mydiary.presentation.base.BaseView
import com.furianrt.mydiary.presentation.base.BasePresenter

interface TagsDialogContract {

    interface View : BaseView {
        fun showTagListView(noteId: String)
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun init(noteId: String)
    }
}