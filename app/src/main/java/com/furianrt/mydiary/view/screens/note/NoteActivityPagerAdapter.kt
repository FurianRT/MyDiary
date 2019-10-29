/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.screens.note

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.furianrt.mydiary.view.screens.note.fragments.mainnote.NoteFragment

class NoteActivityPagerAdapter(
        activity: FragmentActivity,
        private val isNewNote: Boolean
) : FragmentStateAdapter(activity) {

    private val mNoteIds = mutableListOf<String>()

    fun submitList(noteIds: List<String>) {
        val diffCallback = NoteListDiffCallback(mNoteIds, noteIds)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        mNoteIds.clear()
        mNoteIds.addAll(noteIds)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun createFragment(position: Int): Fragment =
            NoteFragment.newInstance(mNoteIds[position], isNewNote)

    override fun getItemCount(): Int = mNoteIds.size

    override fun getItemId(position: Int): Long = mNoteIds[position].hashCode().toLong()

    override fun containsItem(itemId: Long): Boolean =
            mNoteIds.map { it.hashCode().toLong() }.contains(itemId)

    private class NoteListDiffCallback(
            private val oldList: List<String>,
            private val newList: List<String>
    ) : DiffUtil.Callback() {

        override fun getNewListSize(): Int = newList.size

        override fun getOldListSize(): Int = oldList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                oldList[oldItemPosition] == newList[newItemPosition]

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                areItemsTheSame(oldItemPosition, newItemPosition)
    }
}