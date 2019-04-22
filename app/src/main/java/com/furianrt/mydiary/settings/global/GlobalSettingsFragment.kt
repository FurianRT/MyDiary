package com.furianrt.mydiary.settings.global

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.preference.SwitchPreference
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.prefs.PreferencesHelper
import com.furianrt.mydiary.pin.PinActivity
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
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            PreferencesHelper.COLOR_PRIMARY -> activity?.recreate()
            PreferencesHelper.COLOR_ACCENT -> activity?.recreate()
            PreferencesHelper.SECURITY_KEY -> mPresenter.onPrefSecurityKeyChanged()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CREATE_PIN) {
            if (resultCode == Activity.RESULT_OK) {
                mPresenter.onPasswordCreated()
            } else {
                val keyPref = findPreference(PreferencesHelper.SECURITY_KEY) as SwitchPreference
                keyPref.isChecked = false
            }
        } else if (requestCode == REQUEST_CODE_REMOVE_PIN) {
            if (resultCode == Activity.RESULT_OK) {
                mPresenter.onPasswordRemoved()
            } else {
                val keyPref = findPreference(PreferencesHelper.SECURITY_KEY) as SwitchPreference
                keyPref.isChecked = true            }
        }
    }

    override fun showBackupEmail(email: String) {
        val keyPref = findPreference(PreferencesHelper.SECURITY_KEY) as SwitchPreference
        keyPref.summaryOn = getString(R.string.global_settings_pin_on_summary, email)
    }

    override fun showCreatePasswordView() {
        val intent = Intent(activity!!, PinActivity::class.java)
        intent.putExtra(PinActivity.EXTRA_MODE, PinActivity.MODE_CREATE)
        startActivityForResult(intent, REQUEST_CODE_CREATE_PIN)
    }

    override fun showRemovePasswordView() {
        val intent = Intent(activity!!, PinActivity::class.java)
        intent.putExtra(PinActivity.EXTRA_MODE, PinActivity.MODE_REMOVE)
        startActivityForResult(intent, REQUEST_CODE_REMOVE_PIN)
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