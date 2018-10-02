package com.furianrt.mydiary.note.dialogs.categories.list

import androidx.recyclerview.widget.DiffUtil
import com.furianrt.mydiary.data.model.MyCategory

class CategoriesDiffCallback : DiffUtil.ItemCallback<MyCategory>() {

    override fun areItemsTheSame(oldItem: MyCategory, newItem: MyCategory): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MyCategory, newItem: MyCategory): Boolean {
        return oldItem == newItem
    }
}