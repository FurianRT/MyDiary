package com.furianrt.mydiary.note

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyNote
import com.furianrt.mydiary.data.model.MyNoteAppearance
import com.furianrt.mydiary.data.model.MyNoteWithProp
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

class NoteActivityPresenter(
        private val mDataManager: DataManager
) : NoteActivityContract.Presenter() {

    override fun loadNotes() {
        addDisposable(mDataManager.getAllNotesWithProp()
                .firstOrError()
                .flatMapObservable { Observable.fromIterable(it) }
                .toSortedList { o1, o2 ->
                    return@toSortedList if (mDataManager.isSortDesc()) {
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
        addDisposable(mDataManager.findNote(noteId)
                .switchIfEmpty(mDataManager.insertNote(tempNote)
                        .andThen(mDataManager.insertAppearance(noteAppearance))
                        .toSingleDefault(tempNote))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { note ->
                    view?.showNotes(listOf(MyNoteWithProp(note, null, null, null)))
                })

    }
}