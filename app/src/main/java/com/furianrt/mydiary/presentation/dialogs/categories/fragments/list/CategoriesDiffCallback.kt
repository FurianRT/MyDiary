/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.dialogs.categories.fragments.list

import androidx.recyclerview.widget.DiffUtil
import com.furianrt.mydiary.presentation.dialogs.categories.fragments.list.CategoriesListAdapter.*

class CategoriesDiffCallback : DiffUtil.ItemCallback<CategoryItemView>() {

    override fun areItemsTheSame(oldItem: CategoryItemView, newItem: CategoryItemView): Boolean =
            oldItem.category?.id == newItem.category?.id

    override fun areContentsTheSame(oldItem: CategoryItemView, newItem: CategoryItemView): Boolean =
            oldItem == newItem
}