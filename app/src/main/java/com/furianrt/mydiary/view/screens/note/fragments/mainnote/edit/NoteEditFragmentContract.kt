package com.furianrt.mydiary.view.screens.note.fragments.mainnote.edit

import com.furianrt.mydiary.view.base.mvp.BaseMvpView
import com.furianrt.mydiary.view.base.mvp.BaseMvpPresenter

interface NoteEditFragmentContract {

    interface MvpView : BaseMvpView {

        fun closeView()

    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {

        abstract fun onDoneButtonClick()
    }
}