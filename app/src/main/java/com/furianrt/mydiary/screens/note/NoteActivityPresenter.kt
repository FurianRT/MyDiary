package com.furianrt.mydiary.screens.note

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyNote
import com.furianrt.mydiary.data.model.MyNoteAppearance
import com.furianrt.mydiary.data.model.MyNoteWithProp
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

class NoteActivityPresenter(
        private val dataManager: DataManager
) : NoteActivityContract.Presenter() {

    override fun loadNotes() {
        addDisposable(dataManager.getAllNotesWithProp()
                .firstOrError()
                .flatMapObservable { Observable.fromIterable(it) }
                .toSortedList { o1, o2 ->
                    return@toSortedList if (dataManager.isSortDesc()) {
                        o2.note.time.compareTo(o1.note.time)
                    } else {
                        o1.note.time.compareTo(o2.note.time)
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { notes -> view?.showNotes(notes) })
    }

    override fun loadNote(noteId: String) {
        val tempNote = MyNote(noteId)
        val noteAppearance = MyNoteAppearance(tempNote.id)
        addDisposable(dataManager.findNote(noteId)
                .switchIfEmpty(dataManager.insertNote(tempNote)
                        .andThen(dataManager.insertAppearance(noteAppearance))
                        .toSingleDefault(tempNote))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { note ->
                    view?.showNotes(listOf(MyNoteWithProp(note, null, null, null)))
                })

    }
}