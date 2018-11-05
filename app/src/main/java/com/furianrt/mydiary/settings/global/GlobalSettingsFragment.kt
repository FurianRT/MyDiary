package com.furianrt.mydiary.settings.global

import android.os.Bundle
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import com.furianrt.mydiary.R

class GlobalSettingsFragment : PreferenceFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_global)

        PreferenceManager.setDefaultValues(activity, R.xml.pref_global, false)
    }
}