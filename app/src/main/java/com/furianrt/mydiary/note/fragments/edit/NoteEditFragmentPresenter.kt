package com.furianrt.mydiary.note.fragments.edit

import android.util.Log
import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyNote

class NoteEditFragmentPresenter(private val mDataManager: DataManager) : NoteEditFragmentContract.Presenter {

    private var mView: NoteEditFragmentContract.View? = null

    override fun addOrUpdateNote(note: MyNote) {
        mDataManager.contains(note.id)
                .subscribe { contains ->
                    if (contains) {
                        mDataManager.updateNote(note)
                                .subscribe()
                        Log.e("ddwdw", "updateNote")
                    } else if (!note.title.isEmpty() && !note.content.isEmpty()) {
                        mDataManager.insertNote(note)
                                .subscribe { id -> note.id = id }
                        Log.e("ddwdw", "insertNote")
                    }
                }
    }

    override fun deleteNote(note: MyNote) {
    }

    override fun attachView(view: NoteEditFragmentContract.View) {
        mView = view
    }

    override fun detachView() {
        mView = null
    }
}