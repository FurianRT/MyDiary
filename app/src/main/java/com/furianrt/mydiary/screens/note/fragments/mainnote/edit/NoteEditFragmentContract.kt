package com.furianrt.mydiary.screens.note.fragments.mainnote.edit

import com.furianrt.mydiary.base.mvp.BaseMvpView
import com.furianrt.mydiary.base.mvp.BaseMvpPresenter

interface NoteEditFragmentContract {

    interface MvpView : BaseMvpView {

        fun closeView()

    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {

        abstract fun onDoneButtonClick()
    }
}