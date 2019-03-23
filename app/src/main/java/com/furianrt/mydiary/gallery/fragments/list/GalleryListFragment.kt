package com.furianrt.mydiary.gallery.fragments.list

import android.Manifest
import android.animation.Animator
import android.content.res.Configuration
import android.os.Bundle
import android.os.Parcelable
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyImage
import com.furianrt.mydiary.gallery.fragments.pager.GalleryPagerFragment
import com.furianrt.mydiary.utils.*
import com.yanzhenjie.album.Album
import com.yanzhenjie.album.api.widget.Widget
import jp.wasabeef.recyclerview.animators.FadeInUpAnimator
import kotlinx.android.synthetic.main.activity_gallery.*
import kotlinx.android.synthetic.main.fragment_gallery_list.*
import kotlinx.android.synthetic.main.fragment_gallery_list.view.*
import kotlinx.android.synthetic.main.gallery_list_empty_state.view.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

class GalleryListFragment : Fragment(), GalleryListAdapter.OnListItemInteractionListener,
        GalleryListContract.View, ActionMode.Callback {

    companion object {
        const val TAG = "GalleryListFragment"
        private const val ARG_NOTE_ID = "noteId"
        private const val VERTICAL_LIST_SPAN_COUNT = 2
        private const val HORIZONTAL_LIST_SPAN_COUNT = 3
        private const val IMAGE_SPAN = 1
        private const val BUNDLE_SELECTION_ACTIVE = "selectionActive"
        private const val BUNDLE_SELECTED_IMAGES = "selectedImages"
        private const val BUNDLE_RECYCLER_VIEW_STATE = "recyclerState"
        private const val STORAGE_PERMISSIONS_REQUEST_CODE = 1
        private val FAB_DEFAULT_SIZE = dpToPx(46f)
        private val FAB_HIGHLIGHTED_SIZE = dpToPx(60f)
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
    lateinit var mPresenter: GalleryListContract.Presenter

    private lateinit var mAdapter: GalleryListAdapter
    private var mSelectionActive = false
    private var mRecyclerViewState: Parcelable? = null
    private var mActionMode: ActionMode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(context!!).inject(this)
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        val noteId = arguments?.getString(ARG_NOTE_ID) ?: throw IllegalStateException()
        mPresenter.setNoteId(noteId)

        savedInstanceState?.let {
            mRecyclerViewState = it.getParcelable(BUNDLE_RECYCLER_VIEW_STATE)
            mSelectionActive = it.getBoolean(BUNDLE_SELECTION_ACTIVE, false)
            mPresenter.onRestoreInstanceState(it.getParcelableArrayList(BUNDLE_SELECTED_IMAGES))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_gallery_list, container, false)

        mAdapter = GalleryListAdapter(this@GalleryListFragment, view.list_gallery)
        val orientation = context!!.resources.configuration.orientation

        view.list_gallery.layoutManager = if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            GridLayoutManager(context, HORIZONTAL_LIST_SPAN_COUNT).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int =
                            when (mAdapter.getItemViewType(position)) {
                                GalleryListAdapter.ViewItem.TYPE_FOOTER -> HORIZONTAL_LIST_SPAN_COUNT
                                else -> IMAGE_SPAN
                            }
                }
            }
        } else {
            GridLayoutManager(context, VERTICAL_LIST_SPAN_COUNT).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int =
                            when (mAdapter.getItemViewType(position)) {
                                GalleryListAdapter.ViewItem.TYPE_FOOTER -> VERTICAL_LIST_SPAN_COUNT
                                else -> IMAGE_SPAN
                            }
                }
            }
        }
        view.list_gallery.adapter = mAdapter
        view.list_gallery.itemAnimator = FadeInUpAnimator()

        view.fab_trash.drawable.mutate().setTint(ContextCompat.getColor(context!!, R.color.white))

        if (mSelectionActive) {
            mActionMode = (activity as AppCompatActivity).startSupportActionMode(this)
        }

        view.button_add_images.setOnClickListener { mPresenter.onAddImageButtonClick() }

        return view
    }

    override fun onResume() {
        super.onResume()
        mPresenter.attachView(this)
        mPresenter.onViewStart()
    }

    override fun onPause() {
        super.onPause()
        mPresenter.detachView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(BUNDLE_RECYCLER_VIEW_STATE,
                list_gallery.layoutManager?.onSaveInstanceState())
        outState.putParcelableArrayList(BUNDLE_SELECTED_IMAGES,
                ArrayList(mPresenter.onSaveInstanceState()))
        outState.putBoolean(BUNDLE_SELECTION_ACTIVE, mSelectionActive)
    }

    override fun showImages(images: List<MyImage>, selectedImages: List<MyImage>) {
        hideEmptyState()
        showImageCount(images.size)
        mRecyclerViewState?.let {
            list_gallery.layoutManager?.onRestoreInstanceState(mRecyclerViewState)
            mRecyclerViewState = null
        }
        mAdapter.selectedImages = selectedImages.toMutableList()
        mAdapter.submitList(images)
    }

    override fun showEmptyList() {
        showEmptyState()
        showImageCount(0)
        mAdapter.selectedImages = mutableListOf()
        mAdapter.submitList(emptyList())
    }

    override fun selectImages(images: MutableList<MyImage>) {
        mAdapter.selectedImages.addAll(images)
        val listImages = mAdapter.getImages()
        for (image in images) {
            mAdapter.notifyItemChanged(listImages.indexOfFirst { it.name == image.name })
        }
    }

    override fun showSelectedImageCount(count: Int) {
        mActionMode?.title = getString(R.string.fragment_gallery_list_selected) + ": $count"
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.fragment_gallery_list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.menu_multi_select -> {
                mPresenter.onMultiSelectionButtonClick()
                true
            }
            R.id.menu_add_image -> {
                mPresenter.onAddImageButtonClick()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        activity?.menuInflater?.inflate(R.menu.fragment_gallery_list_cab_menu, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.menu_delete -> {
                mPresenter.onCabDeleteButtonClick()
                true
            }

            R.id.menu_select_all -> {
                mPresenter.onCabSelectAllButtonClick()
                true
            }
            else -> false
        }
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        mPresenter.onCabCloseSelection()
    }

    override fun activateSelection() {
        mSelectionActive = true
        mActionMode = (activity as AppCompatActivity).startSupportActionMode(this)
    }

    override fun deactivateSelection() {
        mSelectionActive = false
        mAdapter.deactivateSelection()
    }

    override fun selectImage(image: MyImage) {
        mAdapter.selectedImages.add(image)
        mAdapter.notifyItemChanged(mAdapter.getImages().indexOf(image))
    }

    override fun deselectImage(image: MyImage) {
        mAdapter.selectedImages.remove(image)
        mAdapter.notifyItemChanged(mAdapter.getImages().indexOf(image))
    }

    override fun onListItemClick(image: MyImage, position: Int) {
        mPresenter.onListItemClick(image, position, mSelectionActive)
    }

    override fun onImagesOrderChange(images: List<MyImage>) {
        mPresenter.onImagesOrderChange(mAdapter.getImages())
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
        //todo добавить подтверждение удаления
        hideTrash()
        mPresenter.onImageDeleted(image)
    }

    override fun showViewImagePager(noteId: String, position: Int) {
        if (fragmentManager?.findFragmentByTag(GalleryPagerFragment.TAG) == null) {
            fragmentManager?.inTransaction {
                replace(R.id.container_gallery,
                        GalleryPagerFragment.newInstance(noteId, position), GalleryPagerFragment.TAG)
            }
        }
    }

    override fun requestStoragePermissions() {
        val readExtStorage = Manifest.permission.READ_EXTERNAL_STORAGE
        val camera = Manifest.permission.CAMERA
        if (EasyPermissions.hasPermissions(context!!, readExtStorage, camera)) {
            mPresenter.onStoragePermissionsGranted()
        } else {
            EasyPermissions.requestPermissions(this,
                    getString(R.string.storage_permission_request),
                    STORAGE_PERMISSIONS_REQUEST_CODE, readExtStorage, camera)
        }
    }

    @AfterPermissionGranted(STORAGE_PERMISSIONS_REQUEST_CODE)
    override fun showImageExplorer() {
        val widget = Widget.newDarkBuilder(context)
                .statusBarColor(getThemePrimaryDarkColor(context!!))
                .toolBarColor(getThemePrimaryColor(context!!))
                .navigationBarColor(ContextCompat.getColor(context!!, R.color.black))
                .title(R.string.album)
                .build()

        Album.image(this)
                .multipleChoice()
                .columnCount(3)
                .filterMimeType {
                    when (it) {
                        "jpeg" -> true
                        "jpg " -> true
                        else -> false
                    }
                }
                .afterFilterVisibility(false)
                .camera(true)
                .widget(widget)
                .onResult {
                    showLoading()
                    mPresenter.onNoteImagesPicked(it.map { image -> image.path })
                }
                .start()
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
        val textCount = getString(R.string.fragment_gallery_list_image_count) + ": $count"
        activity?.text_toolbar_title?.text = textCount
    }

    private fun showEmptyState() {
        if (empty_state.visibility == View.INVISIBLE) {
            empty_state.translationY = empty_state.height + 100f
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
            empty_state.animate()
                    .translationY(empty_state.height + 100f)
                    .setDuration(ANIMATION_MOVE_DURATION)
                    .setInterpolator(AccelerateInterpolator())
                    .setListener(object : Animator.AnimatorListener {
                        override fun onAnimationRepeat(animation: Animator?) {}
                        override fun onAnimationCancel(animation: Animator?) {}
                        override fun onAnimationStart(animation: Animator?) {}
                        override fun onAnimationEnd(animation: Animator?) {
                            empty_state.visibility = View.INVISIBLE
                        }
                    })
        }
    }

    private fun restoreTrash(animate: Boolean) {
        if (animate) {
            fab_trash.animateScale(
                    FAB_HIGHLIGHTED_SIZE.toFloat() / FAB_DEFAULT_SIZE.toFloat(),
                    1f,
                    ANIMATION_SCALE_DURATION
            )
        } else {
            fab_trash.customSize = FAB_HIGHLIGHTED_SIZE
            fab_trash.requestLayout()
        }
    }

    private fun highlightTrash() {
        fab_trash.animateScale(
                1f,
                FAB_HIGHLIGHTED_SIZE.toFloat() / FAB_DEFAULT_SIZE.toFloat(),
                ANIMATION_SCALE_DURATION
        )
    }

    private fun showTrash() {
        fab_trash.show()
    }

    private fun hideTrash() {
        fab_trash.hide()
    }
}
