package com.furianrt.mydiary.note.fragments.edit

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyNote
import io.reactivex.disposables.CompositeDisposable

class NoteEditFragmentPresenter(private val mDataManager: DataManager)
    : NoteEditFragmentContract.Presenter {

    private var mView: NoteEditFragmentContract.View? = null
    private val mCompositeDisposable = CompositeDisposable()

    override fun onStop(note: MyNote) {
        //if (!note.title.isEmpty() || !note.content.isEmpty()) {
        val disposable = mDataManager.insertNote(note)
                .subscribe { id -> note.id = id }
        mCompositeDisposable.add(disposable)
       // }
    }

    override fun deleteNote(note: MyNote) {
    }

    override fun attachView(view: NoteEditFragmentContract.View) {
        mView = view
    }

    override fun detachView() {
        mCompositeDisposable.clear()
        mView = null
    }
}