package com.furianrt.mydiary.note

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyNote
import com.furianrt.mydiary.data.model.MyNoteWithProp
import com.furianrt.mydiary.utils.generateUniqueId

class NoteActivityPresenter(private val mDataManager: DataManager) : NoteActivityContract.Presenter {

    private var mView: NoteActivityContract.View? = null

    override fun addNote(note: MyNote) {
        mDataManager.insertNote(note)
                .subscribe()
    }

    override fun deleteNote(note: MyNote) {
        mDataManager.deleteNote(note)
                .subscribe()
    }

    override fun loadNotes(mode: Mode) {
        if (mode == Mode.ADD) {
            mView?.showNotes(listOf(MyNoteWithProp(generateUniqueId())))
        } else if (mode == Mode.READ) {
            mDataManager.getNotesWithProp()
                    .subscribe { mView?.showNotes(it) }
        }
    }

    override fun attachView(view: NoteActivityContract.View) {
        mView = view
    }

    override fun detachView() {
        mView = null
    }
}