package com.furianrt.mydiary.gallery.fragments.list

import android.content.res.Configuration
import android.os.Bundle
import android.os.Parcelable
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.recyclerview.widget.GridLayoutManager
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyImage
import com.furianrt.mydiary.gallery.fragments.pager.GalleryPagerFragment
import com.furianrt.mydiary.note.fragments.notefragment.inTransaction
import jp.wasabeef.recyclerview.animators.FadeInUpAnimator
import kotlinx.android.synthetic.main.fragment_gallery_list.view.*
import javax.inject.Inject

private const val ARG_NOTE_ID = "noteId"
private const val VERTICAL_LIST_SPAN_COUNT = 2
private const val HORIZONTAL_LIST_SPAN_COUNT = 3
private const val BUNDLE_SELECTION_ACTIVE = "selectionActive"
private const val BUNDLE_SELECTED_IMAGES = "selectedImages"
private const val BUNDLE_RECYCLER_VIEW_STATE = "recyclerState"

class GalleryListFragment : androidx.fragment.app.Fragment(), GalleryListAdapter.OnListItemClickListener,
        GalleryListContract.View, ActionMode.Callback {

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

        view.apply {
            val orientation = context!!.resources.configuration.orientation
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                list_gallery.layoutManager = GridLayoutManager(context, HORIZONTAL_LIST_SPAN_COUNT)
            } else {
                list_gallery.layoutManager = GridLayoutManager(context, VERTICAL_LIST_SPAN_COUNT)
            }
            mAdapter = GalleryListAdapter(this@GalleryListFragment, list_gallery)
            list_gallery.adapter = mAdapter
            list_gallery.itemAnimator = FadeInUpAnimator()
        }

        if (mSelectionActive) {
            mActionMode = (activity as AppCompatActivity).startSupportActionMode(this)
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        mPresenter.attachView(this)
        mPresenter.onViewStart()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(BUNDLE_RECYCLER_VIEW_STATE,
                view?.list_gallery?.layoutManager?.onSaveInstanceState())
        outState.putParcelableArrayList(BUNDLE_SELECTED_IMAGES,
                ArrayList(mPresenter.onSaveInstanceState()))
        outState.putBoolean(BUNDLE_SELECTION_ACTIVE, mSelectionActive)
    }

    override fun showImages(images: List<MyImage>, selectedImages: List<MyImage>) {
        mRecyclerViewState?.let {
            view?.list_gallery?.layoutManager?.onRestoreInstanceState(mRecyclerViewState)
            mRecyclerViewState = null
        }

        if (mAdapter.getImages().isEmpty()) {
            mAdapter.selectedImages = selectedImages.toMutableList()
        } else {
            mAdapter.selectedImages.clear()
        }

        mAdapter.submitList(images)
    }

    override fun selectItems(items: MutableList<MyImage>) {
        mAdapter.selectedImages.addAll(items)
        for (item in items) {
            mAdapter.notifyItemChanged(mAdapter.getImages().indexOf(item))
        }
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

    override fun selectItem(image: MyImage) {
        mAdapter.selectedImages.add(image)
        mAdapter.notifyItemChanged(mAdapter.getImages().indexOf(image))
    }

    override fun deselectItem(image: MyImage) {
        mAdapter.selectedImages.remove(image)
        mAdapter.notifyItemChanged(mAdapter.getImages().indexOf(image))
    }

    override fun onListItemClick(image: MyImage, position: Int) {
        mPresenter.onListItemClick(image, position, mSelectionActive)
    }

    override fun onImagesOrderChange(images: List<MyImage>) {
        mPresenter.onImagesOrderChange(mAdapter.getImages())
    }

    override fun showViewImagePager(noteId: String, position: Int) {
        val tag = GalleryPagerFragment::class.toString()
        if (fragmentManager?.findFragmentByTag(tag) == null) {
            fragmentManager?.inTransaction {
                replace(R.id.container_gallery,
                        GalleryPagerFragment.newInstance(noteId, position), tag)
            }
        }
    }

    override fun closeCab() {
        mActionMode?.finish()
    }

    override fun onStop() {
        super.onStop()
        mPresenter.detachView()
    }

    companion object {
        @JvmStatic
        fun newInstance(noteId: String) =
                GalleryListFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_NOTE_ID, noteId)
                    }
                }
    }
}
