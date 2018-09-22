package com.furianrt.mydiary.note.fragments.notefragment

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import com.furianrt.mydiary.data.model.MyImage
import com.furianrt.mydiary.note.fragments.notefragment.toolbarimage.NoteImageFragment

class NoteFragmentPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    var images: List<MyImage> = ArrayList()

    override fun getCount(): Int = images.count()

    override fun getItem(position: Int): Fragment = NoteImageFragment.newInstance(images[position])

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }
}