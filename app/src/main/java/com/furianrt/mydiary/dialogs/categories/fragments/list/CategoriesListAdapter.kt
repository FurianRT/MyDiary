package com.furianrt.mydiary.dialogs.categories.fragments.list

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyCategory
import kotlinx.android.synthetic.main.fragment_category_list_item.view.*

class CategoriesListAdapter(
        val listener: OnCategoryListInteractionListener
) : ListAdapter<MyCategory,
        CategoriesListAdapter.CategoriesDialogViewHolder>(CategoriesDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesDialogViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_category_list_item, parent, false)
        return CategoriesDialogViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoriesDialogViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CategoriesDialogViewHolder(view: View) : RecyclerView.ViewHolder(view),
            PopupMenu.OnMenuItemClickListener {

        private lateinit var mCategory: MyCategory

        fun bind(category: MyCategory) {
            mCategory = category
            with(itemView) {
                setOnClickListener {
                    listener.onCategoryClick(category)
                }
                setOnLongClickListener {
                    showPopupMenu(it, this@CategoriesDialogViewHolder)
                    return@setOnLongClickListener true
                }
                button_category_more.setOnClickListener {
                    showPopupMenu(it, this@CategoriesDialogViewHolder)
                }
                text_item_category.text = category.name
                layout_category_color.setBackgroundColor(category.color)
            }
        }

        private fun showPopupMenu(view: View, listener: PopupMenu.OnMenuItemClickListener) {
            val popup = PopupMenu(view.context, view)
            popup.setOnMenuItemClickListener(listener)
            popup.inflate(R.menu.dialog_categories_list_item_menu)
            popup.show()
        }

        override fun onMenuItemClick(item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.menu_category_delete -> {
                    listener.onCategoryDelete(mCategory)
                    true
                }
                R.id.menu_category_edit -> {
                    listener.onCategoryEdit(mCategory)
                    true
                }
                else -> false
            }
        }
    }

    interface OnCategoryListInteractionListener {
        fun onCategoryClick(category: MyCategory)
        fun onCategoryDelete(category: MyCategory)
        fun onCategoryEdit(category: MyCategory)
    }
}