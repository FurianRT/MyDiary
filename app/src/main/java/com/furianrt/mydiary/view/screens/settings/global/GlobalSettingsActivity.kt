/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.screens.settings.global

import android.os.Bundle
import android.view.View
import com.anjlab.android.iab.v3.TransactionDetails
import com.furianrt.mydiary.BuildConfig
import com.furianrt.mydiary.R
import com.furianrt.mydiary.view.base.BaseActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.activity_global_settings.*

class GlobalSettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_global_settings)

        setSupportActionBar(toolbar_settings_global)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onBillingInitialized() {
        super.onBillingInitialized()
        if (!isItemPurchased(BuildConfig.ITEM_PREMIUM_SKU)/* && !isItemPurchased(ITEM_TEST_SKU)*/) {
            showAdView()
        }
    }

    override fun onProductPurchased(productId: String, details: TransactionDetails?) {
        super.onProductPurchased(productId, details)
        if (productId == BuildConfig.ITEM_PREMIUM_SKU/* || productId == ITEM_TEST_SKU*/) {
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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
