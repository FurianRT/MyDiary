package com.furianrt.mydiary.gallery.fragments.pager

import com.furianrt.mydiary.data.model.MyImage
import com.furianrt.mydiary.gallery.fragments.pager.image.GalleryImageFragment

class GalleryPagerAdapter(
        var list: List<MyImage>,
        fm: androidx.fragment.app.FragmentManager
) : androidx.fragment.app.FragmentStatePagerAdapter(fm) {

    override fun getCount() = list.size

    override fun getItem(position: Int): androidx.fragment.app.Fragment = GalleryImageFragment.newInstance(list[position])
}