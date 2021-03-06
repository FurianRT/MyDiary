/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.settings.note

import com.furianrt.mydiary.presentation.base.BaseView
import com.furianrt.mydiary.presentation.base.BasePresenter
import com.furianrt.mydiary.model.entity.MyNoteAppearance

interface NoteSettingsContract {

    interface View : BaseView {
        fun updateSettings(appearance: MyNoteAppearance)
        fun onAppearanceReset()
        fun disableInput()
        fun enableInput()
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun init(noteId: String)
        abstract fun onTextSizeChange(size: Int)
        abstract fun onTextColorChange(color: Int)
        abstract fun onBackgroundColorChange(color: Int)
        abstract fun onBackgroundTextColorChange(color: Int)
        abstract fun onSurfaceTextColorChange(color: Int)
        abstract fun onPrefResetSettingsClick()
    }
}