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
import com.furianrt.mydiary.data.model.MyTag
import kotlinx.android.synthetic.main.dialog_tags.view.*
import javax.inject.Inject

private const val ARG_TAGS = "tags"

class TagsDialog
    : DialogFragment(), TagsDialogListAdapter.OnTagChangedListener, TagsDialogContract.View {

    private val mListAdapter = TagsDialogListAdapter(this)
    private var mListener: OnTagsDialogInteractionListener? = null

    @Inject
    lateinit var mPresenter: TagsDialogContract.Presenter

    companion object {
        @JvmStatic
        fun newInstance(tags: ArrayList<MyTag>) =
                TagsDialog().apply {
                    arguments = Bundle().apply {
                        putParcelableArrayList(ARG_TAGS, tags)
                    }
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(context!!).inject(this)
        super.onCreate(savedInstanceState)
        val tags: ArrayList<MyTag> = if (savedInstanceState != null) {
            savedInstanceState.getParcelableArrayList(ARG_TAGS)!!
        } else {
            arguments?.getParcelableArrayList(ARG_TAGS)!!
        }

        mPresenter.setTags(tags)
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
                    mListener?.onTagsDialogPositiveButtonClick(mPresenter.getTags())
                }
                .setNegativeButton(getString(R.string.cancel), null)
                .create()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        mPresenter.detachView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelableArrayList(ARG_TAGS, mPresenter.getTags())
        super.onSaveInstanceState(outState)
    }

    override fun onTagClicked(tag: MyTag) {
        mPresenter.onTagClicked(tag)
    }

    override fun onTagDeleted(tag: MyTag) {
        mPresenter.onButtonDeleteTagClicked(tag)
    }

    override fun onTagEdited(tag: MyTag) {
        mPresenter.onButtonEditTagClicked(tag)
    }

    override fun showTags(tags: MutableList<MyTag>) {
        mListAdapter.submitList(tags.toMutableList())
    }

    private fun initUiElements(view: View?) {
        mPresenter.onViewCreate()

        view?.apply {
            val editSearch = search_add_tags
                    .findViewById<EditText>(android.support.v7.appcompat.R.id.search_src_text)
            editSearch.setTextColor(Color.WHITE)

            val addTagButton = search_add_tags
                    .findViewById<ImageView>(android.support.v7.appcompat.R.id.search_close_btn)
            addTagButton.setOnClickListener {
                mPresenter.onButtonAddTagClicked(search_add_tags.query.toString())
                button_close_search.visibility = View.INVISIBLE
                search_add_tags.isIconified = true
                search_add_tags.isIconified = true
            }

            search_add_tags.setOnSearchClickListener {
                button_close_search.visibility = View.VISIBLE
            }

            button_close_search.setOnClickListener {
                it.visibility = View.INVISIBLE
                search_add_tags.onActionViewCollapsed()
            }

            button_close_search.postDelayed({
                if (!search_add_tags.isIconified) {
                    button_close_search.visibility = View.VISIBLE
                }
            }, 150)

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