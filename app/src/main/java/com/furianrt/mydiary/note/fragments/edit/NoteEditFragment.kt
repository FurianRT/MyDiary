package com.furianrt.mydiary.note.fragments.edit

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyNote
import com.furianrt.mydiary.note.fragments.*
import kotlinx.android.synthetic.main.fragment_note_edit.view.*
import com.furianrt.mydiary.utils.showKeyboard
import java.util.*
import javax.inject.Inject

const val ARG_CLICKED_VIEW = "clickedView"
const val ARG_POSITION = "position"

enum class ClickedView { TITLE, CONTENT }

class NoteEditFragment : Fragment(), NoteEditFragmentContract.View {

    private lateinit var mNote: MyNote
    private lateinit var mClickedView: ClickedView
    private var mClickPosition = 0

    @Inject
    lateinit var mPresenter: NoteEditFragmentContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(context!!).inject(this)
        super.onCreate(savedInstanceState)
        if (arguments == null) {
            mNote = MyNote("", "", Date().time)
            mClickedView = ClickedView.TITLE
        } else {
            arguments?.apply {
                mNote = this.getSerializable(ARG_NOTE) as MyNote
                mClickedView = this.getSerializable(ARG_CLICKED_VIEW) as ClickedView
                mClickPosition = this.getInt(ARG_POSITION)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_note_edit, container, false)

        mPresenter.attachView(this)

        view.apply {
            edit_note_title.setText(mNote.title)
            edit_note_content.setText(mNote.content)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        view?.apply {
            val focus = activity?.currentFocus
            when {
                focus != null ->
                    showKeyboard(context)
                mClickedView == ClickedView.TITLE -> {
                    edit_note_title.requestFocus()
                    edit_note_title.setSelection(mClickPosition)
                    showKeyboard(context)
                }
                mClickedView == ClickedView.CONTENT -> {
                    edit_note_content.requestFocus()
                    edit_note_content.setSelection(mClickPosition)
                    showKeyboard(context)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        mNote.title = view?.edit_note_title?.text.toString()
        mNote.content = view?.edit_note_content?.text.toString()
        mPresenter.addOrUpdateNote(mNote)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mPresenter.detachView()
    }


    companion object {
        @JvmStatic
        fun newInstance(note: MyNote?, clickedView: ClickedView, clickPosition: Int) =
                NoteEditFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable(ARG_NOTE, note)
                        putSerializable(ARG_CLICKED_VIEW, clickedView)
                        putInt(ARG_POSITION, clickPosition)
                    }
                }
    }
}
