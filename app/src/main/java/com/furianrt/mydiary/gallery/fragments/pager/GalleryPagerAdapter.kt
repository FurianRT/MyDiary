package com.furianrt.mydiary.gallery.fragments.pager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.furianrt.mydiary.data.model.MyImage
import com.furianrt.mydiary.gallery.fragments.pager.image.GalleryImageFragment

class GalleryPagerAdapter(
        var images: List<MyImage>,
        fm: FragmentManager
) : FragmentStatePagerAdapter(fm) {

    override fun getCount() = images.size

    override fun getItem(position: Int): Fragment = GalleryImageFragment.newInstance(images[position])

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }
}