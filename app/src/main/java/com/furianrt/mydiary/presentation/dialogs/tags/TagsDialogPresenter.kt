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

import javax.inject.Inject

class TagsDialogPresenter @Inject constructor() : TagsDialogContract.Presenter() {

    private lateinit var mNoteId: String

    override fun init(noteId: String) {
        mNoteId = noteId
    }

    override fun attachView(view: TagsDialogContract.MvpView) {
        super.attachView(view)
        view.showTagListView(mNoteId)
    }
}