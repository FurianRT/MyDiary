/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.screens.settings.note

import com.furianrt.mydiary.view.base.mvp.BaseMvpView
import com.furianrt.mydiary.view.base.mvp.BaseMvpPresenter
import com.furianrt.mydiary.data.model.MyNoteAppearance

interface NoteSettingsContract {

    interface MvpView : BaseMvpView {
        fun updateSettings(appearance: MyNoteAppearance)
        fun onAppearanceReset()
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun init(noteId: String)
        abstract fun onTextSizeChange(size: Int)
        abstract fun onTextColorChange(color: Int)
        abstract fun onBackgroundColorChange(color: Int)
        abstract fun onBackgroundTextColorChange(color: Int)
        abstract fun onSurfaceTextColorChange(color: Int)
        abstract fun onPrefResetSettingsClick()
    }
}