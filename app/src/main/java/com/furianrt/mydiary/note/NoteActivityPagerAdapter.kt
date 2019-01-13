package com.furianrt.mydiary.note

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.furianrt.mydiary.data.model.MyNoteWithProp
import com.furianrt.mydiary.note.fragments.notefragment.NoteFragment

class NoteActivityPagerAdapter(fm: FragmentManager, private val mode: Mode)
    : FragmentStatePagerAdapter(fm) {

    var list: List<MyNoteWithProp> = ArrayList()

    override fun getItem(position: Int): Fragment =
            NoteFragment.newInstance(list[position].note.id, mode)

    override fun getCount(): Int = list.count()
}