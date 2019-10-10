/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.screens.gallery

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.anjlab.android.iab.v3.TransactionDetails
import com.furianrt.mydiary.BuildConfig
import com.furianrt.mydiary.R
import com.furianrt.mydiary.view.base.BaseActivity
import com.furianrt.mydiary.view.screens.gallery.fragments.list.GalleryListFragment
import com.furianrt.mydiary.view.screens.gallery.fragments.pager.GalleryPagerFragment
import com.furianrt.mydiary.utils.inTransaction
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.activity_gallery.*
import javax.inject.Inject

class GalleryActivity : BaseActivity(R.layout.activity_gallery), GalleryActivityContract.MvpView {

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
    lateinit var mPresenter: GalleryActivityContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(this).inject(this)
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar_gallery)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val position = intent.getIntExtra(EXTRA_POSITION, 0)
        val noteId = intent.getStringExtra(EXTRA_NOTE_ID) ?: throw IllegalArgumentException()
        if (supportFragmentManager.findFragmentByTag(GalleryPagerFragment.TAG) == null
                && supportFragmentManager.findFragmentByTag(GalleryListFragment.TAG) == null) {

            supportFragmentManager.inTransaction {
                add(R.id.container_gallery, GalleryPagerFragment.newInstance(noteId, position),
                        GalleryPagerFragment.TAG)
            }
        }
    }

    override fun onBillingInitialized() {
        super.onBillingInitialized()
        if (!isItemPurchased(BuildConfig.ITEM_PREMIUM_SKU)) {
            showAdView()
        }
    }

    override fun onProductPurchased(productId: String, details: TransactionDetails?) {
        super.onProductPurchased(productId, details)
        if (productId == BuildConfig.ITEM_PREMIUM_SKU) {
            hideAdView()
        }
    }

    private fun showAdView() {
        view_ad?.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                view_ad?.visibility = View.VISIBLE
            }
        }
        view_ad?.loadAd(AdRequest.Builder().build())
    }

    private fun hideAdView() {
        view_ad?.destroy()
        view_ad?.visibility = View.GONE
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
        finish()
        return true
    }
}
