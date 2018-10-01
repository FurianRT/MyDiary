package com.furianrt.mydiary.note.dialogs.tags

import androidx.recyclerview.widget.DiffUtil
import com.furianrt.mydiary.data.model.MyTag

class TagsDialogDiffCallback : DiffUtil.ItemCallback<MyTag>() {

    override fun areItemsTheSame(oldItem: MyTag, newItem: MyTag): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MyTag, newItem: MyTag): Boolean {
        return oldItem == newItem
    }
}