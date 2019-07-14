package com.furianrt.mydiary.screens.note.fragments.mainnote.edit

import javax.inject.Inject

class NoteEditFragmentPresenter @Inject constructor() : NoteEditFragmentContract.Presenter() {

    override fun onDoneButtonClick() {
        view?.closeView()
    }
}