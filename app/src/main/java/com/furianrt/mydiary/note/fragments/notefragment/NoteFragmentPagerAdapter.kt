package com.furianrt.mydiary.note.fragments.notefragment

import com.furianrt.mydiary.data.model.MyImage
import com.furianrt.mydiary.note.fragments.notefragment.toolbarimage.NoteImageFragment

class NoteFragmentPagerAdapter(fm: androidx.fragment.app.FragmentManager) : androidx.fragment.app.FragmentStatePagerAdapter(fm) {

    var images: List<MyImage> = ArrayList()

    override fun getCount(): Int = images.count()

    override fun getItem(position: Int): androidx.fragment.app.Fragment = NoteImageFragment.newInstance(images[position])

    override fun getItemPosition(`object`: Any): Int {
        return androidx.viewpager.widget.PagerAdapter.POSITION_NONE
    }
}