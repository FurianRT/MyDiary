/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.dialogs.moods

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.entity.MyMood
import kotlinx.android.synthetic.main.dialog_moods_list_item.view.*

class MoodsDialogListAdapter(
        var moods: List<MyMood>,
        var listener: OnMoodListInteractionListener?
) : RecyclerView.Adapter<MoodsDialogListAdapter.MoodsListViewHolder>() {

    override fun getItemCount(): Int = moods.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodsListViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.dialog_moods_list_item, parent, false)
        return MoodsListViewHolder(view)
    }

    override fun onBindViewHolder(holder: MoodsListViewHolder, position: Int) {
        holder.bind(moods[position])
    }

    inner class MoodsListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(mood: MyMood) {
            itemView.setOnClickListener { listener?.onMoodClicked(mood) }
            itemView.text_item_mood.text = mood.name
            val smile = itemView.context.resources
                    .getIdentifier(mood.iconName, "drawable", itemView.context.packageName)
            itemView.text_item_mood.setCompoundDrawablesWithIntrinsicBounds(smile, 0, 0, 0)
        }
    }

    interface OnMoodListInteractionListener {
        fun onMoodClicked(mood: MyMood)
    }
}