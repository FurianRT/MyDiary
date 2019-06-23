package com.furianrt.mydiary.screens.settings.note

import com.furianrt.mydiary.base.BaseMvpView
import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.data.model.MyNoteAppearance

interface NoteSettingsContract {

    interface MvpView : BaseMvpView {
        fun updateSettings(appearance: MyNoteAppearance)
    }

    abstract class Presenter : BasePresenter<MvpView>() {
        abstract fun onViewCreate(noteId: String?)
        abstract fun onTextSizeChange(size: Int)
        abstract fun onTextColorChange(color: Int)
        abstract fun onBackgroundColorChange(color: Int)
        abstract fun onBackgroundTextColorChange(color: Int)
        abstract fun onSurfaceTextColorChange(color: Int)
    }
}