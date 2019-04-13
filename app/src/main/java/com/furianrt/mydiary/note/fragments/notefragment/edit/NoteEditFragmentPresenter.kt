package com.furianrt.mydiary.note.fragments.notefragment.edit

import com.furianrt.mydiary.data.DataManager

class NoteEditFragmentPresenter(private val mDataManager: DataManager)
    : NoteEditFragmentContract.Presenter() {

    override fun onDoneButtonClick() {
        view?.closeView()
    }
}