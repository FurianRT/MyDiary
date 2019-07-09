package com.furianrt.mydiary.dialogs.delete.note

import com.furianrt.mydiary.data.DataManager
import io.reactivex.Observable
import javax.inject.Inject

class DeleteNotePresenter @Inject constructor(
        private val dataManager: DataManager
) : DeleteNoteContract.Presenter() {

    override fun onButtonDeleteClick(notesIds: List<String>) {
        addDisposable(Observable.fromIterable(notesIds)
                .flatMapCompletable { dataManager.deleteNote(it) }
                .subscribe { view?.closeView() })
    }

    override fun onButtonCancelClick() {
        view?.closeView()
    }
}