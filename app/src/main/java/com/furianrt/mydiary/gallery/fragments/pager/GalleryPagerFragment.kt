package com.furianrt.mydiary.gallery.fragments.pager

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyImage
import com.furianrt.mydiary.gallery.fragments.list.GalleryListFragment
import com.furianrt.mydiary.utils.inTransaction
import kotlinx.android.synthetic.main.fragment_gallery_pager.view.*
import javax.inject.Inject

class GalleryPagerFragment : Fragment(), GalleryPagerContract.View {

    @Inject
    lateinit var mPresenter: GalleryPagerContract.Presenter

    private var mPagerPosition = 0
    private lateinit var mPagerAdapter: GalleryPagerAdapter

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

        setupUi(view)

        return view
    }

    private fun setupUi(view: View) {
        mPagerAdapter = GalleryPagerAdapter(ArrayList(), childFragmentManager)
        view.pager_gallery.adapter = mPagerAdapter
        view.pager_gallery.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {}
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}
            override fun onPageSelected(position: Int) {
                mPagerPosition = position
            }
        })
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

    override fun showImages(images: List<MyImage>) {
        mPagerAdapter.list = images
        mPagerAdapter.notifyDataSetChanged()
        view?.pager_gallery?.setCurrentItem(mPagerPosition, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.fragment_gallery_pager_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.menu_list_mode -> {
                mPresenter.onListModeButtonClick()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun showListImagesView(noteId: String) {
        fragmentManager?.inTransaction {
            replace(R.id.container_gallery, GalleryListFragment.newInstance(noteId), GalleryListFragment.TAG)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(ARG_POSITION, view!!.pager_gallery.currentItem)
    }

    companion object {

        const val TAG = "GalleryPagerFragment"
        private const val ARG_POSITION = "position"
        private const val ARG_NOTE_ID = "noteId"

        @JvmStatic
        fun newInstance(noteId: String, position: Int) =
                GalleryPagerFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_NOTE_ID, noteId)
                        putInt(ARG_POSITION, position)
                    }
                }
    }
}
