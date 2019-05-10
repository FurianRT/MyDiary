package com.furianrt.mydiary.screens.settings.note

import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView
import com.furianrt.mydiary.data.model.MyNoteAppearance

interface NoteSettingsContract {

    interface View : BaseView {
        fun updateSettings(appearance: MyNoteAppearance)
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onViewCreate(noteId: String?)
        abstract fun onTextSizeChange(size: Int)
        abstract fun onTextColorChange(color: Int)
        abstract fun onBackgroundColorChange(color: Int)
        abstract fun onBackgroundTextColorChange(color: Int)
        abstract fun onSurfaceTextColorChange(color: Int)
    }
}