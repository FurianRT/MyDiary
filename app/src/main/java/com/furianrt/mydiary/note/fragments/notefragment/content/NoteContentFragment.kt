package com.furianrt.mydiary.note.fragments.notefragment.content

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyNoteAppearance
import com.furianrt.mydiary.note.NoteActivity
import com.furianrt.mydiary.note.fragments.notefragment.edit.NoteEditFragment
import com.furianrt.mydiary.note.fragments.notefragment.inTransaction
import kotlinx.android.synthetic.main.fragment_note_content.*
import kotlinx.android.synthetic.main.fragment_note_content.view.*
import javax.inject.Inject

class NoteContentFragment : Fragment(), NoteContentFragmentContract.View {

    private var mAppearance: MyNoteAppearance? = null
    private lateinit var mMode: NoteActivity.Companion.Mode

    @Inject
    lateinit var mPresenter: NoteContentFragmentContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(context!!).inject(this)
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            mMode = (it.getSerializable(ARG_MODE) as? NoteActivity.Companion.Mode?)
                    ?: throw IllegalArgumentException()
        }
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
        view.text_note_title.setOnClickListener {
            (activity as NoteActivity).savePagerPosition()
            mPresenter.onTitleClick()

        }

        view.text_note_content.setOnTouchListener(onTouchListener)
        view.text_note_content.setOnClickListener {
            (activity as NoteActivity).savePagerPosition()
            mPresenter.onContentClick()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e(TAG, (mMode == NoteActivity.Companion.Mode.ADD).toString())

        if (mMode == NoteActivity.Companion.Mode.ADD && savedInstanceState == null) {
            showNoteEditViewForTitleEnd()
        }
    }

    fun showNoteText(title: String, content: String) {
        Log.e(TAG, "showNoteText")
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
            appearance.textColor?.let { text_note_title.setTextColor(it) }
            appearance.textSize?.let { text_note_title.textSize = it.toFloat() }
            appearance.textColor?.let { text_note_content.setTextColor(it) }
            appearance.textSize?.let { text_note_content.textSize = it.toFloat() }
            appearance.textBackground?.let { layout_note_content_root.setBackgroundColor(it) }
        }

        fragmentManager?.let { manager ->
            manager.findFragmentByTag(NoteEditFragment.TAG)?.let {
                (it as NoteEditFragment).setAppearance(appearance)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.menu_edit -> {
                mPresenter.onEditButtonClick()
                true
            }
            else -> super.onOptionsItemSelected(item)
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
        showNoteEditView(NoteEditFragment.ClickedView.TITLE, touchPosition)
    }

    override fun showNoteEditViewForContent(touchPosition: Int) {
        showNoteEditView(NoteEditFragment.ClickedView.CONTENT, touchPosition)
    }

    override fun showNoteEditViewForTitleEnd() {
        view?.let {
            showNoteEditView(NoteEditFragment.ClickedView.TITLE, it.text_note_title.text.length)
        }
    }

    private fun showNoteEditView(clickedView: NoteEditFragment.ClickedView, touchPosition: Int) {
        activity?.supportFragmentManager?.inTransaction {
            this.setPrimaryNavigationFragment(parentFragment)
        }

        fragmentManager?.let { manager ->
            var editFragment = manager.findFragmentByTag(NoteEditFragment.TAG) as? NoteEditFragment
            if (editFragment == null) {
                editFragment = NoteEditFragment.newInstance(
                        view!!.text_note_title.text.toString(),
                        view!!.text_note_content.text.toString(),
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

    fun removeEditFragment() {
        fragmentManager?.findFragmentByTag(NoteEditFragment.TAG)?.let {
            Log.e(TAG, "removeEditFragment")
            fragmentManager?.popBackStack()
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
        private const val ARG_MODE = "mode"

        @JvmStatic
        fun newInstance(mode: NoteActivity.Companion.Mode) =
                NoteContentFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable(ARG_MODE, mode)
                    }
                }
    }
}
