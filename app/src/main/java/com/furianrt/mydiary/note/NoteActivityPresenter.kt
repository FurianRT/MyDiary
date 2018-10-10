package com.furianrt.mydiary.note

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyNoteWithProp
import com.furianrt.mydiary.utils.generateUniqueId
import io.reactivex.disposables.CompositeDisposable

class NoteActivityPresenter(private val mDataManager: DataManager) : NoteActivityContract.Presenter {

    private var mView: NoteActivityContract.View? = null
    private val mCompositeDisposable = CompositeDisposable()

    override fun loadNotes(mode: Mode) {
        if (mode == Mode.ADD) {
            mView?.showNotes(listOf(MyNoteWithProp(generateUniqueId())))
        } else if (mode == Mode.READ) {
            val disposable = mDataManager.getAllNotesWithProp()
                    .subscribe { mView?.showNotes(it) }

            mCompositeDisposable.add(disposable)
        }
    }

    override fun attachView(view: NoteActivityContract.View) {
        mView = view
    }

    override fun detachView() {
        mCompositeDisposable.clear()
        mView = null
    }
}