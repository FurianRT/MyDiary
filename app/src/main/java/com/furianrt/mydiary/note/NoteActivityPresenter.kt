package com.furianrt.mydiary.note

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyNote
import com.furianrt.mydiary.data.model.MyNoteWithProp
import io.reactivex.disposables.CompositeDisposable

class NoteActivityPresenter(private val mDataManager: DataManager) : NoteActivityContract.Presenter {

    private var mView: NoteActivityContract.View? = null
    private val mCompositeDisposable = CompositeDisposable()

    override fun loadNotes() {
        val disposable = mDataManager.getAllNotesWithProp()
                .subscribe { mView?.showNotes(it) }

        mCompositeDisposable.add(disposable)
    }

    override fun loadNote(noteId: String) {
        val tempNote = MyNote(noteId, "", "")
        val disposable = mDataManager.findNote(noteId)
                .switchIfEmpty(mDataManager.insertNote(tempNote).toSingleDefault(tempNote))
                .subscribe { note ->
                    mView?.showNotes(listOf(MyNoteWithProp(note, null, null, null)))
                }

        mCompositeDisposable.add(disposable)
    }

    override fun attachView(view: NoteActivityContract.View) {
        mView = view
    }

    override fun detachView() {
        mCompositeDisposable.clear()
        mView = null
    }
}