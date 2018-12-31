package com.furianrt.mydiary.gallery.fragments.pager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.furianrt.mydiary.data.model.MyImage
import com.furianrt.mydiary.gallery.fragments.pager.image.GalleryImageFragment

class GalleryPagerAdapter(
        var list: List<MyImage>,
        fm: FragmentManager
) : FragmentStatePagerAdapter(fm) {

    override fun getCount() = list.size

    override fun getItem(position: Int): Fragment = GalleryImageFragment.newInstance(list[position])
}