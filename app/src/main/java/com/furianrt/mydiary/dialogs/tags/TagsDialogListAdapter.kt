package com.furianrt.mydiary.dialogs.tags

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyTag
import kotlinx.android.synthetic.main.dialog_tags_list_item.view.*

class TagsDialogListAdapter(val listener: OnTagChangedListener)
    : ListAdapter<MyTag, TagsDialogListAdapter.TagsDialogViewHolder>(TagsDialogDiffCallback()) {

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
            View.OnClickListener, CompoundButton.OnCheckedChangeListener,
            PopupMenu.OnMenuItemClickListener {

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
                    popup.setOnMenuItemClickListener(this@TagsDialogViewHolder)
                    popup.inflate(R.menu.dialog_tags_list_item_menu)
                    popup.show()
                }
            }
        }

        override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
            mTag.isChecked = isChecked
            listener.onTagClicked(mTag)
        }

        override fun onClick(v: View) {
            v.check_tag.isChecked = !v.check_tag.isChecked
        }

        override fun onMenuItemClick(item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.menu_tag_delete -> {
                    listener.onTagDeleted(mTag)
                    true
                }
                R.id.menu_tag_edit -> {
                    listener.onTagEdited(mTag)
                    true
                }
                else -> false
            }
        }
    }
}