package com.furianrt.mydiary.screens.note.fragments.mainnote.edit

import com.furianrt.mydiary.data.DataManager

class NoteEditFragmentPresenter(private val dataManager: DataManager)
    : NoteEditFragmentContract.Presenter() {

    override fun onDoneButtonClick() {
        view?.closeView()
    }
}