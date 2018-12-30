package com.furianrt.mydiary.note

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.furianrt.mydiary.data.model.MyNoteWithProp
import com.furianrt.mydiary.note.fragments.notefragment.NoteFragment

class NoteActivityPagerAdapter(fm: FragmentManager, private val mode: Mode)
    : FragmentStatePagerAdapter(fm) {

    var list: List<MyNoteWithProp> = ArrayList()

    private var mCurrentFragment: Fragment? = null

    fun getCurrentFragment(): Fragment? {
        return mCurrentFragment
    }

    override fun getItem(position: Int): Fragment =
            NoteFragment.newInstance(list[position].note.id, mode)

    override fun getCount(): Int = list.count()

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        if (getCurrentFragment() !== `object`) {
            mCurrentFragment = `object` as Fragment
        }
        super.setPrimaryItem(container, position, `object`)
    }
}