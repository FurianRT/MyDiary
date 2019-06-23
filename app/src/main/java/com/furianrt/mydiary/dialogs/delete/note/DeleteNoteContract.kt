package com.furianrt.mydiary.dialogs.delete.note

import com.furianrt.mydiary.base.BaseMvpView
import com.furianrt.mydiary.base.BasePresenter

interface DeleteNoteContract {

    interface MvpView : BaseMvpView {
        fun closeView()
    }

    abstract class Presenter : BasePresenter<MvpView>() {
        abstract fun onButtonDeleteClick(notesIds: List<String>)
        abstract fun onButtonCancelClick()
    }
}