package com.furianrt.mydiary.note

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.furianrt.mydiary.data.model.MyNoteWithProp
import com.furianrt.mydiary.note.fragments.NoteFragment

class NotePagerAdapter(fm: FragmentManager, private val mode: Mode) : FragmentStatePagerAdapter(fm) {

    var list: List<MyNoteWithProp> = ArrayList()

    override fun getItem(position: Int): Fragment {
        return NoteFragment.newInstance(list[position], mode)
    }

    override fun getCount(): Int {
        return list.count()
    }
}