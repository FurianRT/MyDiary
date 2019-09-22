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

import javax.inject.Inject

class NoteEditFragmentPresenter @Inject constructor() : NoteEditFragmentContract.Presenter() {

    override fun onDoneButtonClick() {
        view?.closeView()
    }

    override fun onButtonTextBoldClick(selectionStart: Int, selectionEnd: Int, text: String?) {
        text?.let {
            val selection = getWordIndexes(selectionStart, selectionEnd, it)
            view?.applyBoldText(selection.first, selection.second)
        }
    }

    override fun onButtonTextItalicClick(selectionStart: Int, selectionEnd: Int, text: String?) {
        text?.let {
            val selection = getWordIndexes(selectionStart, selectionEnd, it)
            view?.applyItalicText(selection.first, selection.second)
        }
    }

    override fun onButtonTextStrikethroughClick(selectionStart: Int, selectionEnd: Int, text: String?) {
        text?.let {
            val selection = getWordIndexes(selectionStart, selectionEnd, it)
            view?.applyStrikethroughText(selection.first, selection.second)
        }
    }

    override fun onButtonTextLargeClick(selectionStart: Int, selectionEnd: Int, text: String?) {
        text?.let {
            val selection = getWordIndexes(selectionStart, selectionEnd, it)
            view?.applyLargeText(selection.first, selection.second)
        }
    }

    override fun onButtonTextColorClick(selectionStart: Int, selectionEnd: Int, text: String?, color: String?) {
        text?.let {
            val selection = getWordIndexes(selectionStart, selectionEnd, it)
            view?.applyTextColor(selection.first, selection.second, color)
        }
    }

    override fun onButtonTextFillColorClick(selectionStart: Int, selectionEnd: Int, text: String?, color: String?) {
        text?.let {
            val selection = getWordIndexes(selectionStart, selectionEnd, it)
            view?.applyTextFillColor(selection.first, selection.second, color)
        }
    }

    private fun getWordIndexes(selectionStart: Int, selectionEnd: Int, text: String): Pair<Int, Int> {
        if (text.isEmpty() || selectionStart != selectionEnd) {
            return Pair(selectionStart, selectionEnd)
        }
        var indexStart = selectionStart
        var indexEnd = selectionEnd
        if (indexStart > 0) {
            indexStart--
        }

        for (i in selectionStart..text.length) {
            indexEnd = i
            if (i == text.length || text[i] == ' ' || text[i] == '\n') {
                break
            }
        }
        for (i in indexStart downTo 0) {
            if (text[i] == ' ' || text[i] == '\n') {
                break
            }
            indexStart = i
        }

        return Pair(indexStart, indexEnd)
    }
}