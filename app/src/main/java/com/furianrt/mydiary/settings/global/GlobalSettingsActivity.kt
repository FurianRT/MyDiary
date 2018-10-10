package com.furianrt.mydiary.settings.global

import android.os.Bundle
import android.preference.PreferenceManager
import com.furianrt.mydiary.BaseActivity
import com.furianrt.mydiary.R
import kotlinx.android.synthetic.main.activity_global_settings.*

class GlobalSettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_global_settings)

        setSupportActionBar(toolbar_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        PreferenceManager.setDefaultValues(this, R.xml.pref_global, false)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
