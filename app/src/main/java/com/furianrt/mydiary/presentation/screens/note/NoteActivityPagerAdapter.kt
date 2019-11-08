/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.note

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.furianrt.mydiary.presentation.screens.note.fragments.mainnote.NoteFragment

class NoteActivityPagerAdapter(
        fm: FragmentManager,
        private val isNewNote: Boolean
) : FragmentStatePagerAdapter(fm, BEHAVIOR_SET_USER_VISIBLE_HINT) {

    private var mIsSizeChanged = false

    var noteIds = emptyList<String>()
        set(value) {
            mIsSizeChanged = field.size != value.size
            field = value
        }

    override fun getItem(position: Int) = NoteFragment.newInstance(noteIds[position], isNewNote)

    override fun getCount(): Int = noteIds.size

    override fun getItemPosition(`object`: Any): Int =
            if (mIsSizeChanged) {
                PagerAdapter.POSITION_NONE
            } else {
                super.getItemPosition(`object`)
            }
}