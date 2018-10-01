package com.furianrt.mydiary.note

import com.furianrt.mydiary.data.model.MyNoteWithProp
import com.furianrt.mydiary.note.fragments.notefragment.NoteFragment

class NoteActivityPagerAdapter(fm: androidx.fragment.app.FragmentManager, private val mode: Mode)
    : androidx.fragment.app.FragmentStatePagerAdapter(fm) {

    var list: List<MyNoteWithProp> = ArrayList()

    override fun getItem(position: Int): androidx.fragment.app.Fragment = NoteFragment.newInstance(list[position], mode)

    override fun getCount(): Int = list.count()
}