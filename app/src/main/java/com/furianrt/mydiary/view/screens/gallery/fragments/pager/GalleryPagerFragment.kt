/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.screens.gallery.fragments.pager

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.viewpager2.widget.ViewPager2
import com.furianrt.mydiary.R
import com.furianrt.mydiary.analytics.MyAnalytics
import com.furianrt.mydiary.view.base.BaseFragment
import com.furianrt.mydiary.data.model.MyImage
import com.furianrt.mydiary.view.dialogs.delete.image.DeleteImageDialog
import com.furianrt.mydiary.view.screens.gallery.fragments.list.GalleryListFragment
import com.furianrt.mydiary.utils.getThemeAccentColor
import com.furianrt.mydiary.utils.getThemePrimaryColor
import com.furianrt.mydiary.utils.getThemePrimaryDarkColor
import com.furianrt.mydiary.utils.inTransaction
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_gallery.*
import kotlinx.android.synthetic.main.fragment_gallery_pager.*
import kotlinx.android.synthetic.main.fragment_gallery_pager.view.*
import javax.inject.Inject

class GalleryPagerFragment : BaseFragment(), GalleryPagerContract.MvpView {

    companion object {

        const val TAG = "GalleryPagerFragment"
        private const val ARG_POSITION = "position"
        private const val ARG_NOTE_ID = "noteId"
        private const val IMAGE_EDITOR_REQUEST_CODE = 2

        @JvmStatic
        fun newInstance(noteId: String, position: Int) =
                GalleryPagerFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_NOTE_ID, noteId)
                        putInt(ARG_POSITION, position)
                    }
                }
    }

    @Inject
    lateinit var mPresenter: GalleryPagerContract.Presenter

    private val mPagerAdapter = GalleryPagerAdapter()
    private var mPagerPosition = 0

    private val mOnPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            mPagerPosition = position
            showImageCounter(position + 1, mPagerAdapter.itemCount)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        mPresenter.init(arguments?.getString(ARG_NOTE_ID)!!)

        mPagerPosition = savedInstanceState?.getInt(ARG_POSITION)
                ?: (arguments?.getInt(ARG_POSITION) ?: 0)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_gallery_pager, container, false)

        view.pager_gallery.adapter = mPagerAdapter
        view.pager_gallery.isSaveEnabled = false

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(ARG_POSITION, pager_gallery.currentItem)
    }

    override fun onStart() {
        super.onStart()
        pager_gallery.registerOnPageChangeCallback(mOnPageChangeCallback)
        mPresenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        pager_gallery.unregisterOnPageChangeCallback(mOnPageChangeCallback)
        mPresenter.detachView()
    }

    override fun showImages(images: List<MyImage>) {
        if (mPagerPosition >= images.size) {
            mPagerPosition = images.size - 1
        }
        mPagerAdapter.submitImages(images)
        pager_gallery.setCurrentItem(mPagerPosition, false)
        showImageCounter(pager_gallery.currentItem + 1, images.size)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_gallery_pager_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.menu_list_mode -> {
            analytics.sendEvent(MyAnalytics.EVENT_NOTE_IMAGE_LIST_OPENED)
            mPresenter.onButtonListModeClick()
            true
        }
        R.id.menu_delete -> {
            analytics.sendEvent(MyAnalytics.EVENT_NOTE_IMAGE_PAGER_IMAGE_DELETE)
            mPresenter.onButtonDeleteClick(mPagerAdapter.getItem(mPagerPosition))
            true
        }
        R.id.menu_edit -> {
            mPresenter.onButtonEditClick(mPagerAdapter.getItem(mPagerPosition))
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun showDeleteConfirmationDialog(image: MyImage) {
        DeleteImageDialog.newInstance(listOf(image.name))
                .show(requireActivity().supportFragmentManager, DeleteImageDialog.TAG)
    }

    override fun showListImagesView(noteId: String) {
        fragmentManager?.inTransaction {
            replace(R.id.container_gallery, GalleryListFragment.newInstance(noteId), GalleryListFragment.TAG)
        }
    }

    override fun showEditImageView(image: MyImage) {
        val uri = Uri.parse(image.uri)
        UCrop.of(uri, uri)
                .withOptions(UCrop.Options().apply {
                    setToolbarTitle(getString(R.string.fragment_gallery_pager_edit_photo))
                    setToolbarWidgetColor(Color.WHITE)
                    setActiveWidgetColor(requireContext().getThemeAccentColor())
                    setStatusBarColor(requireContext().getThemePrimaryDarkColor())
                    setToolbarColor(requireContext().getThemePrimaryColor())
                    setCompressionQuality(100)
                    setFreeStyleCropEnabled(true)
                })
                .start(requireContext(), this, IMAGE_EDITOR_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_EDITOR_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            analytics.sendEvent(MyAnalytics.EVENT_NOTE_IMAGE_PAGER_IMAGE_EDITED)
            mPresenter.onImageEdited()
        }
    }

    private fun showImageCounter(current: Int, count: Int) {
        activity?.text_toolbar_title?.text = getString(R.string.counter_format, current, count)
    }
}
