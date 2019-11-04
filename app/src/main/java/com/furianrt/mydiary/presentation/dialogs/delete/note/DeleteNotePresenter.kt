/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.dialogs.delete.note

import com.furianrt.mydiary.domain.delete.DeleteNotesUseCase
import com.furianrt.mydiary.utils.MyRxUtils
import javax.inject.Inject

class DeleteNotePresenter @Inject constructor(
        private val deleteNotes: DeleteNotesUseCase,
        private val scheduler: MyRxUtils.BaseSchedulerProvider
) : DeleteNoteContract.Presenter() {

    override fun onButtonDeleteClick(notesIds: List<String>) {
        addDisposable(deleteNotes.invoke(notesIds)
                .observeOn(scheduler.ui())
                .subscribe { view?.closeView() })
    }

    override fun onButtonCancelClick() {
        view?.closeView()
    }
}