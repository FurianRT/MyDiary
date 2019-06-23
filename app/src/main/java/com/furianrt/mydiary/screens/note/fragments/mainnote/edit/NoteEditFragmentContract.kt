package com.furianrt.mydiary.screens.note.fragments.mainnote.edit

import com.furianrt.mydiary.base.BaseMvpView
import com.furianrt.mydiary.base.BasePresenter

interface NoteEditFragmentContract {

    interface MvpView : BaseMvpView {

        fun closeView()

    }

    abstract class Presenter : BasePresenter<MvpView>() {

        abstract fun onDoneButtonClick()
    }
}