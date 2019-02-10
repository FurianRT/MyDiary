package com.furianrt.mydiary.note.fragments.notefragment.edit

import com.furianrt.mydiary.BasePresenter
import com.furianrt.mydiary.BaseView

interface NoteEditFragmentContract {

    interface View : BaseView {

        fun closeView()

    }

    abstract class Presenter : BasePresenter<View>() {

        abstract fun onDoneButtonClick()
    }
}