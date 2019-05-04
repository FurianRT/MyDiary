package com.furianrt.mydiary.dialogs.delete.note

import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView

interface DeleteNoteContract {

    interface View : BaseView {
        fun closeView()
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onButtonDeleteClick(notesIds: List<String>)
        abstract fun onButtonCancelClick()
    }
}