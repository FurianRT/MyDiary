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