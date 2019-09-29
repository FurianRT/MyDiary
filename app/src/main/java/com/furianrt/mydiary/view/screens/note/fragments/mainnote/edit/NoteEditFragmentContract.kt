/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.screens.note.fragments.mainnote.edit

import com.furianrt.mydiary.view.base.mvp.BaseMvpView
import com.furianrt.mydiary.view.base.mvp.BaseMvpPresenter

interface NoteEditFragmentContract {

    interface MvpView : BaseMvpView {
        fun closeView()
        fun applyBoldText(wordStart: Int, wordEnd: Int)
        fun applyItalicText(wordStart: Int, wordEnd: Int)
        fun applyStrikethroughText(wordStart: Int, wordEnd: Int)
        fun applyLargeText(wordStart: Int, wordEnd: Int)
        fun applyTextColor(wordStart: Int, wordEnd: Int, color: String?)
        fun applyTextFillColor(wordStart: Int, wordEnd: Int, color: String?)
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun onDoneButtonClick()
        abstract fun onButtonTextBoldClick(selectionStart: Int, selectionEnd: Int, text: String?)
        abstract fun onButtonTextItalicClick(selectionStart: Int, selectionEnd: Int, text: String?)
        abstract fun onButtonTextStrikethroughClick(selectionStart: Int, selectionEnd: Int, text: String?)
        abstract fun onButtonTextLargeClick(selectionStart: Int, selectionEnd: Int, text: String?)
        abstract fun onButtonTextColorClick(selectionStart: Int, selectionEnd: Int, text: String?, color: String?)
        abstract fun onButtonTextFillColorClick(selectionStart: Int, selectionEnd: Int, text: String?, color: String?)
    }
}