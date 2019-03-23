package com.furianrt.mydiary.note.fragments.notefragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.furianrt.mydiary.data.model.MyImage
import com.furianrt.mydiary.note.fragments.notefragment.toolbarimage.NoteImageFragment

class NoteFragmentPagerAdapter(
        fm: FragmentManager,
        var images: List<MyImage> = ArrayList()
) : FragmentStatePagerAdapter(fm) {

    override fun getCount(): Int = images.count()

    override fun getItem(position: Int): Fragment = NoteImageFragment.newInstance(images[position])

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }
}