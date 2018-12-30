package com.furianrt.mydiary.note.fragments.notefragment.edit

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.fragment.app.Fragment
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyNote
import com.furianrt.mydiary.data.model.MyNoteAppearance
import com.furianrt.mydiary.note.fragments.notefragment.NoteFragment
import com.furianrt.mydiary.utils.showKeyboard
import kotlinx.android.synthetic.main.fragment_note_edit.view.*
import javax.inject.Inject

private const val ARG_CLICKED_VIEW = "clickedView"
private const val ARG_NOTE = "note"
private const val ARG_APPEARANCE = "appearance"
private const val ARG_POSITION = "position"

enum class ClickedView { TITLE, CONTENT }

class NoteEditFragment : Fragment(), NoteEditFragmentContract.View {

    private lateinit var mNote: MyNote
    private lateinit var mAppearance: MyNoteAppearance
    private var mClickedView: ClickedView? = null
    private var mClickPosition = 0
    private var mListener: OnNoteFragmentInteractionListener? = null
    private val mTextChangeListener = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            view?.let {
                (parentFragment as NoteFragment).onNoteTextChange(
                        it.edit_note_title.text.toString(),
                        it.edit_note_content.text.toString()
                )
            }
        }
    }

    @Inject
    lateinit var mPresenter: NoteEditFragmentContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(context!!).inject(this)
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.apply {
            mNote = getParcelable(ARG_NOTE)!!
            mAppearance = getParcelable(ARG_APPEARANCE)!!
            mClickedView = getSerializable(ARG_CLICKED_VIEW) as? ClickedView
            mClickPosition = getInt(ARG_POSITION)
        }
        if (savedInstanceState != null) {
            mClickedView = null
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_note_edit, container, false)

        view.apply {
            edit_note_title.setText(mNote.title)
            edit_note_title.addTextChangedListener(mTextChangeListener)
            //edit_note_title.setTextColor(mAppearance.textColor)
            //edit_note_title.textSize = mAppearance.textSize
            edit_note_content.setText(mNote.content)
            edit_note_content.addTextChangedListener(mTextChangeListener)
            //edit_note_content.setTextColor(mAppearance.textColor)
            //edit_note_content.textSize = mAppearance.textSize
            //layout_note_edit_root.setBackgroundColor(mAppearance.textColor)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            when (mClickedView) {
                null -> activity?.currentFocus?.postDelayed({ edit_note_title.showKeyboard() }, 400)
                ClickedView.TITLE -> {
                    edit_note_title.requestFocus()
                    edit_note_title.setSelection(mClickPosition)
                    edit_note_title.postDelayed({ edit_note_title.showKeyboard() }, 400)

                }
                ClickedView.CONTENT -> {
                    edit_note_content.requestFocus()
                    edit_note_content.setSelection(mClickPosition)
                    edit_note_content.postDelayed({ edit_note_content.showKeyboard() }, 400)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mPresenter.attachView(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu?.removeItem(R.id.menu_edit)
        menu?.removeItem(R.id.menu_delete)
        inflater?.inflate(R.menu.fragment_edit_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
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

    override fun onAttach(context: Context?) {
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
        view?.edit_note_title?.removeTextChangedListener(mTextChangeListener)
        view?.edit_note_content?.removeTextChangedListener(mTextChangeListener)
    }

    override fun onResume() {
        super.onResume()
        mListener?.onNoteFragmentEditModeEnabled()
        if (mNote.id.isNotEmpty()) {
            (parentFragment as? NoteFragment)?.disableActionBarExpanding(true)
        }
    }

    override fun onStop() {
        super.onStop()
        val noteTitle = view?.edit_note_title?.text.toString()
        val noteContent = view?.edit_note_content?.text.toString()
        val noteFragment = (parentFragment as? NoteFragment)
        noteFragment?.onNoteEditFinished(noteTitle, noteContent)
        noteFragment?.enableActionBarExpanding(false, false)
        mListener?.onNoteFragmentEditModeDisabled()
        mPresenter.detachView()
    }

    interface OnNoteFragmentInteractionListener {

        fun onNoteFragmentEditModeEnabled()

        fun onNoteFragmentEditModeDisabled()
    }

    companion object {

        val TAG = NoteEditFragment::class.toString()

        @JvmStatic
        fun newInstance(note: MyNote, appearance: MyNoteAppearance, clickedView: ClickedView?,
                        clickPosition: Int) =

                NoteEditFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_NOTE, note)
                        putParcelable(ARG_APPEARANCE, appearance)
                        putSerializable(ARG_CLICKED_VIEW, clickedView)
                        putInt(ARG_POSITION, clickPosition)
                    }
                }
    }
}
