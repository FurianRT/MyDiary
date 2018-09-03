package com.furianrt.mydiary.note.fragments.content

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyNote
import com.furianrt.mydiary.note.NoteActivity
import com.furianrt.mydiary.note.fragments.ARG_NOTE
import com.furianrt.mydiary.note.fragments.edit.ClickedView
import com.furianrt.mydiary.note.fragments.edit.NoteEditFragment
import com.furianrt.mydiary.note.fragments.inTransaction
import kotlinx.android.synthetic.main.fragment_note_content.view.*
import java.util.*
import javax.inject.Inject

class NoteContentFragment : Fragment(), NoteContentFragmentContract.View {

    private lateinit var mNote: MyNote
    private var mTouchPosition = 0

    @Inject
    lateinit var mPresenter: NoteContentFragmentContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(context!!).inject(this)
        super.onCreate(savedInstanceState)
        if (arguments == null) {
            mNote = MyNote("", "", Date().time)
        } else {
            arguments?.let {
                mNote = it.getSerializable(ARG_NOTE) as MyNote
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_note_content, container, false)

        mPresenter.attachView(this)

        view.apply {
            val title = mNote.title
            if (title.isEmpty()) {
                text_note_title.visibility = View.GONE
            } else {
                text_note_title.text = mNote.title
            }
            text_note_content.text = mNote.content
        }

        setListeners(view)

        return view
    }

    override fun showNote(note: MyNote) {
        Log.e("fff", "FragmentContent.showNote()")
        mNote = note
        view?.let {
            val title = mNote.title
            if (title.isEmpty()) {
                it.text_note_title.visibility = View.GONE
            } else {
                it.text_note_title.text = mNote.title
            }
            it.text_note_content.text = mNote.content
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mPresenter.detachView()
    }

    private fun setListeners(view: View) {
        val onTouchListener = View.OnTouchListener { v, motionEvent ->
            val layout = (v as TextView).layout
            val x = motionEvent.x
            val y = motionEvent.y
            if (layout != null && motionEvent.action == MotionEvent.ACTION_UP) {
                val line = layout.getLineForVertical(y.toInt())
                mTouchPosition = layout.getOffsetForHorizontal(line, x)
            }
            false
        }

        view.text_note_title.setOnTouchListener(onTouchListener)
        view.text_note_content.setOnTouchListener(onTouchListener)


        view.text_note_title.setOnClickListener {
            (activity as NoteActivity).savePagerPosition()
            showEditFragment(ClickedView.TITLE, mTouchPosition)
        }

        view.text_note_content.setOnClickListener {
            (activity as NoteActivity).savePagerPosition()
            showEditFragment(ClickedView.CONTENT, mTouchPosition)
        }
    }

    private fun showEditFragment(clickedView: ClickedView, touchPosition: Int) {
        activity?.supportFragmentManager?.inTransaction {
            this.setPrimaryNavigationFragment(parentFragment)
        }

        fragmentManager?.let {
            if (it.findFragmentByTag(NoteEditFragment::class.toString()) == null) {
                it.inTransaction {
                    replace(R.id.container_note_edit,
                            NoteEditFragment.newInstance(mNote, clickedView, touchPosition),
                            NoteEditFragment::class.toString())
                    addToBackStack(null)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        mPresenter.findNote(mNote)
    }

    companion object {
        @JvmStatic
        fun newInstance(note: MyNote?) =
                NoteContentFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable(ARG_NOTE, note)
                    }
                }
    }
}
