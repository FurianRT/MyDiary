package com.furianrt.mydiary.note.dialogs

import android.support.v7.util.DiffUtil
import com.furianrt.mydiary.data.model.MyTag

class TagsDialogDiffCallback : DiffUtil.ItemCallback<MyTag>() {

    override fun areItemsTheSame(oldItem: MyTag, newItem: MyTag): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: MyTag, newItem: MyTag): Boolean {
        return oldItem == newItem
    }


}