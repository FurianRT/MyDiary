package com.furianrt.mydiary.view.screens.settings.note

import com.furianrt.mydiary.view.base.mvp.BaseMvpView
import com.furianrt.mydiary.view.base.mvp.BaseMvpPresenter
import com.furianrt.mydiary.data.model.MyNoteAppearance

interface NoteSettingsContract {

    interface MvpView : BaseMvpView {
        fun updateSettings(appearance: MyNoteAppearance)
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun init(noteId: String)
        abstract fun onTextSizeChange(size: Int)
        abstract fun onTextColorChange(color: Int)
        abstract fun onBackgroundColorChange(color: Int)
        abstract fun onBackgroundTextColorChange(color: Int)
        abstract fun onSurfaceTextColorChange(color: Int)
    }
}