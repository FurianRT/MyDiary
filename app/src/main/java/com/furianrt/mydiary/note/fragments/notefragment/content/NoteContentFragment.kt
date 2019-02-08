package com.furianrt.mydiary.note.fragments.notefragment.content

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyNote
import com.furianrt.mydiary.data.model.MyNoteAppearance
import com.furianrt.mydiary.note.NoteActivity
import com.furianrt.mydiary.note.fragments.notefragment.edit.NoteEditFragment
import com.furianrt.mydiary.note.fragments.notefragment.inTransaction
import kotlinx.android.synthetic.main.fragment_note_content.*
import kotlinx.android.synthetic.main.fragment_note_content.view.*
import javax.inject.Inject

class NoteContentFragment : Fragment(), NoteContentFragmentContract.View {

    private lateinit var mNote: MyNote
    private lateinit var mAppearance: MyNoteAppearance
    private var mTouchPosition = 0

    @Inject
    lateinit var mPresenter: NoteContentFragmentContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(context!!).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_note_content, container, false)

        setListeners(view)

        return view
    }

    fun showNote(note: MyNote) {
        Log.e(TAG, "showNote")
        mNote = note
        view?.apply {
            val title = mNote.title
            if (title.isEmpty()) {
                text_note_title.visibility = View.GONE
            } else {
                text_note_title.visibility = View.VISIBLE
                text_note_title.text = mNote.title
            }
            text_note_content.text = mNote.content
        }
    }

    fun showNoteText(title: String, content: String) {
        view?.apply {
            if (title.isEmpty()) {
                text_note_title.visibility = View.GONE
            } else {
                text_note_title.visibility = View.VISIBLE
                text_note_title.text = title
            }
            text_note_content.text = content
        }
    }

    fun setAppearance(appearance: MyNoteAppearance) {
        mAppearance = appearance
        view?.apply {
            mAppearance.textColor?.let { text_note_title.setTextColor(it) }
            mAppearance.textSize?.let { text_note_title.textSize = it.toFloat() }
            mAppearance.textColor?.let { text_note_content.setTextColor(it) }
            mAppearance.textSize?.let { text_note_content.textSize = it.toFloat() }
            mAppearance.textBackground?.let { layout_note_content_root.setBackgroundColor(it) }
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
        view.text_note_title.setOnClickListener {
            (activity as NoteActivity).savePagerPosition()
            showEditFragment(NoteEditFragment.ClickedView.TITLE, mTouchPosition)
        }

        view.text_note_content.setOnTouchListener(onTouchListener)
        view.text_note_content.setOnClickListener {
            (activity as NoteActivity).savePagerPosition()
            showEditFragment(NoteEditFragment.ClickedView.CONTENT, mTouchPosition)
        }
    }

    fun showEditFragment(clickedView: NoteEditFragment.ClickedView, touchPosition: Int) {
        activity?.supportFragmentManager?.inTransaction {
            this.setPrimaryNavigationFragment(parentFragment)
        }

        fragmentManager?.let {
            if (it.findFragmentByTag(NoteEditFragment.TAG) == null) {
                it.inTransaction {
                    add(R.id.container_note_edit,
                            NoteEditFragment.newInstance(mNote, mAppearance, clickedView, touchPosition),
                            NoteEditFragment.TAG)
                    addToBackStack(null)
                }
            }
        }
    }

    fun updateNoteText(title: String, content: String) {
        text_note_title.text = title
        text_note_content.text = content
    }

    fun setVisibility(visibility: Int) {
        layout_note_content_root.visibility = visibility
    }

    companion object {
        const val TAG = "NoteContentFragment"
    }
}
