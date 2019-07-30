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

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.*
import androidx.core.graphics.ColorUtils
import com.furianrt.mydiary.R
import com.furianrt.mydiary.view.base.BaseFragment
import com.furianrt.mydiary.data.model.MyNoteAppearance
import com.furianrt.mydiary.view.screens.note.fragments.mainnote.NoteFragment
import com.furianrt.mydiary.view.screens.note.fragments.mainnote.content.NoteContentFragment
import com.furianrt.mydiary.utils.hideKeyboard
import com.furianrt.mydiary.utils.showKeyboard
import kotlinx.android.synthetic.main.fragment_note_edit.*
import kotlinx.android.synthetic.main.fragment_note_edit.view.*
import javax.inject.Inject

class NoteEditFragment : BaseFragment(), NoteEditFragmentContract.MvpView {

    private var mClickedView: Int? = null
    private var mClickPosition = 0
    private var mNoteTitle = ""
    private var mNoteContent = ""
    private var mAppearance: MyNoteAppearance? = null
    private var mEnableUndo = false
    private var mEnableRedo = false
    private var mListener: OnNoteFragmentInteractionListener? = null
    private val mTextChangeListener = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            (parentFragment as NoteFragment).onNoteTextChange(
                    edit_note_title.text.toString(),
                    edit_note_content.text.toString()
            )
        }
    }

    @Inject
    lateinit var mPresenter: NoteEditFragmentContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            mNoteTitle = it.getString(ARG_NOTE_TITLE, "")
            mNoteContent = it.getString(ARG_NOTE_CONTENT, "")
            mClickedView = it.getInt(ARG_CLICKED_VIEW)
            mClickPosition = it.getInt(ARG_POSITION)
            mAppearance = it.getParcelable(ARG_APPEARANCE) as? MyNoteAppearance?
        }
        if (savedInstanceState != null) {
            mClickedView = null
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_note_edit, container, false)

        fragmentManager?.findFragmentByTag(NoteContentFragment.TAG)?.let {
            (it as NoteContentFragment).setVisibility(View.INVISIBLE)
        }

        view.edit_note_title.setText(mNoteTitle)
        view.edit_note_content.setText(mNoteContent)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAppearance?.let { setAppearance(it) }
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.removeItem(R.id.menu_done)
        menu.removeItem(R.id.menu_edit)
        menu.removeItem(R.id.menu_delete)
        menu.removeItem(R.id.menu_undo)
        menu.removeItem(R.id.menu_redo)
        inflater.inflate(R.menu.fragment_edit_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val itemUndo = menu.findItem(R.id.menu_undo)
        val itemRedo = menu.findItem(R.id.menu_redo)

        itemUndo?.isEnabled = mEnableUndo
        itemRedo?.isEnabled = mEnableRedo

        if (mEnableUndo) {
            itemUndo?.icon?.mutate()?.setTint(Color.WHITE)
        } else {
            itemUndo?.icon?.mutate()?.setTint(Color.GRAY)
        }

        if (mEnableRedo) {
            itemRedo?.icon?.mutate()?.setTint(Color.WHITE)
        } else {
            itemRedo?.icon?.mutate()?.setTint(Color.GRAY)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_done -> {
                mPresenter.onDoneButtonClick()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun closeView() {
        fragmentManager?.popBackStack()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnNoteFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context.toString()
                    + " must implement OnNoteFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.currentFocus?.hideKeyboard()
        activity?.currentFocus?.clearFocus()
        fragmentManager?.findFragmentByTag(NoteContentFragment.TAG)?.let {
            (it as NoteContentFragment).setVisibility(View.VISIBLE)
        }
    }

    override fun onStart() {
        super.onStart()
        mPresenter.attachView(this)
        (parentFragment as? NoteFragment?)?.onNoteFragmentEditModeEnabled()
        mListener?.onNoteFragmentEditModeEnabled()
        edit_note_title.addTextChangedListener(mTextChangeListener)
        edit_note_content.addTextChangedListener(mTextChangeListener)
    }

    override fun onStop() {
        super.onStop()
        val noteTitle = edit_note_title.text.toString()
        val noteContent = edit_note_content.text.toString()
        val noteFragment = (parentFragment as? NoteFragment?)
        noteFragment?.onNoteEditFinished(noteTitle, noteContent)
        noteFragment?.enableActionBarExpanding(expanded = false, animate = false)
        edit_note_title.removeTextChangedListener(mTextChangeListener)
        edit_note_content.removeTextChangedListener(mTextChangeListener)
        mListener?.onNoteFragmentEditModeDisabled()
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

    fun showNoteText(title: String, content: String) {
        Log.e(TAG, "showNoteText")
        // Отключаем листенер что бы в undo/redo не прилетал измененный им же текст
        edit_note_title.removeTextChangedListener(mTextChangeListener)
        edit_note_content.removeTextChangedListener(mTextChangeListener)
        // Клава с SUGGESTIONS кэширует текст и делает странные вещи при undo/redo.
        // Приходится отключать SUGGESTIONS при программном изменении текста
        val currentTitleInputType = edit_note_title.inputType
        val currentContentInputType = edit_note_content.inputType

        edit_note_title.inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        edit_note_content.inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS

        edit_note_title.setText(title)
        edit_note_content.setText(content)

        edit_note_title.inputType = currentTitleInputType
        edit_note_content.inputType = currentContentInputType
        edit_note_title.addTextChangedListener(mTextChangeListener)
        edit_note_content.addTextChangedListener(mTextChangeListener)

        if (edit_note_title.isFocused) {
            edit_note_title.setSelection(title.length)
        } else if (edit_note_content.isFocused) {
            edit_note_content.setSelection(content.length)
        }
    }

    fun getNoteTitleText(): String = edit_note_title?.text?.toString() ?: ""

    fun getNoteContentText(): String = edit_note_content?.text?.toString() ?: ""

    fun enableRedoButton(enable: Boolean) {
        mEnableRedo = enable
        activity?.invalidateOptionsMenu()
    }

    fun enableUndoButton(enable: Boolean) {
        mEnableUndo = enable
        activity?.invalidateOptionsMenu()
    }

    interface OnNoteFragmentInteractionListener {
        fun onNoteFragmentEditModeEnabled()
        fun onNoteFragmentEditModeDisabled()
    }

    companion object {

        const val TAG = "NoteEditFragment"
        private const val ARG_CLICKED_VIEW = "clickedView"
        private const val ARG_POSITION = "position"
        private const val ARG_NOTE_TITLE = "noteTitle"
        private const val ARG_NOTE_CONTENT = "noteContent"
        private const val ARG_APPEARANCE = "noteAppearance"
        const val VIEW_TITLE = 0
        const val VIEW_CONTENT = 1

        @JvmStatic
        fun newInstance(noteTitle: String, noteContent: String, clickedView: Int,
                        clickPosition: Int, appearance: MyNoteAppearance?) =
                NoteEditFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_CLICKED_VIEW, clickedView)
                        putInt(ARG_POSITION, clickPosition)
                        putString(ARG_NOTE_TITLE, noteTitle)
                        putString(ARG_NOTE_CONTENT, noteContent)
                        appearance?.let { putParcelable(ARG_APPEARANCE, it) }
                    }
                }
    }
}
