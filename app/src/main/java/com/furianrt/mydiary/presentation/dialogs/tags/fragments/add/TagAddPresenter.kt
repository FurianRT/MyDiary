/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.dialogs.tags.fragments.add

import com.furianrt.mydiary.domain.save.AddTagToNoteUseCase
import com.furianrt.mydiary.domain.save.SaveTagUseCase
import com.furianrt.mydiary.utils.MyRxUtils
import javax.inject.Inject

class TagAddPresenter @Inject constructor(
        private val saveTagUseCase: SaveTagUseCase,
        private val addTagToNoteUseCase: AddTagToNoteUseCase,
        private val scheduler: MyRxUtils.BaseSchedulerProvider
) : TagAddContract.Presenter() {

    private lateinit var mNoteId: String

    override fun init(noteId: String) {
        mNoteId = noteId
    }

    override fun onButtonAddClick(name: String) {
        if (name.isBlank()) {
            view?.showErrorEmptyTagName()
        } else {
            addDisposable(saveTagUseCase(name)
                    .flatMapCompletable { addTagToNoteUseCase(mNoteId, it) }
                    .observeOn(scheduler.ui())
                    .subscribe({
                        view?.closeView()
                    }, {
                        if (it is SaveTagUseCase.InvalidTagNameException) {
                            view?.showErrorExistingTagName()
                        } else {
                            it.printStackTrace()
                        }
                    }))
        }
    }

    override fun onButtonCloseClick() {
        view?.closeView()
    }
}