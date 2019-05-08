package com.furianrt.mydiary.screens.note

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.furianrt.mydiary.data.model.MyNoteWithProp
import com.furianrt.mydiary.screens.note.fragments.notefragment.NoteFragment

class NoteActivityPagerAdapter(
        fm: FragmentManager,
        private val mode: NoteActivity.Companion.Mode
) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private var mIsSizeChanged = false

    var list: List<MyNoteWithProp> = ArrayList()
        set(value) {
            mIsSizeChanged = field.size != value.size
            field = value
        }

    override fun getItem(position: Int) = NoteFragment.newInstance(list[position].note, mode)

    override fun getCount(): Int = list.count()

    override fun getItemPosition(`object`: Any): Int =
            if (mIsSizeChanged) {
                PagerAdapter.POSITION_NONE
            } else {
                super.getItemPosition(`object`)
            }
}