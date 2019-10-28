/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.dialogs.categories.fragments.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.furianrt.mydiary.R
import com.furianrt.mydiary.model.entity.MyCategory
import com.furianrt.mydiary.view.dialogs.categories.fragments.list.CategoriesListAdapter.*
import kotlinx.android.synthetic.main.fragment_category_list_item.view.*

class CategoriesListAdapter(
        val listener: OnCategoryListInteractionListener? = null
) : ListAdapter<CategoryItemView, CategoriesViewHolder>(CategoriesDiffCallback()) {

    data class CategoryItemView(
            val category: MyCategory? = null,
            val noteCount: Int = 0
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_category_list_item, parent, false)
        return CategoriesViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CategoriesViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(item: CategoryItemView) {
            val category = item.category
            if (category != null) {
                val popupMenuListener = PopupMenu.OnMenuItemClickListener { popup ->
                    when (popup.itemId) {
                        R.id.menu_category_delete -> {
                            listener?.onCategoryDelete(category)
                            true
                        }
                        R.id.menu_category_edit -> {
                            listener?.onCategoryEdit(category)
                            true
                        }
                        else -> false
                    }
                }

                itemView.setOnClickListener { listener?.onCategoryClick(item.category) }
                itemView.setOnLongClickListener {
                    showPopupMenu(it, popupMenuListener)
                    return@setOnLongClickListener true
                }
                itemView.button_category_more.setOnClickListener {
                    showPopupMenu(it, popupMenuListener)
                }
                itemView.text_item_category.text = item.category.name
                itemView.text_category_count.text = item.noteCount.toString()
                itemView.layout_category_color.setBackgroundColor(item.category.color)
                itemView.layout_category_color.visibility = View.VISIBLE
                itemView.button_category_more.visibility = View.VISIBLE
            } else {
                itemView.layout_category_color.visibility = View.INVISIBLE
                itemView.button_category_more.visibility = View.INVISIBLE
                itemView.text_category_count.text = item.noteCount.toString()
                itemView.text_item_category.text = itemView.context.getString(R.string.no_category)
                itemView.setOnClickListener { listener?.onNoCategoryClick() }
            }
        }

        private fun showPopupMenu(view: View, listener: PopupMenu.OnMenuItemClickListener) {
            val popup = PopupMenu(view.context, view)
            popup.setOnMenuItemClickListener(listener)
            popup.inflate(R.menu.dialog_categories_list_item_menu)
            popup.show()
        }
    }

    interface OnCategoryListInteractionListener {
        fun onCategoryClick(category: MyCategory)
        fun onNoCategoryClick()
        fun onCategoryDelete(category: MyCategory)
        fun onCategoryEdit(category: MyCategory)
    }
}