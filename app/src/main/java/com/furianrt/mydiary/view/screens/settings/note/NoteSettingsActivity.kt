/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.screens.settings.note

import android.os.Bundle
import android.view.View
import com.anjlab.android.iab.v3.TransactionDetails
import com.furianrt.mydiary.BuildConfig
import com.furianrt.mydiary.R
import com.furianrt.mydiary.view.base.BaseActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.activity_note_settings.*

class NoteSettingsActivity : BaseActivity(R.layout.activity_note_settings),
        NoteSettingsFragment.OnNoteSettingsFragmentListener {

    companion object {
        const val EXTRA_NOTE_ID = "noteId"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val noteId = intent.getStringExtra(EXTRA_NOTE_ID) ?: throw IllegalArgumentException()
        addSettingsFragment(noteId)

        setSupportActionBar(toolbar_settings_note)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        view_input_lock.setOnTouchListener { _, _ -> true }
    }

    private fun addSettingsFragment(noteId: String) {
        if (supportFragmentManager.findFragmentByTag(NoteSettingsFragment.TAG) == null) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.container_settings_note, NoteSettingsFragment.newInstance(noteId),
                            NoteSettingsFragment.TAG)
                    .commit()
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

    override fun enableInput() {
        view_input_lock.visibility = View.GONE
    }

    override fun disableInput() {
        view_input_lock.visibility = View.VISIBLE
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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}