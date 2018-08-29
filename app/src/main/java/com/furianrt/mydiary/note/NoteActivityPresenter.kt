package com.furianrt.mydiary.note

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyNote
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.*

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
            mView?.showNotes(listOf(MyNote("", "", Date().time)))
        } else if (mode == Mode.READ) {
            mDataManager.getAllNotes()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        mView?.showNotes(it)
                    }
        }
    }

    override fun attachView(view: NoteActivityContract.View) {
        mView = view
    }

    override fun detachView() {
        mView = null
    }
}