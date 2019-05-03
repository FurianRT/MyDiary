package com.furianrt.mydiary.dialogs.tags.fragments.list

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyTag
import kotlinx.android.synthetic.main.tags_list_item.view.*

class TagListAdapter(
        val listener: OnTagListItemInteractionListener
) : RecyclerView.Adapter<TagListAdapter.TagsViewHolder>() {

    private val mTags = mutableListOf<MyTag>()

    fun showList(tags: MutableList<MyTag>) {
        val diffCallback = TagListDiffCallback(mTags, tags)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        mTags.clear()
        mTags.addAll(tags)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagsViewHolder =
            TagsViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.tags_list_item, parent, false))

    override fun onBindViewHolder(holder: TagsViewHolder, position: Int) {
        holder.bind(mTags[position])
    }

    override fun getItemCount(): Int = mTags.size

    inner class TagsViewHolder(view: View) : RecyclerView.ViewHolder(view), PopupMenu.OnMenuItemClickListener {

        private lateinit var mTag: MyTag

        fun bind(tag: MyTag) {
            mTag = tag
            with(itemView) {
                text_tag_name.text = tag.name
                setOnClickListener {
                    image_select_tag.visibility = if (image_select_tag.visibility == View.INVISIBLE) {
                        listener.onItemCheckChange(tag, true)
                        View.VISIBLE
                    } else {
                        listener.onItemCheckChange(tag, false)
                        View.INVISIBLE
                    }
                }
                image_select_tag.visibility = if (tag.isChecked) {
                    View.VISIBLE
                } else {
                    View.INVISIBLE
                }
                button_tag_more.setOnClickListener {
                    val popup = PopupMenu(it.context, it)
                    popup.setOnMenuItemClickListener(this@TagsViewHolder)
                    popup.inflate(R.menu.tags_list_item_menu)
                    popup.show()
                }
            }
        }

        override fun onMenuItemClick(item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.menu_tag_delete -> {
                    listener.onItemDeleteClick(mTag)
                    true
                }
                R.id.menu_tag_edit -> {
                    listener.onItemEditClick(mTag)
                    true
                }
                else -> false
            }
        }
    }

    interface OnTagListItemInteractionListener {
        fun onItemCheckChange(tag: MyTag, checked: Boolean)
        fun onItemEditClick(tag: MyTag)
        fun onItemDeleteClick(tag: MyTag)
    }

    class TagListDiffCallback(
            private val oldList: List<MyTag>,
            private val newList: List<MyTag>
    ) : DiffUtil.Callback() {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                oldList[oldItemPosition].id == newList[newItemPosition].id

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                oldList[oldItemPosition] == newList[newItemPosition]

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size
    }
}