/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.settings.global

import android.app.Activity
import android.os.Bundle
import com.furianrt.mydiary.R
import com.furianrt.mydiary.presentation.base.BaseActivity
import com.furianrt.mydiary.utils.inTransaction
import kotlinx.android.synthetic.main.activity_global_settings.*

class GlobalSettingsActivity : BaseActivity(R.layout.activity_global_settings),
        GlobalSettingsFragment.GlobalSettingsFragmentListener {

    companion object {
        private const val BUNDLE_THEME_CHANGED = "theme_changed"
    }

    private var mThemeChanged = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar_settings_global)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        addSettingsFragment()
        savedInstanceState?.let { mThemeChanged = it.getBoolean(BUNDLE_THEME_CHANGED, false) }
    }

    private fun addSettingsFragment() {
        if (supportFragmentManager.findFragmentByTag(GlobalSettingsFragment.TAG) == null) {
            supportFragmentManager.inTransaction {
                add(R.id.container_settings_global, GlobalSettingsFragment(), GlobalSettingsFragment.TAG)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(BUNDLE_THEME_CHANGED, mThemeChanged)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        if (mThemeChanged) {
            setResult(Activity.RESULT_OK)
            finish()
        } else {
            super.onBackPressed()
        }
    }

    override fun onThemeAttributeChanged() {
        mThemeChanged = true
    }
}
