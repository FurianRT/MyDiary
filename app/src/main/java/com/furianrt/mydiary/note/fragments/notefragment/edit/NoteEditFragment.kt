package com.furianrt.mydiary.note.fragments.notefragment.edit

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyNoteAppearance
import com.furianrt.mydiary.note.fragments.notefragment.NoteFragment
import com.furianrt.mydiary.note.fragments.notefragment.content.NoteContentFragment
import com.furianrt.mydiary.utils.showKeyboard
import kotlinx.android.synthetic.main.fragment_note_edit.view.*
import javax.inject.Inject

class NoteEditFragment : Fragment(), NoteEditFragmentContract.View {

    private var mClickedView: ClickedView? = null
    private var mClickPosition = 0
    private var mNoteTitle = ""
    private var mNoteContent = ""
    private var mAppearance: MyNoteAppearance? = null
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
            mNoteTitle = getString(ARG_NOTE_TITLE, "")
            mNoteContent = getString(ARG_NOTE_CONTENT, "")
            mClickedView = getSerializable(ARG_CLICKED_VIEW) as? ClickedView?
            mClickPosition = getInt(ARG_POSITION)
            mAppearance = getParcelable(ARG_APPEARANCE) as? MyNoteAppearance?
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
        view.edit_note_title.addTextChangedListener(mTextChangeListener)
        view.edit_note_content.addTextChangedListener(mTextChangeListener)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAppearance?.let { setAppearance(it) }
        view.apply {
            when (mClickedView) {
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
        fragmentManager?.findFragmentByTag(NoteContentFragment.TAG)?.let {
            (it as NoteContentFragment).setVisibility(View.VISIBLE)
        }
        view?.edit_note_title?.removeTextChangedListener(mTextChangeListener)
        view?.edit_note_content?.removeTextChangedListener(mTextChangeListener)
    }

    override fun onResume() {
        super.onResume()
        mListener?.onNoteFragmentEditModeEnabled()
        (parentFragment as? NoteFragment)?.disableActionBarExpanding(true)
    }

    override fun onStop() {
        super.onStop()
        val noteTitle = view?.edit_note_title?.text.toString()
        val noteContent = view?.edit_note_content?.text.toString()
        val noteFragment = (parentFragment as? NoteFragment)
        noteFragment?.onNoteEditFinished(noteTitle, noteContent)
        noteFragment?.enableActionBarExpanding(expanded = false, animate = false)
        mListener?.onNoteFragmentEditModeDisabled()
        mPresenter.detachView()
    }

    fun setAppearance(appearance: MyNoteAppearance) {
        appearance.textColor?.let {
            view?.edit_note_title?.setTextColor(it)
            view?.edit_note_content?.setTextColor(it)
            view?.edit_note_title?.setHintTextColor(ColorUtils.setAlphaComponent(it, 50))
            view?.edit_note_content?.setHintTextColor(ColorUtils.setAlphaComponent(it, 50))
        }
        appearance.textSize?.let {
            view?.edit_note_title?.textSize = it.toFloat()
            view?.edit_note_content?.textSize = it.toFloat()
        }
    }

    interface OnNoteFragmentInteractionListener {

        fun onNoteFragmentEditModeEnabled()

        fun onNoteFragmentEditModeDisabled()
    }

    enum class ClickedView { TITLE, CONTENT }

    companion object {

        const val TAG = "NoteEditFragment"
        private const val ARG_CLICKED_VIEW = "clickedView"
        private const val ARG_POSITION = "position"
        private const val ARG_NOTE_TITLE = "noteTitle"
        private const val ARG_NOTE_CONTENT = "noteContent"
        private const val ARG_APPEARANCE = "noteAppearance"

        @JvmStatic
        fun newInstance(noteTitle: String, noteContent: String, clickedView: ClickedView?,
                        clickPosition: Int, appearance: MyNoteAppearance?) =
                NoteEditFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable(ARG_CLICKED_VIEW, clickedView)
                        putInt(ARG_POSITION, clickPosition)
                        putString(ARG_NOTE_TITLE, noteTitle)
                        putString(ARG_NOTE_CONTENT, noteContent)
                        appearance?.let { putParcelable(ARG_APPEARANCE, it) }
                    }
                }
    }
}
