package com.furianrt.mydiary.gallery

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import com.furianrt.mydiary.R
import com.furianrt.mydiary.gallery.fragments.list.GalleryListFragment
import com.furianrt.mydiary.gallery.fragments.pager.GalleryPagerFragment
import com.furianrt.mydiary.note.fragments.notefragment.inTransaction
import kotlinx.android.synthetic.main.activity_gallery.*
import javax.inject.Inject

const val EXTRA_POSITION = "position"
const val EXTRA_NOTE_ID = "noteId"

class GalleryActivity : AppCompatActivity(), GalleryActivityContract.View {

    @Inject
    lateinit var mPresenter: GalleryActivityContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getPresenterComponent(this).inject(this)
        setContentView(R.layout.activity_gallery)

        mPresenter.attachView(this)

        setupUi()
    }

    private fun setupUi() {
        setSupportActionBar(toolbar_gallery)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val position = intent.getIntExtra(EXTRA_POSITION, 0)
        val noteId = intent.getStringExtra(EXTRA_NOTE_ID)
        val pagerTag = GalleryPagerFragment::class.toString()
        val listTag = GalleryListFragment::class.toString()
        if (supportFragmentManager.findFragmentByTag(pagerTag) == null
                && supportFragmentManager.findFragmentByTag(listTag) == null) {

            supportFragmentManager.inTransaction {
                add(R.id.container_gallery, GalleryPagerFragment.newInstance(noteId, position), pagerTag)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_gallery_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.detachView()
    }
}
