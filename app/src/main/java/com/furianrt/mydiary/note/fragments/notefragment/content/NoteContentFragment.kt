package com.furianrt.mydiary.note.fragments.notefragment.content

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyNote
import com.furianrt.mydiary.note.NoteActivity
import com.furianrt.mydiary.note.fragments.notefragment.ARG_NOTE
import com.furianrt.mydiary.note.fragments.notefragment.edit.ClickedView
import com.furianrt.mydiary.note.fragments.notefragment.edit.NoteEditFragment
import com.furianrt.mydiary.note.fragments.notefragment.inTransaction
import kotlinx.android.synthetic.main.fragment_note_content.view.*
import javax.inject.Inject

class NoteContentFragment : androidx.fragment.app.Fragment(), NoteContentFragmentContract.View {

    private lateinit var mNote: MyNote
    private var mTouchPosition = 0

    @Inject
    lateinit var mPresenter: NoteContentFragmentContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(context!!).inject(this)
        super.onCreate(savedInstanceState)
        arguments?.let {
            mNote = it.getParcelable(ARG_NOTE)!!
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_note_content, container, false)

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

    override fun onStart() {
        super.onStart()
        mPresenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
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
            if (it.findFragmentByTag(NoteEditFragment.TAG) == null) {
                it.inTransaction {
                    replace(R.id.container_note_edit,
                            NoteEditFragment.newInstance(mNote, clickedView, touchPosition),
                            NoteEditFragment.TAG)
                    addToBackStack(null)
                }
            }
        }
    }

    companion object {

        val TAG = NoteContentFragment::class.toString()

        @JvmStatic
        fun newInstance(note: MyNote) =
                NoteContentFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_NOTE, note)
                    }
                }
    }
}
