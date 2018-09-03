package com.furianrt.mydiary.note.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
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

private const val ARG_TAGS = "tags"

class TagsDialog : DialogFragment(), TagsDialogListAdapter.OnTagChangedListener {

    private val mListAdapter = TagsDialogListAdapter(this)
    private var mListener: OnTagsDialogInteractionListener? = null

    private lateinit var mTags: ArrayList<MyTag>

    companion object {
        @JvmStatic
        fun newInstance(tags: ArrayList<MyTag>) =
                TagsDialog().apply {
                    arguments = Bundle().apply {
                        putSerializable(ARG_TAGS, tags)
                    }
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            arguments?.let {
                mTags = it.getSerializable(ARG_TAGS) as ArrayList<MyTag>
            }
    }

    fun setOnTagChangedListener(listener: OnTagsDialogInteractionListener?) {
        mListener = listener
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = activity?.layoutInflater
        val view = inflater?.inflate(R.layout.dialog_tags, null)

        initUiElements(view)

        return AlertDialog.Builder(context)
                .setView(view)
                .setPositiveButton(getString(R.string.save)) { _, _ ->
                    mListener?.onTagsDialogPositiveButtonClick(mTags)
                }
                .setNegativeButton(getString(R.string.cancel), null)
                .create()
    }

    override fun onTagClicked(tag: MyTag) {
        mTags.find { it.name == tag.name }
                ?.isChecked = tag.isChecked
    }

    override fun onTagDeleted(tag: MyTag) {

    }

    override fun onTagEdited(tag: MyTag) {

    }

    private fun initUiElements(view: View?) {
        mListAdapter.submitList(mTags)

        val imageClearSearch = view?.search_tags
                ?.findViewById<ImageView>(android.support.v7.appcompat.R.id.search_close_btn)
        imageClearSearch?.setImageResource(R.drawable.ic_close)
        val textSearch = view?.search_tags
                ?.findViewById<EditText>(android.support.v7.appcompat.R.id.search_src_text)
        textSearch?.setTextColor(Color.WHITE)

        view?.list_tags?.apply {
            val manager = LinearLayoutManager(context)
            layoutManager = manager
            adapter = mListAdapter
            addItemDecoration(DividerItemDecoration(context, manager.orientation))
        }
    }

    interface OnTagsDialogInteractionListener {

        fun onTagsDialogPositiveButtonClick(tags: List<MyTag>)

        fun onTagsDialogTagEdited()

        fun onTagsDialogTagDeleted()
    }
}