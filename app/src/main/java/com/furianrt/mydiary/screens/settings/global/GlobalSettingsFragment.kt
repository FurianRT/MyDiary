package com.furianrt.mydiary.screens.settings.global

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.preference.SwitchPreference
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.prefs.PreferencesHelper
import com.furianrt.mydiary.screens.pin.PinActivity
import javax.inject.Inject

class GlobalSettingsFragment : PreferenceFragment(), GlobalSettingsContract.View,
        SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        private const val REQUEST_CODE_CREATE_PIN = 1
        private const val REQUEST_CODE_REMOVE_PIN = 2
    }

    @Inject
    lateinit var mPresenter: GlobalSettingsContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(activity!!).inject(this)
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_global)
        PreferenceManager.setDefaultValues(activity, R.xml.pref_global, false)
        mPresenter.attachView(this)
        mPresenter.onViewCreate()

        findPreference(PreferencesHelper.SECURITY_KEY).setOnPreferenceClickListener {
            mPresenter.onPrefSecurityKeyClick()
            return@setOnPreferenceClickListener true
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            PreferencesHelper.COLOR_PRIMARY -> activity?.recreate()
            PreferencesHelper.COLOR_ACCENT -> activity?.recreate()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CREATE_PIN) {
            val keyPref = findPreference(PreferencesHelper.SECURITY_KEY) as SwitchPreference
            if (resultCode == Activity.RESULT_OK) {
                keyPref.isChecked = true
                mPresenter.onPasswordCreated()
            } else {
                keyPref.isChecked = false
            }
        } else if (requestCode == REQUEST_CODE_REMOVE_PIN) {
            val keyPref = findPreference(PreferencesHelper.SECURITY_KEY) as SwitchPreference
            if (resultCode == Activity.RESULT_OK) {
                keyPref.isChecked = false
                mPresenter.onPasswordRemoved()
            } else {
                keyPref.isChecked = true            }
        }
    }

    override fun showBackupEmail(email: String) {
        val keyPref = findPreference(PreferencesHelper.SECURITY_KEY) as SwitchPreference
        keyPref.summaryOn = getString(R.string.global_settings_pin_on_summary, email)
    }

    override fun showCreatePasswordView() {
        (findPreference(PreferencesHelper.SECURITY_KEY) as SwitchPreference).isChecked = false
        startActivityForResult(PinActivity.newIntentModeCreate(activity!!), REQUEST_CODE_CREATE_PIN)
    }

    override fun showRemovePasswordView() {
        (findPreference(PreferencesHelper.SECURITY_KEY) as SwitchPreference).isChecked = true
        startActivityForResult(PinActivity.newIntentModeRemove(activity!!), REQUEST_CODE_REMOVE_PIN)
    }

    override fun onStart() {
        super.onStart()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onStop() {
        super.onStop()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.detachView()
    }
}