package com.furianrt.mydiary.note.fragments.content

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyNote
import io.reactivex.disposables.CompositeDisposable

class NoteContentFragmentPresenter(private val mDataManager: DataManager)
    : NoteContentFragmentContract.Presenter {

    private var mView: NoteContentFragmentContract.View? = null
    private val mCompositeDisposable = CompositeDisposable()

    override fun deleteNote(note: MyNote) {
        val disposable = mDataManager.deleteNote(note)
                .subscribe()
        mCompositeDisposable.add(disposable)
    }

    override fun attachView(view: NoteContentFragmentContract.View) {
        mView = view
    }

    override fun detachView() {
        mCompositeDisposable.clear()
        mView = null
    }
}