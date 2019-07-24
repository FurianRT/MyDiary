package com.furianrt.mydiary.view.screens.settings.global

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreference
import com.furianrt.mydiary.R
import com.furianrt.mydiary.analytics.MyAnalytics
import com.furianrt.mydiary.view.base.BaseView
import com.furianrt.mydiary.data.prefs.PreferencesHelper
import com.furianrt.mydiary.view.screens.pin.PinActivity
import com.furianrt.mydiary.utils.isFingerprintAvailable
import javax.inject.Inject

class GlobalSettingsFragment : PreferenceFragmentCompat(), BaseView, GlobalSettingsContract.MvpView,
        SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        private const val REQUEST_CODE_CREATE_PIN = 1
        private const val REQUEST_CODE_REMOVE_PIN = 2
        private const val RECREATE_DELAY = 100L
    }

    @Inject
    lateinit var mPresenter: GlobalSettingsContract.Presenter

    @Inject
    lateinit var mAnalytics : MyAnalytics

    private val mHandler = Handler()
    private val mRecreateRunnable = Runnable { activity?.recreate() }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_global, rootKey)
        PreferenceManager.setDefaultValues(activity, R.xml.pref_global, false)
        mPresenter.attachView(this)
        mPresenter.onViewCreate()

        findPreference<SwitchPreference>(PreferencesHelper.FINGERPRINT_STATUS)?.isVisible =
                requireContext().isFingerprintAvailable()

        findPreference<SwitchPreference>(PreferencesHelper.SECURITY_KEY)?.onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    mPresenter.onPrefSecurityKeyClick()
                    return@OnPreferenceClickListener true
                }

        findPreference<Preference>(PreferencesHelper.RATE_APP_PREF_BUTTON)?.onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    mPresenter.onPrefRateAppClick()
                    return@OnPreferenceClickListener true
                }

        findPreference<Preference>(PreferencesHelper.REPORT_PROBLEM_PREF_BUTTON)?.onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    mPresenter.onPrefReportProblemClick()
                    return@OnPreferenceClickListener true
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireActivity()).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            PreferencesHelper.COLOR_PRIMARY -> {
                mAnalytics.sendEvent(MyAnalytics.EVENT_PRIMARY_COLOR_CHANGED)
                mHandler.postDelayed(mRecreateRunnable, RECREATE_DELAY)
            }
            PreferencesHelper.COLOR_ACCENT -> {
                mAnalytics.sendEvent(MyAnalytics.EVENT_ACCENT_COLOR_CHANGED)
                mHandler.postDelayed(mRecreateRunnable, RECREATE_DELAY)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CREATE_PIN) {
            val keyPref = findPreference<SwitchPreference>(PreferencesHelper.SECURITY_KEY)
            if (resultCode == Activity.RESULT_OK) {
                mAnalytics.sendEvent(MyAnalytics.EVENT_PIN_CREATED)
                keyPref?.isChecked = true
                mPresenter.onPasswordCreated()
            } else {
                keyPref?.isChecked = false
            }
        } else if (requestCode == REQUEST_CODE_REMOVE_PIN) {
            val keyPref = findPreference<SwitchPreference>(PreferencesHelper.SECURITY_KEY)
            if (resultCode == Activity.RESULT_OK) {
                mAnalytics.sendEvent(MyAnalytics.EVENT_PIN_REMOVED)
                keyPref?.isChecked = false
                mPresenter.onPasswordRemoved()
            } else {
                keyPref?.isChecked = true
            }
        }
    }

    override fun showBackupEmail(email: String) {
        val keyPref = findPreference<SwitchPreference>(PreferencesHelper.SECURITY_KEY)
        keyPref?.summaryOn = getString(R.string.global_settings_pin_on_summary, email)
    }

    override fun showCreatePasswordView() {
        findPreference<SwitchPreference>(PreferencesHelper.SECURITY_KEY)?.isChecked = false
        startActivityForResult(PinActivity.newIntentModeCreate(requireActivity()), REQUEST_CODE_CREATE_PIN)
    }

    override fun showRemovePasswordView() {
        findPreference<SwitchPreference>(PreferencesHelper.SECURITY_KEY)?.isChecked = true
        startActivityForResult(PinActivity.newIntentModeRemove(requireActivity()), REQUEST_CODE_REMOVE_PIN)
    }

    override fun openAppPage() {
        val playMarketPage = "market://details?id=${requireContext().packageName}"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(playMarketPage)
        startActivity(intent)
    }

    override fun sendEmailToSupport(supportEmail: String) {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$supportEmail"))
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.low_rate_email_subject))
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.low_rate_email_text))
        startActivity(Intent.createChooser(intent, getString(R.string.low_rate_email_title)))
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