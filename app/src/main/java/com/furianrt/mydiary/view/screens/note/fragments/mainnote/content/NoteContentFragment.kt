/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.screens.note.fragments.mainnote.content

import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.furianrt.mydiary.R
import com.furianrt.mydiary.view.base.BaseFragment
import com.furianrt.mydiary.data.model.MyNoteAppearance
import com.furianrt.mydiary.view.screens.note.NoteActivity
import com.furianrt.mydiary.view.screens.note.fragments.mainnote.NoteFragment
import com.furianrt.mydiary.view.screens.note.fragments.mainnote.edit.NoteEditFragment
import com.furianrt.mydiary.utils.inTransaction
import com.furianrt.mydiary.utils.toHtmlString
import kotlinx.android.synthetic.main.fragment_note_content.*
import kotlinx.android.synthetic.main.fragment_note_content.view.*
import javax.inject.Inject

class NoteContentFragment : BaseFragment(), NoteContentFragmentContract.MvpView {

    private var mAppearance: MyNoteAppearance? = null
    private var mIsNewNote = true

    @Inject
    lateinit var mPresenter: NoteContentFragmentContract.Presenter

    private var mTitle: Spannable? = null
    private var mContent: Spannable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
        mIsNewNote = arguments?.getBoolean(ARG_IS_NEW_NOTE)!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_note_content, container, false)

        val onTouchListener = View.OnTouchListener { v, motionEvent ->
            val layout = (v as TextView).layout
            val x = motionEvent.x
            val y = motionEvent.y
            if (layout != null && motionEvent.action == MotionEvent.ACTION_UP) {
                val line = layout.getLineForVertical(y.toInt())
                mPresenter.onTouchPositionChange(layout.getOffsetForHorizontal(line, x))
            }
            return@OnTouchListener false
        }
        view.text_note_title.setOnTouchListener(onTouchListener)
        view.text_note_title.setOnClickListener { mPresenter.onTitleClick() }
        view.text_note_content.setOnTouchListener(onTouchListener)
        view.text_note_content.setOnClickListener { mPresenter.onContentClick() }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (mIsNewNote && savedInstanceState == null) {
            (parentFragment as? NoteFragment)?.disableActionBarExpanding(false)
            showNoteEditView(NoteEditFragment.VIEW_TITLE, mTitle?.length ?: 0)
        }
    }

    fun showNoteText(title: Spannable, content: Spannable) {
        Log.e(TAG, "showNoteText")
        mTitle = title
        mContent = content
        if (title.isEmpty()) {
            text_note_title.visibility = View.GONE
        } else {
            text_note_title.visibility = View.VISIBLE
            text_note_title.setText(title, TextView.BufferType.SPANNABLE)
        }
        text_note_content.setText(content, TextView.BufferType.SPANNABLE)
    }

    fun setAppearance(appearance: MyNoteAppearance) {
        mAppearance = appearance
        appearance.textColor?.let { text_note_title.setTextColor(it) }
        appearance.textSize?.let { text_note_title.textSize = it.toFloat() }
        appearance.textColor?.let { text_note_content.setTextColor(it) }
        appearance.textSize?.let { text_note_content.textSize = it.toFloat() }
        appearance.textBackground?.let { layout_note_content_root.setBackgroundColor(it) }
        fragmentManager?.let { manager ->
            manager.findFragmentByTag(NoteEditFragment.TAG)?.let {
                (it as NoteEditFragment).setAppearance(appearance)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mPresenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        mPresenter.detachView()
    }

    override fun showNoteEditViewForTitle(touchPosition: Int) {
        (parentFragment as? NoteFragment)?.disableActionBarExpanding(true)
        showNoteEditView(NoteEditFragment.VIEW_TITLE, touchPosition)
    }

    override fun showNoteEditViewForContent(touchPosition: Int) {
        (parentFragment as? NoteFragment)?.disableActionBarExpanding(true)
        showNoteEditView(NoteEditFragment.VIEW_CONTENT, touchPosition)
    }

    override fun showNoteEditViewForTitleEnd() {
        (parentFragment as? NoteFragment)?.disableActionBarExpanding(true)
        showNoteEditView(NoteEditFragment.VIEW_TITLE, mTitle?.length ?: 0)
    }

    private fun showNoteEditView(clickedView: Int, touchPosition: Int) {
        (activity as NoteActivity).savePagerPosition()
        if (fragmentManager?.findFragmentByTag(NoteEditFragment.TAG) == null) {
            activity?.supportFragmentManager?.inTransaction {
                setPrimaryNavigationFragment(parentFragment)
            }
            fragmentManager?.let { manager ->
                val editFragment = NoteEditFragment.newInstance(
                        mTitle?.toHtmlString() ?: "",
                        mContent?.toHtmlString() ?: "",
                        clickedView,
                        touchPosition,
                        mAppearance
                )
                manager.inTransaction {
                    add(R.id.container_note_edit, editFragment, NoteEditFragment.TAG)
                    addToBackStack(null)
                }
            }
        }
    }

    fun getNoteTitleText(): Spannable = mTitle ?: Editable.Factory().newEditable("")

    fun getNoteContentText(): Spannable = mContent ?: Editable.Factory().newEditable("")

    fun removeEditFragment() {
        fragmentManager?.findFragmentByTag(NoteEditFragment.TAG)?.let {
            Log.e(TAG, "removeEditFragment")
            fragmentManager?.popBackStack()
        }
    }

    fun updateNoteText(title: Spannable, content: Spannable) {
        mTitle = title
        mContent = content
        text_note_title.setText(title, TextView.BufferType.SPANNABLE)
        text_note_content.setText(content, TextView.BufferType.SPANNABLE)
    }

    fun setVisibility(visibility: Int) {
        layout_note_content_root.visibility = visibility
    }

    companion object {
        const val TAG = "NoteContentFragment"
        private const val ARG_IS_NEW_NOTE = "is_new_note"

        @JvmStatic
        fun newInstance(isNewNote: Boolean) =
                NoteContentFragment().apply {
                    arguments = Bundle().apply {
                        putBoolean(ARG_IS_NEW_NOTE, isNewNote)
                    }
                }
    }
}
