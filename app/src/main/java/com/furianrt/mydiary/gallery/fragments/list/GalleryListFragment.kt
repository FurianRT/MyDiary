package com.furianrt.mydiary.gallery.fragments.list

import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyImage
import com.furianrt.mydiary.gallery.fragments.pager.GalleryPagerFragment
import com.furianrt.mydiary.note.fragments.notefragment.inTransaction
import kotlinx.android.synthetic.main.fragment_gallery_list.view.*
import javax.inject.Inject


private const val ARG_NOTE_ID = "noteId"
private const val VERTICAL_LIST_SPAN_COUNT = 2
private const val HORIZONTAL_LIST_SPAN_COUNT = 3

class GalleryListFragment : Fragment(), GalleryListAdapter.OnListItemClickListener,
        GalleryListContract.View {

    @Inject
    lateinit var mPresenter: GalleryListContract.Presenter

    private lateinit var mAdapter: GalleryListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(context!!).inject(this)
        super.onCreate(savedInstanceState)

        val noteId = arguments?.getString(ARG_NOTE_ID) ?: throw IllegalStateException()
        mPresenter.setNoteId(noteId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_gallery_list, container, false)

        mPresenter.attachView(this)

        mPresenter.onViewCreate()

        view.apply {
            val orientation = context!!.resources.configuration.orientation
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                list_gallery.layoutManager = GridLayoutManager(context, HORIZONTAL_LIST_SPAN_COUNT)
            } else {
                list_gallery.layoutManager = GridLayoutManager(context, VERTICAL_LIST_SPAN_COUNT)
            }
            mAdapter = GalleryListAdapter(ArrayList(), this@GalleryListFragment, list_gallery)
            list_gallery.adapter = mAdapter
        }

        return view
    }

    override fun showImages(images: List<MyImage>) {
        val items = images.asSequence()
                .map { GalleryListAdapter.GalleryListItem(it.order.toLong(), it) }
                .toList()
        mAdapter.items = ArrayList(items)
        mAdapter.notifyDataSetChanged()
    }

    override fun onListItemClick(image: MyImage, position: Int) {
        mPresenter.onListItemClick(image, position)
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

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.detachView()
    }

    override fun onStop() {
        super.onStop()
        val images = mAdapter.items.asSequence()
                .map { it.image }
                .toList()
        mPresenter.onStop(images)
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
