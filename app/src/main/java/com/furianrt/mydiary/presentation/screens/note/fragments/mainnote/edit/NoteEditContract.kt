/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.note.fragments.mainnote.edit

import com.furianrt.mydiary.model.entity.MyTextSpan
import com.furianrt.mydiary.presentation.base.BaseView
import com.furianrt.mydiary.presentation.base.BasePresenter

interface NoteEditContract {

    interface View : BaseView {
        fun closeView()
        fun applyBoldText(wordStart: Int, wordEnd: Int)
        fun applyItalicText(wordStart: Int, wordEnd: Int)
        fun applyStrikethroughText(wordStart: Int, wordEnd: Int)
        fun applyLargeText(wordStart: Int, wordEnd: Int)
        fun applyTextColor(wordStart: Int, wordEnd: Int, color: String?)
        fun applyTextFillColor(wordStart: Int, wordEnd: Int, color: String?)
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onDoneButtonClick()
        abstract fun onButtonTextBoldClick(selectionStart: Int, selectionEnd: Int, text: String?)
        abstract fun onButtonTextItalicClick(selectionStart: Int, selectionEnd: Int, text: String?)
        abstract fun onButtonTextStrikethroughClick(selectionStart: Int, selectionEnd: Int, text: String?)
        abstract fun onButtonTextLargeClick(selectionStart: Int, selectionEnd: Int, text: String?)
        abstract fun onButtonTextColorClick(selectionStart: Int, selectionEnd: Int, text: String?, color: String?)
        abstract fun onButtonTextFillColorClick(selectionStart: Int, selectionEnd: Int, text: String?, color: String?)
        abstract fun onViewStopped(noteId: String, title: String, content: String, textSpans: List<MyTextSpan>)
    }
}