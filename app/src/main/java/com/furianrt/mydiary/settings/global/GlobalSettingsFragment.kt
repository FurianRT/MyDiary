package com.furianrt.mydiary.settings.global

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.prefs.PreferencesHelper

class GlobalSettingsFragment : PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_global)

        PreferenceManager.setDefaultValues(activity, R.xml.pref_global, false)
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            PreferencesHelper.COLOR_PRIMARY -> activity?.recreate()
            PreferencesHelper.COLOR_ACCENT -> activity?.recreate()
        }
    }
}