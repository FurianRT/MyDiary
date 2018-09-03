package com.furianrt.mydiary.note.dialogs

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyTag
import kotlinx.android.synthetic.main.dialog_tags_list_item.view.*
import android.support.v7.widget.PopupMenu
import android.widget.CompoundButton

class TagsDialogListAdapter(private val mListener: OnTagChangedListener)
    : ListAdapter<MyTag,
        TagsDialogListAdapter.TagsDialogViewHolder>(TagsDialogDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagsDialogViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.dialog_tags_list_item, parent, false)
        return TagsDialogViewHolder(view)
    }

    override fun onBindViewHolder(holder: TagsDialogViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    interface OnTagChangedListener {

        fun onTagClicked(tag: MyTag)

        fun onTagDeleted(tag: MyTag)

        fun onTagEdited(tag: MyTag)
    }

    inner class TagsDialogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
            View.OnClickListener, CompoundButton.OnCheckedChangeListener {

        private lateinit var mTag: MyTag

        fun bind(tag: MyTag) {
            mTag = tag
            itemView.apply {
                setOnClickListener(this@TagsDialogViewHolder)
                check_tag.setOnCheckedChangeListener(this@TagsDialogViewHolder)
                check_tag.text = tag.name
                check_tag.isChecked = tag.isChecked
                button_tag_more.setOnClickListener {
                    val popup = PopupMenu(it.context, it)
                    popup.inflate(R.menu.dialog_tags_list_item_menu)
                    popup.show()
                }
            }
        }

        override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
            mTag.isChecked = isChecked
            mListener.onTagClicked(mTag)
        }

        override fun onClick(v: View) {
            v.check_tag.isChecked = !v.check_tag.isChecked
        }
    }
}