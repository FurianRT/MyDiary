package com.furianrt.mydiary.screens.note.fragments.notefragment.edit

import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView

interface NoteEditFragmentContract {

    interface View : BaseView {

        fun closeView()

    }

    abstract class Presenter : BasePresenter<View>() {

        abstract fun onDoneButtonClick()
    }
}