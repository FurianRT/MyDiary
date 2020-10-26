/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.gallery.fragments.list

import android.Manifest
import android.animation.Animator
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.furianrt.mydiary.R
import com.furianrt.mydiary.analytics.MyAnalytics
import com.furianrt.mydiary.presentation.base.BaseFragment
import com.furianrt.mydiary.model.entity.MyImage
import com.furianrt.mydiary.presentation.dialogs.delete.image.DeleteImageDialog
import com.furianrt.mydiary.presentation.screens.gallery.fragments.list.adapter.GalleryListAdapter
import com.furianrt.mydiary.presentation.screens.gallery.fragments.pager.GalleryPagerFragment
import com.furianrt.mydiary.utils.*
import jp.wasabeef.recyclerview.animators.FadeInUpAnimator
import kotlinx.android.synthetic.main.activity_gallery.*
import kotlinx.android.synthetic.main.empty_state_gallery_list.*
import kotlinx.android.synthetic.main.fragment_gallery_list.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date
import javax.inject.Inject
import kotlin.collections.ArrayList

class GalleryListFragment : BaseFragment(R.layout.fragment_gallery_list), GalleryListAdapter.OnListItemInteractionListener,
        GalleryListContract.View, ActionMode.Callback, DeleteImageDialog.OnDeleteImageConfirmListener {

    companion object {
        const val TAG = "GalleryListFragment"
        private val FAB_DEFAULT_SIZE = dpToPx(46f)
        private val FAB_HIGHLIGHTED_SIZE = dpToPx(60f)
        private const val ARG_NOTE_ID = "note_id"
        private const val VERTICAL_LIST_SPAN_COUNT = 2
        private const val HORIZONTAL_LIST_SPAN_COUNT = 3
        private const val IMAGE_SPAN = 1
        private const val BUNDLE_SELECTION_ACTIVE = "selection_active"
        private const val BUNDLE_SELECTED_IMAGE_NAMES = "selected_image_names"
        private const val BUNDLE_RECYCLER_VIEW_STATE = "recycler_state"
        private const val BUNDLE_PHOTO_PATH = "photo_path"
        private const val STORAGE_PERMISSIONS_REQUEST_CODE = 1
        private const val CAMERA_PERMISSIONS_REQUEST_CODE = 6
        private const val CAMERA_REQUEST_CODE = 7
        private const val IMAGE_PICKER_REQUEST_CODE = 5
        private const val ITEM_HIDING_ALPHA = 0.5f
        private const val ITEM_DEFAULT_ALPHA = 1f
        private const val ITEM_DRAGGING_SCALE = 1.04f
        private const val ITEM_DEFAULT_SCALE = 1f
        private const val ANIMATION_ALPHA_DURATION = 250L
        private const val ANIMATION_SCALE_DURATION = 200L
        private const val ANIMATION_MOVE_DURATION = 500L

        @JvmStatic
        fun newInstance(noteId: String) =
                GalleryListFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_NOTE_ID, noteId)
                    }
                }
    }

    @Inject
    lateinit var presenter: GalleryListContract.Presenter

    private lateinit var mAdapter: GalleryListAdapter
    private var mSelectionActive = false
    private var mRecyclerViewState: Parcelable? = null
    private var mActionMode: ActionMode? = null
    private var mListener: OnGalleryListInteractionListener? = null
    private var mPhotoPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        presenter.init(requireArguments().getString(ARG_NOTE_ID)!!)

        savedInstanceState?.let {
            mRecyclerViewState = it.getParcelable(BUNDLE_RECYCLER_VIEW_STATE)
            mSelectionActive = it.getBoolean(BUNDLE_SELECTION_ACTIVE, false)
            mPhotoPath = it.getString(BUNDLE_PHOTO_PATH)
            presenter.onRestoreInstanceState(it.getStringArrayList(BUNDLE_SELECTED_IMAGE_NAMES)?.toSet())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAdapter = GalleryListAdapter(this@GalleryListFragment, list_gallery)
        val orientation = requireContext().resources.configuration.orientation

        list_gallery.layoutManager = if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            GridLayoutManager(context, HORIZONTAL_LIST_SPAN_COUNT).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int =
                            when (mAdapter.getItemViewType(position)) {
                                GalleryListAdapter.TYPE_FOOTER -> HORIZONTAL_LIST_SPAN_COUNT
                                else -> IMAGE_SPAN
                            }
                }
            }
        } else {
            GridLayoutManager(context, VERTICAL_LIST_SPAN_COUNT).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int =
                            when (mAdapter.getItemViewType(position)) {
                                GalleryListAdapter.TYPE_FOOTER -> VERTICAL_LIST_SPAN_COUNT
                                else -> IMAGE_SPAN
                            }
                }
            }
        }
        list_gallery.adapter = mAdapter
        list_gallery.itemAnimator = FadeInUpAnimator()
        list_gallery.setHasFixedSize(true)

        fab_trash.drawable.mutate().setTint(Color.WHITE)

        if (mSelectionActive) {
            mActionMode = (activity as AppCompatActivity).startSupportActionMode(this)
        }

        button_add_images.setOnClickListener { presenter.onButtonSelectImageClick() }
    }

    override fun onStart() {
        super.onStart()
        presenter.attachView(this)
        (activity?.supportFragmentManager?.findFragmentByTag(DeleteImageDialog.TAG) as? DeleteImageDialog?)
                ?.setOnDeleteConfirmListener(this)
    }

    override fun onStop() {
        super.onStop()
        presenter.detachView()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnGalleryListInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException("$context must implement OnGalleryListInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(BUNDLE_RECYCLER_VIEW_STATE,
                list_gallery.layoutManager?.onSaveInstanceState())
        outState.putStringArrayList(BUNDLE_SELECTED_IMAGE_NAMES, ArrayList(presenter.onSaveInstanceState()))
        outState.putBoolean(BUNDLE_SELECTION_ACTIVE, mSelectionActive)
        mPhotoPath?.let { outState.putString(BUNDLE_PHOTO_PATH, it) }
    }

    override fun showImages(images: List<MyImage>, selectedImageNames: Set<String>) {
        hideEmptyState()
        showImageCount(images.size)
        mRecyclerViewState?.let {
            list_gallery.layoutManager?.onRestoreInstanceState(mRecyclerViewState)
            mRecyclerViewState = null
        }
        mAdapter.selectedImageNames.clear()
        mAdapter.selectedImageNames.addAll(selectedImageNames)
        mAdapter.submitList(images)
    }

    override fun showEmptyList() {
        showEmptyState()
        showImageCount(0)
        mAdapter.selectedImageNames.clear()
        mAdapter.submitList(emptyList())
    }

    override fun selectImages(imageNames: Set<String>) {
        mAdapter.selectedImageNames.addAll(imageNames)
        val listImages = mAdapter.getImages()
        for (imageName in imageNames) {
            mAdapter.notifyItemChanged(listImages.indexOfFirst { it.name == imageName })
        }
    }

    override fun showSelectedImageCount(count: Int) {
        mActionMode?.title = getString(R.string.fragment_gallery_list_selected, count)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_gallery_list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.menu_multi_select -> {
            presenter.onButtonMultiSelectionClick()
            true
        }
        R.id.menu_select_photo -> {
            analytics.sendEvent(MyAnalytics.EVENT_NOTE_IMAGE_LIST_IMAGE_ADD)
            presenter.onButtonSelectImageClick()
            true
        }
        R.id.menu_take_photo -> {
            analytics.sendEvent(MyAnalytics.EVENT_NOTE_IMAGE_LIST_TAKE_PHOTO)
            presenter.onButtonTakePhotoClick()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        activity?.menuInflater?.inflate(R.menu.fragment_gallery_list_cab_menu, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean = false

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean = when (item?.itemId) {
        R.id.menu_delete -> {
            analytics.sendEvent(MyAnalytics.EVENT_NOTE_IMAGE_LIST_IMAGE_DELETE)
            presenter.onButtonCabDeleteClick()
            true
        }

        R.id.menu_select_all -> {
            presenter.onButtonCabSelectAllClick()
            true
        }
        else -> false
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        presenter.onCabCloseSelection()
    }

    override fun activateSelection() {
        mSelectionActive = true
        mActionMode = (activity as AppCompatActivity).startSupportActionMode(this)
    }

    override fun deactivateSelection() {
        mSelectionActive = false
        mAdapter.deactivateSelection()
    }

    override fun showDeleteConfirmationDialog(imageNames: List<String>) {
        DeleteImageDialog.newInstance(imageNames).apply {
            setOnDeleteConfirmListener(this@GalleryListFragment)
        }.show(requireActivity().supportFragmentManager, DeleteImageDialog.TAG)
    }

    override fun onButtonDeleteConfirmClick() {
        presenter.onButtonDeleteConfirmClick()
    }

    override fun onDialogDeleteDismiss(imageNames: List<String>) {
        imageNames.forEach { imageName ->
            mAdapter.notifyItemChanged(mAdapter.getImages().indexOfFirst { it.name == imageName })
        }
    }

    override fun selectImage(imageName: String) {
        mAdapter.selectedImageNames.add(imageName)
        mAdapter.getImages().find { it.name == imageName }?.let {
            mAdapter.notifyItemChanged(mAdapter.getImages().indexOf(it))
        }
    }

    override fun deselectImage(imageName: String) {
        mAdapter.selectedImageNames.remove(imageName)
        mAdapter.getImages().find { it.name == imageName }?.let {
            mAdapter.notifyItemChanged(mAdapter.getImages().indexOf(it))
        }
    }

    override fun onListItemClick(image: MyImage, position: Int) {
        presenter.onListItemClick(image, position, mSelectionActive)
    }

    override fun onImagesOrderChange(images: List<MyImage>) {
        presenter.onImagesOrderChange(mAdapter.getImages())
    }

    override fun onListItemStartDrag(item: View) {
        restoreTrash(false)
        showTrash()
        mAdapter.trashPoint.x = (fab_trash.x + fab_trash.width / 2).toInt()
        mAdapter.trashPoint.y = (fab_trash.y + fab_trash.height / 2).toInt()
        item.animateScale(ITEM_DEFAULT_SCALE, ITEM_DRAGGING_SCALE, ANIMATION_SCALE_DURATION)
    }

    override fun onListItemDropped(item: View) {
        hideTrash()
        item.animateScale(ITEM_DRAGGING_SCALE, ITEM_DEFAULT_SCALE, ANIMATION_SCALE_DURATION)
    }

    override fun onItemDragOverTrash(item: View) {
        highlightTrash()
        item.animateAlpha(ITEM_DEFAULT_ALPHA, ITEM_HIDING_ALPHA, ANIMATION_ALPHA_DURATION)
    }

    override fun onItemDragOutOfTrash(item: View) {
        restoreTrash(true)
        item.animateAlpha(ITEM_HIDING_ALPHA, ITEM_DEFAULT_ALPHA, ANIMATION_ALPHA_DURATION)
    }

    override fun onItemTrashed(image: MyImage) {
        hideTrash()
        analytics.sendEvent(MyAnalytics.EVENT_NOTE_IMAGE_LIST_DRAG_DELETE)
        presenter.onImageTrashed(image)
    }

    override fun showViewImagePager(noteId: String, position: Int) {
        if (parentFragmentManager.findFragmentByTag(GalleryPagerFragment.TAG) == null) {
            parentFragmentManager.inTransaction {
                replace(R.id.container_gallery,
                        GalleryPagerFragment.newInstance(noteId, position), GalleryPagerFragment.TAG)
            }
        }
    }

    override fun requestStoragePermissions() {
        val readExtStorage = Manifest.permission.READ_EXTERNAL_STORAGE
        if (EasyPermissions.hasPermissions(requireContext(), readExtStorage)) {
            presenter.onStoragePermissionsGranted()
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.storage_permission_request),
                    STORAGE_PERMISSIONS_REQUEST_CODE, readExtStorage)
        }
    }

    override fun requestCameraPermissions() {
        val cameraPermission = Manifest.permission.CAMERA
        if (EasyPermissions.hasPermissions(requireContext(), cameraPermission)) {
            presenter.onCameraPermissionsGranted()
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.camera_permission_request),
                    CAMERA_PERMISSIONS_REQUEST_CODE, cameraPermission)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @AfterPermissionGranted(STORAGE_PERMISSIONS_REQUEST_CODE)
    override fun showImageExplorer() {
        try {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            galleryIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivityForResult(galleryIntent, IMAGE_PICKER_REQUEST_CODE)
            mListener?.onGalleryListImagePickerOpen()
        } catch (e: ActivityNotFoundException) {
            try {
                with(Intent()) {
                    type = "image/*"
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    action = Intent.ACTION_GET_CONTENT
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivityForResult(Intent.createChooser(this, ""), IMAGE_PICKER_REQUEST_CODE)
                    mListener?.onGalleryListImagePickerOpen()
                }
            } catch (e: ActivityNotFoundException) {
                analytics.sendEvent(MyAnalytics.EVENT_GALLERY_NOT_FOUND_ERROR)
                Toast.makeText(requireContext(), getString(R.string.phone_related_error), Toast.LENGTH_SHORT).show()
            }
        }
    }

    @AfterPermissionGranted(CAMERA_PERMISSIONS_REQUEST_CODE)
    override fun showCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).let { takePictureIntent ->
            takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
                val photoFile = try {
                    createImageFile()
                } catch (e: IOException) {
                    e.printStackTrace()
                    null
                }
                photoFile?.let { photo ->
                    val photoURI = FileProvider.getUriForFile(
                            requireContext(),
                            "com.furianrt.mydiary.fileprovider",
                            photo
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
                    mListener?.onGalleryListImagePickerOpen()
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
        return File.createTempFile(timeStamp, ".jpg").apply { mPhotoPath = absolutePath }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val clipData = data?.clipData
            val uris = mutableListOf<String>()
            if (clipData != null && clipData.itemCount > 0) {
                for (i in 0 until clipData.itemCount) {
                    clipData.getItemAt(i)?.uri?.let { uris.add(it.toString()) }
                }
            } else {
                data?.data?.let { uris.add(it.toString()) }
            }
            showLoading()
            presenter.onNoteImagesPicked(uris)
        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            showLoading()
            presenter.onNewPhotoTaken(mPhotoPath)
        }
    }

    override fun showLoading() {
        layout_loading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        layout_loading.visibility = View.GONE
    }

    override fun closeCab() {
        mActionMode?.finish()
    }

    private fun showImageCount(count: Int) {
        activity?.text_toolbar_title?.text = getString(R.string.fragment_gallery_list_image_count, count)
    }

    private fun showEmptyState() {
        if (empty_state.visibility == View.INVISIBLE) {
            anim_image_empty_list.playAnimation()
            empty_state.translationY = empty_state.height.toFloat()
            empty_state.visibility = View.VISIBLE
            empty_state.animate()
                    .translationY(0f)
                    .setDuration(ANIMATION_MOVE_DURATION)
                    .setInterpolator(DecelerateInterpolator())
                    .setListener(null)
        }
    }

    private fun hideEmptyState() {
        if (empty_state.visibility == View.VISIBLE) {
            anim_image_empty_list.cancelAnimation()
            empty_state.animate()
                    .translationY(empty_state.height.toFloat())
                    .setDuration(ANIMATION_MOVE_DURATION)
                    .setInterpolator(AccelerateInterpolator())
                    .setListener(object : Animator.AnimatorListener {
                        override fun onAnimationRepeat(animation: Animator?) {}
                        override fun onAnimationCancel(animation: Animator?) {}
                        override fun onAnimationStart(animation: Animator?) {}
                        override fun onAnimationEnd(animation: Animator?) {
                            empty_state?.visibility = View.INVISIBLE
                        }
                    })
        }
    }

    override fun showErrorSaveImage() {
        analytics.sendEvent(MyAnalytics.EVENT_IMAGE_SAVE_ERROR)
        Toast.makeText(requireContext(), R.string.fragment_gallery_list_image_save_error, Toast.LENGTH_SHORT).show()
    }

    private fun restoreTrash(animate: Boolean) {
        if (animate) {
            fab_trash?.animateScale(
                    FAB_HIGHLIGHTED_SIZE.toFloat() / FAB_DEFAULT_SIZE.toFloat(),
                    1f,
                    ANIMATION_SCALE_DURATION
            )
        } else {
            fab_trash?.customSize = FAB_HIGHLIGHTED_SIZE
            fab_trash?.requestLayout()
        }
    }

    private fun highlightTrash() {
        fab_trash?.animateScale(
                1f,
                FAB_HIGHLIGHTED_SIZE.toFloat() / FAB_DEFAULT_SIZE.toFloat(),
                ANIMATION_SCALE_DURATION
        )
    }

    private fun showTrash() {
        fab_trash?.show()
    }

    private fun hideTrash() {
        fab_trash?.hide()
    }

    interface OnGalleryListInteractionListener {
        fun onGalleryListImagePickerOpen()
    }
}