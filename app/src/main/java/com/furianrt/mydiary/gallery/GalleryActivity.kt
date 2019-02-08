package com.furianrt.mydiary.gallery

import android.os.Bundle
import com.furianrt.mydiary.BaseActivity
import com.furianrt.mydiary.R
import com.furianrt.mydiary.gallery.fragments.list.GalleryListFragment
import com.furianrt.mydiary.gallery.fragments.pager.GalleryPagerFragment
import com.furianrt.mydiary.note.fragments.notefragment.inTransaction
import kotlinx.android.synthetic.main.activity_gallery.*
import javax.inject.Inject

class GalleryActivity : BaseActivity(), GalleryActivityContract.View {

    companion object {
        const val EXTRA_POSITION = "position"
        const val EXTRA_NOTE_ID = "noteId"
    }

    @Inject
    lateinit var mPresenter: GalleryActivityContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getPresenterComponent(this).inject(this)
        setContentView(R.layout.activity_gallery)

        setupUi()
    }

    private fun setupUi() {
        setSupportActionBar(toolbar_gallery)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val position = intent.getIntExtra(EXTRA_POSITION, 0)
        val noteId = intent.getStringExtra(EXTRA_NOTE_ID)
        if (supportFragmentManager.findFragmentByTag(GalleryPagerFragment.TAG) == null
                && supportFragmentManager.findFragmentByTag(GalleryListFragment.TAG) == null) {

            supportFragmentManager.inTransaction {
                add(R.id.container_gallery, GalleryPagerFragment.newInstance(noteId, position),
                        GalleryPagerFragment.TAG)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mPresenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        mPresenter.detachView()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
