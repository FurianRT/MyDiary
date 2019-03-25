package com.furianrt.mydiary.gallery.fragments.pager

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyImage
import com.furianrt.mydiary.gallery.fragments.list.GalleryListFragment
import com.furianrt.mydiary.general.DeleteConfirmDialog
import com.furianrt.mydiary.utils.getThemeAccentColor
import com.furianrt.mydiary.utils.getThemePrimaryColor
import com.furianrt.mydiary.utils.getThemePrimaryDarkColor
import com.furianrt.mydiary.utils.inTransaction
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_gallery.*
import kotlinx.android.synthetic.main.fragment_gallery_pager.*
import kotlinx.android.synthetic.main.fragment_gallery_pager.view.*
import java.util.*
import javax.inject.Inject

class GalleryPagerFragment : Fragment(), GalleryPagerContract.View,
        DeleteConfirmDialog.OnDeleteConfirmListener {

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

    private var mPagerPosition = 0
    private lateinit var mPagerAdapter: GalleryPagerAdapter

    private val mOnPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {}
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
        override fun onPageSelected(position: Int) {
            mPagerPosition = position
            showImageCounter(mPagerPosition + 1, mPagerAdapter.count)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(context!!).inject(this)
        super.onCreate(savedInstanceState)
        mPagerPosition = savedInstanceState?.getInt(ARG_POSITION)
                ?: (arguments?.getInt(ARG_POSITION) ?: 0)
        val noteId = arguments?.getString(ARG_NOTE_ID) ?: throw IllegalStateException()
        mPresenter.setNoteId(noteId)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_gallery_pager, container, false)

        mPagerAdapter = GalleryPagerAdapter(ArrayList(), childFragmentManager)
        view.pager_gallery.adapter = mPagerAdapter

        return view
    }

    override fun onResume() {
        super.onResume()
        pager_gallery.addOnPageChangeListener(mOnPageChangeListener)
        mPresenter.attachView(this)
        mPresenter.onViewStart()
        (activity?.supportFragmentManager?.findFragmentByTag(DeleteConfirmDialog.TAG) as? DeleteConfirmDialog)
                ?.setOnDeleteConfirmListener(this)
    }

    override fun onPause() {
        super.onPause()
        pager_gallery.removeOnPageChangeListener(mOnPageChangeListener)
        mPresenter.detachView()
    }

    override fun showImages(images: List<MyImage>) {
        if (mPagerPosition >= images.size) {
            mPagerPosition = images.size - 1
        }
        showImageCounter(mPagerPosition + 1, images.size)
        mPagerAdapter.images = images
        mPagerAdapter.notifyDataSetChanged()
        pager_gallery.setCurrentItem(mPagerPosition, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.fragment_gallery_pager_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.menu_list_mode -> {
                mPresenter.onButtonListModeClick()
                true
            }
            R.id.menu_delete -> {
                showDeleteConfirmationDialog()
                true
            }
            R.id.menu_edit -> {
                mPresenter.onButtonEditClick(mPagerAdapter.images[mPagerPosition])
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showDeleteConfirmationDialog() {
        DeleteConfirmDialog.newInstance(resources.getQuantityString(
                R.plurals.image_delete_confirmation,
                1,
                1)
        ).apply {
            setOnDeleteConfirmListener(this@GalleryPagerFragment)
        }.show(activity?.supportFragmentManager, DeleteConfirmDialog.TAG)
    }

    override fun onDialogButtonDeleteClick() {
        mPresenter.onButtonDeleteClick(mPagerAdapter.images[mPagerPosition])
    }

    override fun showListImagesView(noteId: String) {
        fragmentManager?.inTransaction {
            replace(R.id.container_gallery, GalleryListFragment.newInstance(noteId), GalleryListFragment.TAG)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(ARG_POSITION, pager_gallery.currentItem)
    }

    override fun showEditImageView(image: MyImage) {
        val uri = Uri.parse(image.uri)
        UCrop.of(uri, uri)
                .withOptions(UCrop.Options().apply {
                    setToolbarTitle(getString(R.string.fragment_gallery_pager_edit_photo))
                    setActiveWidgetColor(getThemeAccentColor(context!!))
                    setStatusBarColor(getThemePrimaryDarkColor(context!!))
                    setToolbarColor(getThemePrimaryColor(context!!))
                    setCompressionQuality(100)
                    setFreeStyleCropEnabled(true)
                })
                .start(context!!, this, IMAGE_EDITOR_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_EDITOR_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            mPresenter.onImageEdited()
        }
    }

    private fun showImageCounter(current: Int, count: Int) {
        val textCounter = "$current/$count"
        activity?.text_toolbar_title?.text = textCounter
    }
}
