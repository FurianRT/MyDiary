/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.dialogs.tags.fragments.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.entity.MyTag
import kotlinx.android.synthetic.main.tags_list_item.view.*

class TagListAdapter(
        val listener: OnTagListItemInteractionListener
) : RecyclerView.Adapter<TagListAdapter.TagsViewHolder>() {

    data class ViewItem(
            val tag: MyTag,
            var count: Int = 0,
            var isChecked: Boolean = false
    )

    private val mItems = mutableListOf<ViewItem>()

    fun showList(items: List<ViewItem>) {
        val diffResult = DiffUtil.calculateDiff(TagListDiffCallback(mItems, items))
        mItems.clear()
        mItems.addAll(items)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagsViewHolder =
            TagsViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.tags_list_item, parent, false))

    override fun onBindViewHolder(holder: TagsViewHolder, position: Int) {
        holder.bind(mItems[position])
    }

    override fun getItemCount(): Int = mItems.size

    inner class TagsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(item: ViewItem) {
            with(itemView) {
                text_tag_name.text = item.tag.name
                text_tag_count.text = item.count.toString()

                setOnClickListener {
                    image_select_tag.visibility = if (image_select_tag.visibility == View.INVISIBLE) {
                        item.isChecked = true
                        listener.onItemCheckChange(item)
                        View.VISIBLE
                    } else {
                        item.isChecked = false
                        listener.onItemCheckChange(item)
                        View.INVISIBLE
                    }
                }

                val menuListener = PopupMenu.OnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.menu_tag_delete -> {
                            listener.onItemDeleteClick(item)
                            true
                        }
                        R.id.menu_tag_edit -> {
                            listener.onItemEditClick(item)
                            true
                        }
                        else -> false
                    }
                }

                setOnLongClickListener {
                    showPopupMenu(it, menuListener)
                    return@setOnLongClickListener true
                }

                button_tag_more.setOnClickListener { showPopupMenu(it, menuListener) }

                image_select_tag.visibility = if (item.isChecked) {
                    View.VISIBLE
                } else {
                    View.INVISIBLE
                }
            }
        }

        private fun showPopupMenu(view: View, listener: PopupMenu.OnMenuItemClickListener) {
            val popup = PopupMenu(view.context, view)
            popup.setOnMenuItemClickListener(listener)
            popup.inflate(R.menu.tags_list_item_menu)
            popup.show()
        }
    }

    interface OnTagListItemInteractionListener {
        fun onItemCheckChange(item: ViewItem)
        fun onItemEditClick(item: ViewItem)
        fun onItemDeleteClick(item: ViewItem)
    }

    class TagListDiffCallback(
            private val oldList: List<ViewItem>,
            private val newList: List<ViewItem>
    ) : DiffUtil.Callback() {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                oldList[oldItemPosition].tag.id == newList[newItemPosition].tag.id

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                oldList[oldItemPosition] == newList[newItemPosition]

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size
    }
}