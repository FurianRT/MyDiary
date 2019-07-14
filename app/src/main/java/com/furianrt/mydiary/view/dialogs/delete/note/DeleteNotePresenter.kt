package com.furianrt.mydiary.view.dialogs.delete.note

import com.furianrt.mydiary.usecase.delete.DeleteNotesUseCase
import javax.inject.Inject

class DeleteNotePresenter @Inject constructor(
        private val deleteNotes: DeleteNotesUseCase
) : DeleteNoteContract.Presenter() {

    override fun onButtonDeleteClick(notesIds: List<String>) {
        addDisposable(deleteNotes.invoke(notesIds)
                .subscribe { view?.closeView() })
    }

    override fun onButtonCancelClick() {
        view?.closeView()
    }
}