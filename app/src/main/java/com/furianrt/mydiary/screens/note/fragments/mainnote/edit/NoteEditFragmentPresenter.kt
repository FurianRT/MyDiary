package com.furianrt.mydiary.screens.note.fragments.mainnote.edit

import com.furianrt.mydiary.data.DataManager
import javax.inject.Inject

class NoteEditFragmentPresenter @Inject constructor(private val dataManager: DataManager)
    : NoteEditFragmentContract.Presenter() {

    override fun onDoneButtonClick() {
        view?.closeView()
    }
}