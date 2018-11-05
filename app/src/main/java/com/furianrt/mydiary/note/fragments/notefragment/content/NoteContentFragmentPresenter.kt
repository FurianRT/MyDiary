package com.furianrt.mydiary.note.fragments.notefragment.content

import com.furianrt.mydiary.data.DataManager
import io.reactivex.disposables.CompositeDisposable

class NoteContentFragmentPresenter(private val mDataManager: DataManager)
    : NoteContentFragmentContract.Presenter {

    private var mView: NoteContentFragmentContract.View? = null
    private val mCompositeDisposable = CompositeDisposable()

    override fun attachView(view: NoteContentFragmentContract.View) {
        mView = view
    }

    override fun detachView() {
        mCompositeDisposable.clear()
        mView = null
    }
}