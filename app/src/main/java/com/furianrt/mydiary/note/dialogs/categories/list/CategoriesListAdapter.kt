package com.furianrt.mydiary.note.dialogs.categories.list

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

    inner class CategoriesDialogViewHolder(private val mView: View) : RecyclerView.ViewHolder(mView),
            View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        private lateinit var mCategory: MyCategory

        fun bind(category: MyCategory) {
            mCategory = category
            mView.apply {
                setOnClickListener(this@CategoriesDialogViewHolder)
                text_item_category.text = category.name
                layout_category_color.setBackgroundColor(category.color)
                button_category_more.setOnClickListener {
                    val popup = PopupMenu(it.context, it)
                    popup.setOnMenuItemClickListener(this@CategoriesDialogViewHolder)
                    popup.inflate(R.menu.dialog_categories_list_item_menu)
                    popup.show()
                }
            }
        }

        override fun onClick(v: View?) {
            listener.onCategoryClick(mCategory)
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