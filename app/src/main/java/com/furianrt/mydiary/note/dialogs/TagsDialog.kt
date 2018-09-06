package com.furianrt.mydiary.note.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyNote
import com.furianrt.mydiary.data.model.MyTag
import com.furianrt.mydiary.note.fragments.ARG_NOTE
import kotlinx.android.synthetic.main.dialog_tags.view.*
import javax.inject.Inject

private const val ARG_TAGS = "tags"

class TagsDialog
    : DialogFragment(), TagsDialogListAdapter.OnTagChangedListener, TagsDialogContract.View {

    private val mListAdapter = TagsDialogListAdapter(this)
    private var mListener: OnTagsDialogInteractionListener? = null

    private lateinit var mNote: MyNote
    private lateinit var mTags: ArrayList<MyTag>

    @Inject
    lateinit var mPresenter: TagsDialogContract.Presenter

    companion object {
        @JvmStatic
        fun newInstance(note: MyNote, tags: ArrayList<MyTag>) =
                TagsDialog().apply {
                    arguments = Bundle().apply {
                        putSerializable(ARG_NOTE, note)
                        putSerializable(ARG_TAGS, tags)
                    }
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(context!!).inject(this)
        super.onCreate(savedInstanceState)
        mTags = if (savedInstanceState != null) {
            savedInstanceState.getSerializable(ARG_TAGS) as ArrayList<MyTag>
        } else {
            arguments?.getSerializable(ARG_TAGS) as ArrayList<MyTag>
        }
        mNote = arguments?.getSerializable(ARG_NOTE) as MyNote
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = activity?.layoutInflater
        val view = inflater?.inflate(R.layout.dialog_tags, null)

        mPresenter.attachView(this)

        initUiElements(view)

        return AlertDialog.Builder(context)
                .setView(view)
                .setPositiveButton(getString(R.string.save)) { _, _ ->
                    mListener?.onTagsDialogPositiveButtonClick(mTags)
                }
                .setNegativeButton(getString(R.string.cancel), null)
                .create()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        mPresenter.detachView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(ARG_TAGS, mTags)
        super.onSaveInstanceState(outState)
    }

    override fun onTagClicked(tag: MyTag) {
        mPresenter.onTagClicked(mTags, tag)
    }

    override fun onTagDeleted(tag: MyTag) {
        mPresenter.onButtonDeleteTagClicked(tag, mTags)
    }

    override fun onTagEdited(tag: MyTag) {
        mPresenter.onButtonEditTagClicked(tag, mTags)
    }

    override fun showTags(tags: MutableList<MyTag>) {
        mListAdapter.submitList(tags)
    }

    private fun initUiElements(view: View?) {
        showTags(mTags)

        view?.apply {
            val textSearch = search_add_tags
                    .findViewById<EditText>(android.support.v7.appcompat.R.id.search_src_text)
            textSearch.setTextColor(Color.WHITE)

            val addTagButton = search_add_tags
                    .findViewById<ImageView>(android.support.v7.appcompat.R.id.search_close_btn)
            addTagButton.setOnClickListener {
                mPresenter.onButtonAddTagClicked(search_add_tags.query.toString(), mTags)
                button_close_search.visibility = View.GONE
                search_add_tags.onActionViewCollapsed()
            }

            search_add_tags.setOnSearchClickListener {
                button_close_search.visibility = View.VISIBLE
            }

            button_close_search.setOnClickListener {
                it.visibility = View.GONE
                search_add_tags.onActionViewCollapsed()
            }

            list_tags.apply {
                val manager = LinearLayoutManager(context)
                layoutManager = manager
                adapter = mListAdapter
                addItemDecoration(DividerItemDecoration(context, manager.orientation))
            }
        }
    }

    fun setOnTagChangedListener(listener: OnTagsDialogInteractionListener?) {
        mListener = listener
    }

    interface OnTagsDialogInteractionListener {

        fun onTagsDialogPositiveButtonClick(tags: List<MyTag>)
    }
}