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

import com.furianrt.mydiary.presentation.base.mvp.BaseMvpView
import com.furianrt.mydiary.presentation.base.mvp.BaseMvpPresenter

interface TagsDialogContract {

    interface View : BaseMvpView {
        fun showTagListView(noteId: String)
    }

    abstract class Presenter : BaseMvpPresenter<View>() {
        abstract fun init(noteId: String)
    }
}