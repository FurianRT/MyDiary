/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.gallery

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.furianrt.mydiary.R
import com.furianrt.mydiary.presentation.base.BaseActivity
import com.furianrt.mydiary.presentation.screens.gallery.fragments.list.GalleryListFragment
import com.furianrt.mydiary.presentation.screens.gallery.fragments.pager.GalleryPagerFragment
import com.furianrt.mydiary.utils.inTransaction
import kotlinx.android.synthetic.main.activity_gallery.*
import javax.inject.Inject

class GalleryActivity : BaseActivity(R.layout.activity_gallery), GalleryActivityContract.View,
        GalleryListFragment.OnGalleryListInteractionListener {

    companion object {
        private const val EXTRA_POSITION = "position"
        private const val EXTRA_NOTE_ID = "noteId"

        fun newIntent(context: Context, noteId: String, position: Int) =
                Intent(context, GalleryActivity::class.java).apply {
                    putExtra(EXTRA_NOTE_ID, noteId)
                    putExtra(EXTRA_POSITION, position)
                }
    }

    @Inject
    lateinit var presenter: GalleryActivityContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(this).inject(this)
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar_gallery)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val position = intent.getIntExtra(EXTRA_POSITION, 0)
        val noteId = intent.getStringExtra(EXTRA_NOTE_ID)!!
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
        presenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        presenter.detachView()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onGalleryListImagePickerOpen() {
        skipOneLock = true
    }
}
