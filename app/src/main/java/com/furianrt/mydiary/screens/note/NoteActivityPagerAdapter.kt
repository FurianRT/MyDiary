package com.furianrt.mydiary.screens.note

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.furianrt.mydiary.data.model.MyNoteWithProp
import com.furianrt.mydiary.screens.note.fragments.notefragment.NoteFragment

class NoteActivityPagerAdapter(
        fm: FragmentManager,
        private val mode: NoteActivity.Companion.Mode
) : FragmentStatePagerAdapter(fm) {

    var list: List<MyNoteWithProp> = ArrayList()

    override fun getItem(position: Int): Fragment =
            NoteFragment.newInstance(list[position].note, mode)

    override fun getCount(): Int = list.count()
}