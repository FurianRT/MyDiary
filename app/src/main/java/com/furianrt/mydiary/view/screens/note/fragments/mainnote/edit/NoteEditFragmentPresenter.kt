package com.furianrt.mydiary.view.screens.note.fragments.mainnote.edit

import javax.inject.Inject

class NoteEditFragmentPresenter @Inject constructor() : NoteEditFragmentContract.Presenter() {

    override fun onDoneButtonClick() {
        view?.closeView()
    }
}