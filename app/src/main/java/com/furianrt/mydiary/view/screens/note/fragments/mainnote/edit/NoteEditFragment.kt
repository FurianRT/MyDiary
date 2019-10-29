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

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.*
import android.text.style.*
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.graphics.ColorUtils
import com.furianrt.mydiary.R
import com.furianrt.mydiary.analytics.MyAnalytics
import com.furianrt.mydiary.view.base.BaseFragment
import com.furianrt.mydiary.model.entity.MyNoteAppearance
import com.furianrt.mydiary.utils.hideKeyboard
import com.furianrt.mydiary.view.screens.note.fragments.mainnote.NoteFragment
import com.furianrt.mydiary.utils.showKeyboard
import kotlinx.android.synthetic.main.fragment_note_edit.*
import javax.inject.Inject

class NoteEditFragment : BaseFragment(R.layout.fragment_note_edit), NoteEditFragmentContract.MvpView {

    private var mClickedView: Int? = null
    private var mClickPosition = 0
    private var mNoteTitle = ""
    private var mNoteContent = Spannable.Factory().newSpannable("")
    private var mAppearance: MyNoteAppearance? = null
    private val mTextChangeListener = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            (parentFragment as NoteFragment).onNoteTextChange(
                    edit_note_title.text?.toString() ?: "",
                    edit_note_content.text ?: Spannable.Factory().newSpannable("")
            )
        }
    }

    @Inject
    lateinit var mPresenter: NoteEditFragmentContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        mNoteTitle = requireArguments().getString(ARG_NOTE_TITLE, "")
        mNoteContent = requireArguments().getCharSequence(ARG_NOTE_CONTENT, Spannable.Factory().newSpannable("")) as Spannable
        mClickedView = requireArguments().getInt(ARG_CLICKED_VIEW)
        mClickPosition = requireArguments().getInt(ARG_POSITION)
        mAppearance = requireArguments().getParcelable(ARG_APPEARANCE) as? MyNoteAppearance?
        if (savedInstanceState != null) {
            mClickedView = null
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            edit_note_title.setText(mNoteTitle)
            edit_note_content.setText(mNoteContent, TextView.BufferType.SPANNABLE)
        }

        edit_note_content.enableLines = true
        edit_note_content.selectionListener = { selStart, selEnd -> onSpansChange(selStart, selEnd) }
        edit_note_content.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                (parentFragment as? NoteFragment?)?.showRichTextOptions()
            } else {
                (parentFragment as? NoteFragment?)?.hideRichTextOptions()
            }
        }

        edit_note_title.imeOptions = EditorInfo.IME_ACTION_DONE
        edit_note_title.setRawInputType(InputType.TYPE_CLASS_TEXT)

        mAppearance?.let { setAppearance(it) }

        if (savedInstanceState == null) {
            when (mClickedView) {
                VIEW_TITLE -> {
                    edit_note_title.requestFocus()
                    edit_note_title.setSelection(mClickPosition)
                    edit_note_title.showKeyboard()

                }
                VIEW_CONTENT -> {
                    edit_note_content.requestFocus()
                    edit_note_content.setSelection(mClickPosition)
                    edit_note_content.showKeyboard()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.removeItem(R.id.menu_done)
        menu.removeItem(R.id.menu_edit)
        menu.removeItem(R.id.menu_delete)
        inflater.inflate(R.menu.fragment_edit_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
            when (item.itemId) {
                R.id.menu_done -> {
                    mPresenter.onDoneButtonClick()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }

    override fun closeView() {
        activity?.currentFocus?.let { focus ->
            focus.hideKeyboard()
            focus.clearFocus()
        }
        fragmentManager?.popBackStack()
    }

    override fun onResume() {
        super.onResume()
        if (edit_note_content.hasFocus()) {
            (parentFragment as? NoteFragment?)?.showRichTextOptions()
        } else {
            (parentFragment as? NoteFragment?)?.hideRichTextOptions()
        }
    }

    override fun onStart() {
        super.onStart()
        mPresenter.attachView(this)
        (parentFragment as? NoteFragment?)?.onNoteFragmentEditModeEnabled()
        edit_note_title.addTextChangedListener(mTextChangeListener)
        edit_note_content.addTextChangedListener(mTextChangeListener)
    }

    override fun onStop() {
        super.onStop()
        edit_note_title.removeTextChangedListener(mTextChangeListener)
        edit_note_content.removeTextChangedListener(mTextChangeListener)
        (parentFragment as? NoteFragment?)?.onNoteFragmentEditModeDisabled(
                edit_note_title.text?.toString() ?: "",
                edit_note_content.text ?: Spannable.Factory().newSpannable("")
        )
        mPresenter.detachView()
    }

    fun setAppearance(appearance: MyNoteAppearance) {
        appearance.textColor?.let {
            edit_note_title.setTextColor(it)
            edit_note_content.setTextColor(it)
            edit_note_title.setHintTextColor(ColorUtils.setAlphaComponent(it, 50))
            edit_note_content.setHintTextColor(ColorUtils.setAlphaComponent(it, 50))
            edit_note_content.setLineColor(ColorUtils.setAlphaComponent(it, 35))
        }
        appearance.textSize?.let {
            edit_note_title.textSize = it.toFloat()
            edit_note_content.textSize = it.toFloat()
        }
    }

    fun showNoteText(title: String, content: Spannable) {
        Log.e(TAG, "showNoteText")
        // Отключаем листенер что бы в undo/redo не прилетал измененный им же текст
        edit_note_title.removeTextChangedListener(mTextChangeListener)
        edit_note_content.removeTextChangedListener(mTextChangeListener)
        // Клава с SUGGESTIONS кэширует текст и делает странные вещи при undo/redo.
        // Приходится отключать SUGGESTIONS при программном изменении текста
        val currentTitleInputType = edit_note_title.inputType
        val currentContentInputType = edit_note_content.inputType
        val selectionStart: Int
        val selectionEnd: Int
        if (edit_note_title.hasFocus()) {
            selectionStart = edit_note_title.selectionStart
            selectionEnd = edit_note_title.selectionEnd
        } else {
            selectionStart = edit_note_content.selectionStart
            selectionEnd = edit_note_content.selectionEnd
        }

        edit_note_title.inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        edit_note_content.inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS

        edit_note_title.setText(title)
        edit_note_content.setText(content, TextView.BufferType.SPANNABLE)

        edit_note_title.inputType = currentTitleInputType
        edit_note_content.inputType = currentContentInputType

        if (edit_note_title.hasFocus()) {
            val textLength = edit_note_title.text?.length ?: 0
            if (textLength < selectionEnd) {
                edit_note_title.setSelection(textLength)
            } else {
                edit_note_title.setSelection(selectionStart, selectionEnd)
            }
        } else if (edit_note_content.hasFocus()) {
            val textLength = edit_note_content.text?.length ?: 0
            if (textLength < selectionEnd) {
                edit_note_content.setSelection(textLength)
            } else {
                edit_note_content.setSelection(selectionStart, selectionEnd)
            }
        }

        edit_note_title.addTextChangedListener(mTextChangeListener)
        edit_note_content.addTextChangedListener(mTextChangeListener)
    }

    fun getNoteTitleText(): String = edit_note_title.text?.toString() ?: ""

    fun getNoteContentText(): Spannable = edit_note_content.text
            ?: Spannable.Factory().newSpannable("")

    override fun applyBoldText(wordStart: Int, wordEnd: Int) {
        val text = edit_note_content.text ?: return

        val boldSpans = text.getSpans(
                edit_note_content.selectionStart,
                edit_note_content.selectionEnd,
                StyleSpan::class.java
        ).filter { it.style == Typeface.BOLD }

        if (boldSpans.isEmpty()) {
            addBoldTextSpan(wordStart, wordEnd, text)
        } else {
            removeBoldTextSpan(wordStart, wordEnd, boldSpans, text)
        }

        (parentFragment as NoteFragment).onNoteTextChange(
                edit_note_title.text?.toString() ?: "",
                text
        )

        onSpansChange(edit_note_content.selectionStart, edit_note_content.selectionEnd)
    }

    override fun applyItalicText(wordStart: Int, wordEnd: Int) {
        val text = edit_note_content.text ?: return
        val italicSpans = text.getSpans(
                edit_note_content.selectionStart,
                edit_note_content.selectionEnd,
                StyleSpan::class.java
        ).filter { it.style == Typeface.ITALIC }

        if (italicSpans.isEmpty()) {
            addItalicTextSpan(wordStart, wordEnd, text)
        } else {
            removeItalicTextSpan(wordStart, wordEnd, italicSpans, text)
        }

        (parentFragment as NoteFragment).onNoteTextChange(
                edit_note_title.text?.toString() ?: "",
                text
        )

        onSpansChange(edit_note_content.selectionStart, edit_note_content.selectionEnd)
    }

    override fun applyStrikethroughText(wordStart: Int, wordEnd: Int) {
        val text = edit_note_content.text ?: return
        val strikethroughSpans = text.getSpans(
                edit_note_content.selectionStart,
                edit_note_content.selectionEnd,
                StrikethroughSpan::class.java
        ).toList()

        if (strikethroughSpans.isEmpty()) {
            addStrikethroughTextSpan(wordStart, wordEnd, text)
        } else {
            removeStrikethroughTextSpan(wordStart, wordEnd, strikethroughSpans, text)
        }

        (parentFragment as NoteFragment).onNoteTextChange(
                edit_note_title.text?.toString() ?: "",
                text
        )

        onSpansChange(edit_note_content.selectionStart, edit_note_content.selectionEnd)
    }

    override fun applyLargeText(wordStart: Int, wordEnd: Int) {
        val text = edit_note_content.text ?: return
        val largeSpans = text.getSpans(
                edit_note_content.selectionStart,
                edit_note_content.selectionEnd,
                RelativeSizeSpan::class.java
        ).toList()

        if (largeSpans.isEmpty()) {
            addLargeTextSpan(wordStart, wordEnd, text)
        } else {
            removeLargeTextSpan(wordStart, wordEnd, largeSpans, text)
        }

        (parentFragment as NoteFragment).onNoteTextChange(
                edit_note_title.text?.toString() ?: "",
                text
        )

        onSpansChange(edit_note_content.selectionStart, edit_note_content.selectionEnd)
    }

    override fun applyTextColor(wordStart: Int, wordEnd: Int, color: String?) {
        val text = edit_note_content.text ?: return
        val textColorSpans = text.getSpans(
                edit_note_content.selectionStart,
                edit_note_content.selectionEnd,
                ForegroundColorSpan::class.java
        ).toList()

        if (textColorSpans.isEmpty()) {
            color?.let { addTextColorSpan(wordStart, wordEnd, text, it) }
        } else {
            removeTextColorSpan(wordStart, wordEnd, textColorSpans, text)
        }

        (parentFragment as NoteFragment).onNoteTextChange(
                edit_note_title.text?.toString() ?: "",
                text
        )

        onSpansChange(edit_note_content.selectionStart, edit_note_content.selectionEnd)
    }

    override fun applyTextFillColor(wordStart: Int, wordEnd: Int, color: String?) {
        val text = edit_note_content.text ?: return
        val textColorFillSpans = text.getSpans(
                edit_note_content.selectionStart,
                edit_note_content.selectionEnd,
                BackgroundColorSpan::class.java
        ).toList()

        if (textColorFillSpans.isEmpty()) {
            color?.let { addTextFillColorSpan(wordStart, wordEnd, text, it) }
        } else {
            removeTextFillColorSpan(wordStart, wordEnd, textColorFillSpans, text)
        }

        (parentFragment as NoteFragment).onNoteTextChange(
                edit_note_title.text?.toString() ?: "",
                text
        )

        onSpansChange(edit_note_content.selectionStart, edit_note_content.selectionEnd)
    }

    private fun addBoldTextSpan(wordStart: Int, wordEnd: Int, text: Spannable) {
        analytics.sendEvent(MyAnalytics.EVENT_RICH_TEXT_BOLD_APPLIED)
        var indexStart = wordStart
        var indexEnd = wordEnd

        val prevSpanPosition = text.getSpans(indexStart, indexEnd, StyleSpan::class.java)
                .filter { it.style == Typeface.BOLD }
                .map { text.getSpanEnd(it) }
                .filter { it < edit_note_content.selectionStart }
                .max()

        val nextSpanPosition = text.getSpans(indexStart, indexEnd, StyleSpan::class.java)
                .filter { it.style == Typeface.BOLD }
                .map { text.getSpanStart(it) }
                .filter { it > edit_note_content.selectionEnd }
                .min()

        if (prevSpanPosition != null && prevSpanPosition != indexStart) {
            indexStart = prevSpanPosition
        }

        if (nextSpanPosition != null && nextSpanPosition != indexEnd) {
            indexEnd = nextSpanPosition - 1
        }

        edit_note_content.text?.setSpan(
                StyleSpan(Typeface.BOLD),
                indexStart,
                indexEnd,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
    }

    private fun removeBoldTextSpan(wordStart: Int, wordEnd: Int, boldSpans: List<StyleSpan>, text: Spannable) {
        if (text.isEmpty()) {
            boldSpans.forEach { text.removeSpan(it) }
            return
        }

        boldSpans.forEach { span ->
            val spanStart = text.getSpanStart(span)
            val spanEnd = text.getSpanEnd(span)

            when {
                spanStart < wordStart && spanEnd > wordEnd -> {
                    text.setSpan(StyleSpan(Typeface.BOLD), spanStart, wordStart, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                    text.setSpan(StyleSpan(Typeface.BOLD), wordEnd, spanEnd, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                }
                spanStart < wordStart && spanEnd <= wordEnd ->
                    text.setSpan(StyleSpan(Typeface.BOLD), spanStart, wordStart, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                spanStart >= wordStart && spanEnd > wordEnd ->
                    text.setSpan(StyleSpan(Typeface.BOLD), wordEnd, spanEnd, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
            }
            text.removeSpan(span)
        }
    }

    private fun addItalicTextSpan(wordStart: Int, wordEnd: Int, text: Spannable) {
        analytics.sendEvent(MyAnalytics.EVENT_RICH_TEXT_ITALIC_APPLIED)
        var indexStart = wordStart
        var indexEnd = wordEnd

        val prevSpanPosition = text.getSpans(indexStart, indexEnd, StyleSpan::class.java)
                .filter { it.style == Typeface.ITALIC }
                .map { text.getSpanEnd(it) }
                .filter { it < edit_note_content.selectionStart }
                .max()

        val nextSpanPosition = text.getSpans(indexStart, indexEnd, StyleSpan::class.java)
                .filter { it.style == Typeface.ITALIC }
                .map { text.getSpanStart(it) }
                .filter { it > edit_note_content.selectionEnd }
                .min()

        if (prevSpanPosition != null && prevSpanPosition != indexStart) {
            indexStart = prevSpanPosition
        }

        if (nextSpanPosition != null && nextSpanPosition != indexEnd) {
            indexEnd = nextSpanPosition - 1
        }

        edit_note_content.text?.setSpan(
                StyleSpan(Typeface.ITALIC),
                indexStart,
                indexEnd,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
    }

    private fun removeItalicTextSpan(wordStart: Int, wordEnd: Int, italicSpans: List<StyleSpan>, text: Spannable) {
        if (text.isEmpty()) {
            italicSpans.forEach { text.removeSpan(it) }
            return
        }

        italicSpans.forEach { span ->
            val spanStart = text.getSpanStart(span)
            val spanEnd = text.getSpanEnd(span)

            when {
                spanStart < wordStart && spanEnd > wordEnd -> {
                    text.setSpan(StyleSpan(Typeface.ITALIC), spanStart, wordStart, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                    text.setSpan(StyleSpan(Typeface.ITALIC), wordEnd, spanEnd, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                }
                spanStart < wordStart && spanEnd <= wordEnd ->
                    text.setSpan(StyleSpan(Typeface.ITALIC), spanStart, wordStart, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                spanStart >= wordStart && spanEnd > wordEnd ->
                    text.setSpan(StyleSpan(Typeface.ITALIC), wordEnd, spanEnd, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
            }
            text.removeSpan(span)
        }
    }

    private fun addStrikethroughTextSpan(wordStart: Int, wordEnd: Int, text: Spannable) {
        analytics.sendEvent(MyAnalytics.EVENT_RICH_TEXT_STRIKETHROUGH_APPLIED)
        var indexStart = wordStart
        var indexEnd = wordEnd

        val prevSpanPosition = text.getSpans(indexStart, indexEnd, StrikethroughSpan::class.java)
                .map { text.getSpanEnd(it) }
                .filter { it < edit_note_content.selectionStart }
                .max()

        val nextSpanPosition = text.getSpans(indexStart, indexEnd, StrikethroughSpan::class.java)
                .map { text.getSpanStart(it) }
                .filter { it > edit_note_content.selectionEnd }
                .min()

        if (prevSpanPosition != null && prevSpanPosition != indexStart) {
            indexStart = prevSpanPosition
        }

        if (nextSpanPosition != null && nextSpanPosition != indexEnd) {
            indexEnd = nextSpanPosition - 1
        }

        edit_note_content.text?.setSpan(
                StrikethroughSpan(),
                indexStart,
                indexEnd,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
    }

    private fun removeStrikethroughTextSpan(wordStart: Int, wordEnd: Int, strikethroughSpans: List<StrikethroughSpan>, text: Spannable) {
        if (text.isEmpty()) {
            strikethroughSpans.forEach { text.removeSpan(it) }
            return
        }

        strikethroughSpans.forEach { span ->
            val spanStart = text.getSpanStart(span)
            val spanEnd = text.getSpanEnd(span)

            when {
                spanStart < wordStart && spanEnd > wordEnd -> {
                    text.setSpan(StrikethroughSpan(), spanStart, wordStart, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                    text.setSpan(StrikethroughSpan(), wordEnd, spanEnd, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                }
                spanStart < wordStart && spanEnd <= wordEnd ->
                    text.setSpan(StrikethroughSpan(), spanStart, wordStart, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                spanStart >= wordStart && spanEnd > wordEnd ->
                    text.setSpan(StrikethroughSpan(), wordEnd, spanEnd, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
            }
            text.removeSpan(span)
        }
    }

    private fun addLargeTextSpan(wordStart: Int, wordEnd: Int, text: Spannable) {
        analytics.sendEvent(MyAnalytics.EVENT_RICH_TEXT_BIG_TEXT_APPLIED)
        var indexStart = wordStart
        var indexEnd = wordEnd

        val prevSpanPosition = text.getSpans(indexStart, indexEnd, RelativeSizeSpan::class.java)
                .map { text.getSpanEnd(it) }
                .filter { it < edit_note_content.selectionStart }
                .max()

        val nextSpanPosition = text.getSpans(indexStart, indexEnd, RelativeSizeSpan::class.java)
                .map { text.getSpanStart(it) }
                .filter { it > edit_note_content.selectionEnd }
                .min()

        if (prevSpanPosition != null && prevSpanPosition != indexStart) {
            indexStart = prevSpanPosition
        }

        if (nextSpanPosition != null && nextSpanPosition != indexEnd) {
            indexEnd = nextSpanPosition - 1
        }

        edit_note_content.text?.setSpan(
                RelativeSizeSpan(SPAN_LARGE_TEXT_SIZE),
                indexStart,
                indexEnd,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
    }

    private fun removeLargeTextSpan(wordStart: Int, wordEnd: Int, largeTextSpans: List<RelativeSizeSpan>, text: Spannable) {
        if (text.isEmpty()) {
            largeTextSpans.forEach { text.removeSpan(it) }
            return
        }

        largeTextSpans.forEach { span ->
            val spanStart = text.getSpanStart(span)
            val spanEnd = text.getSpanEnd(span)

            when {
                spanStart < wordStart && spanEnd > wordEnd -> {
                    text.setSpan(
                            RelativeSizeSpan(span.sizeChange),
                            spanStart,
                            wordStart,
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                    )
                    text.setSpan(
                            RelativeSizeSpan(span.sizeChange),
                            wordEnd,
                            spanEnd,
                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                    )
                }
                spanStart < wordStart && spanEnd <= wordEnd ->
                    text.setSpan(
                            RelativeSizeSpan(span.sizeChange),
                            spanStart,
                            wordStart,
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                    )
                spanStart >= wordStart && spanEnd > wordEnd ->
                    text.setSpan(
                            RelativeSizeSpan(span.sizeChange),
                            wordEnd,
                            spanEnd,
                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                    )
            }
            text.removeSpan(span)
        }
    }

    private fun addTextColorSpan(wordStart: Int, wordEnd: Int, text: Spannable, color: String) {
        analytics.sendEvent(MyAnalytics.EVENT_RICH_TEXT_COLOR_APPLIED)
        var indexStart = wordStart
        var indexEnd = wordEnd

        val prevSpanPosition = text.getSpans(indexStart, indexEnd, ForegroundColorSpan::class.java)
                .map { text.getSpanEnd(it) }
                .filter { it < edit_note_content.selectionStart }
                .max()

        val nextSpanPosition = text.getSpans(indexStart, indexEnd, ForegroundColorSpan::class.java)
                .map { text.getSpanStart(it) }
                .filter { it > edit_note_content.selectionEnd }
                .min()

        if (prevSpanPosition != null && prevSpanPosition != indexStart) {
            indexStart = prevSpanPosition
        }

        if (nextSpanPosition != null && nextSpanPosition != indexEnd) {
            indexEnd = nextSpanPosition - 1
        }

        edit_note_content.text?.setSpan(
                ForegroundColorSpan(Color.parseColor(color)),
                indexStart,
                indexEnd,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
    }

    private fun removeTextColorSpan(wordStart: Int, wordEnd: Int, textColorSpans: List<ForegroundColorSpan>, text: Spannable) {
        if (text.isEmpty()) {
            textColorSpans.forEach { text.removeSpan(it) }
            return
        }

        textColorSpans.forEach { span ->
            val spanStart = text.getSpanStart(span)
            val spanEnd = text.getSpanEnd(span)

            when {
                spanStart < wordStart && spanEnd > wordEnd -> {
                    text.setSpan(
                            ForegroundColorSpan(span.foregroundColor),
                            spanStart,
                            wordStart,
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                    )
                    text.setSpan(
                            ForegroundColorSpan(span.foregroundColor),
                            wordEnd,
                            spanEnd,
                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                    )
                }
                spanStart < wordStart && spanEnd <= wordEnd ->
                    text.setSpan(
                            ForegroundColorSpan(span.foregroundColor),
                            spanStart,
                            wordStart,
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                    )
                spanStart >= wordStart && spanEnd > wordEnd ->
                    text.setSpan(
                            ForegroundColorSpan(span.foregroundColor),
                            wordEnd,
                            spanEnd,
                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                    )
            }
            text.removeSpan(span)
        }
    }

    private fun addTextFillColorSpan(wordStart: Int, wordEnd: Int, text: Spannable, color: String) {
        analytics.sendEvent(MyAnalytics.EVENT_RICH_TEXT_FILL_COLOR_APPLIED)
        var indexStart = wordStart
        var indexEnd = wordEnd

        val prevSpanPosition = text.getSpans(indexStart, indexEnd, BackgroundColorSpan::class.java)
                .map { text.getSpanEnd(it) }
                .filter { it < edit_note_content.selectionStart }
                .max()

        val nextSpanPosition = text.getSpans(indexStart, indexEnd, BackgroundColorSpan::class.java)
                .map { text.getSpanStart(it) }
                .filter { it > edit_note_content.selectionEnd }
                .min()

        if (prevSpanPosition != null && prevSpanPosition != indexStart) {
            indexStart = prevSpanPosition
        }

        if (nextSpanPosition != null && nextSpanPosition != indexEnd) {
            indexEnd = nextSpanPosition - 1
        }

        edit_note_content.text?.setSpan(
                BackgroundColorSpan(Color.parseColor(color)),
                indexStart,
                indexEnd,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
    }

    private fun removeTextFillColorSpan(wordStart: Int, wordEnd: Int, textFillColorSpans: List<BackgroundColorSpan>, text: Spannable) {
        if (text.isEmpty()) {
            textFillColorSpans.forEach { text.removeSpan(it) }
            return
        }

        textFillColorSpans.forEach { span ->
            val spanStart = text.getSpanStart(span)
            val spanEnd = text.getSpanEnd(span)

            when {
                spanStart < wordStart && spanEnd > wordEnd -> {
                    text.setSpan(
                            BackgroundColorSpan(span.backgroundColor),
                            spanStart,
                            wordStart,
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                    )
                    text.setSpan(
                            BackgroundColorSpan(span.backgroundColor),
                            wordEnd,
                            spanEnd,
                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                    )
                }
                spanStart < wordStart && spanEnd <= wordEnd ->
                    text.setSpan(
                            BackgroundColorSpan(span.backgroundColor),
                            spanStart,
                            wordStart,
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                    )
                spanStart >= wordStart && spanEnd > wordEnd ->
                    text.setSpan(
                            BackgroundColorSpan(span.backgroundColor),
                            wordEnd,
                            spanEnd,
                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                    )
            }
            text.removeSpan(span)
        }
    }

    private fun onSpansChange(selectionStart: Int, selectionEnd: Int) {
        edit_note_content?.text?.let { text ->
            val spans = text.getSpans(selectionStart, selectionEnd, ParcelableSpan::class.java)
            (parentFragment as NoteFragment?)?.onSpansChange(spans)
        }
    }

    fun onButtonTextBoldClick() {
        mPresenter.onButtonTextBoldClick(
                edit_note_content.selectionStart,
                edit_note_content.selectionEnd,
                edit_note_content.text?.toString()
        )
    }

    fun onButtonTextItalicClick() {
        mPresenter.onButtonTextItalicClick(
                edit_note_content.selectionStart,
                edit_note_content.selectionEnd,
                edit_note_content.text?.toString()
        )
    }

    fun onButtonTextStrikethroughClick() {
        mPresenter.onButtonTextStrikethroughClick(
                edit_note_content.selectionStart,
                edit_note_content.selectionEnd,
                edit_note_content.text?.toString()
        )
    }

    fun onButtonTextLargeClick() {
        mPresenter.onButtonTextLargeClick(
                edit_note_content.selectionStart,
                edit_note_content.selectionEnd,
                edit_note_content.text?.toString()
        )
    }

    fun onButtonTextColorClick(color: String?) {
        mPresenter.onButtonTextColorClick(
                edit_note_content.selectionStart,
                edit_note_content.selectionEnd,
                edit_note_content.text?.toString(),
                color
        )
    }

    fun onButtonTextFillColorClick(color: String?) {
        mPresenter.onButtonTextFillColorClick(
                edit_note_content.selectionStart,
                edit_note_content.selectionEnd,
                edit_note_content.text?.toString(),
                color
        )
    }

    companion object {
        const val TAG = "NoteEditFragment"
        private const val ARG_CLICKED_VIEW = "clickedView"
        private const val ARG_POSITION = "position"
        private const val ARG_NOTE_TITLE = "noteTitle"
        private const val ARG_NOTE_CONTENT = "noteContent"
        private const val ARG_APPEARANCE = "noteAppearance"
        private const val SPAN_LARGE_TEXT_SIZE = 1.2f
        const val VIEW_TITLE = 0
        const val VIEW_CONTENT = 1

        @JvmStatic
        fun newInstance(noteTitle: String, noteContent: Spannable, clickedView: Int,
                        clickPosition: Int, appearance: MyNoteAppearance?) =
                NoteEditFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_CLICKED_VIEW, clickedView)
                        putInt(ARG_POSITION, clickPosition)
                        putString(ARG_NOTE_TITLE, noteTitle)
                        putCharSequence(ARG_NOTE_CONTENT, noteContent)
                        appearance?.let { putParcelable(ARG_APPEARANCE, it) }
                    }
                }
    }
}
