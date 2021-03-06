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
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AlertDialog
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreference
import com.furianrt.mydiary.R
import com.furianrt.mydiary.analytics.MyAnalytics
import com.furianrt.mydiary.model.source.preferences.PreferencesSource
import com.furianrt.mydiary.presentation.base.BasePreference
import com.furianrt.mydiary.presentation.screens.pin.PinActivity
import javax.inject.Inject

class GlobalSettingsFragment : BasePreference(), GlobalSettingsContract.View,
        SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        const val TAG = "GlobalSettingsFragment"
        private const val REQUEST_CODE_CREATE_PIN = 1
        private const val REQUEST_CODE_REMOVE_PIN = 2
        private const val RECREATE_DELAY = 300L
    }

    @Inject
    lateinit var presenter: GlobalSettingsContract.Presenter

    private val mHandler = Handler()
    private val mRecreateRunnable = Runnable { activity?.recreate() }
    private var mListener: GlobalSettingsFragmentListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireActivity()).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_global, rootKey)
        PreferenceManager.setDefaultValues(activity, R.xml.pref_global, false)

        presenter.attachView(this)

        findPreference<SwitchPreference>(PreferencesSource.SECURITY_KEY)?.onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    presenter.onPrefSecurityKeyClick()
                    return@OnPreferenceClickListener true
                }

        findPreference<Preference>(PreferencesSource.RATE_APP_PREF_BUTTON)?.onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    presenter.onPrefRateAppClick()
                    return@OnPreferenceClickListener true
                }

        findPreference<Preference>(PreferencesSource.REPORT_PROBLEM_PREF_BUTTON)?.onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    presenter.onPrefReportProblemClick()
                    return@OnPreferenceClickListener true
                }

        findPreference<Preference>(PreferencesSource.RESET_NOTES_APPEARANCE_SETTINGS)?.onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    showResetNotesAppearanceDialog()
                    return@OnPreferenceClickListener true
                }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            PreferencesSource.COLOR_PRIMARY -> {
                analytics.sendEvent(MyAnalytics.EVENT_PRIMARY_COLOR_CHANGED)
                mHandler.postDelayed(mRecreateRunnable, RECREATE_DELAY)
                mListener?.onThemeAttributeChanged()
            }
            PreferencesSource.COLOR_ACCENT -> {
                analytics.sendEvent(MyAnalytics.EVENT_ACCENT_COLOR_CHANGED)
                mHandler.postDelayed(mRecreateRunnable, RECREATE_DELAY)
                mListener?.onThemeAttributeChanged()
            }
            PreferencesSource.APP_FONT_STYLE -> {
                analytics.sendEvent(MyAnalytics.EVENT_FONT_STYLE_CHANGED)
                mHandler.postDelayed(mRecreateRunnable, RECREATE_DELAY)
                mListener?.onThemeAttributeChanged()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CREATE_PIN) {
            val keyPref = findPreference<SwitchPreference>(PreferencesSource.SECURITY_KEY)
            if (resultCode == Activity.RESULT_OK) {
                analytics.sendEvent(MyAnalytics.EVENT_PIN_CREATED)
                keyPref?.isChecked = true
                presenter.onPasswordCreated()
            } else {
                keyPref?.isChecked = false
            }
        } else if (requestCode == REQUEST_CODE_REMOVE_PIN) {
            val keyPref = findPreference<SwitchPreference>(PreferencesSource.SECURITY_KEY)
            if (resultCode == Activity.RESULT_OK) {
                analytics.sendEvent(MyAnalytics.EVENT_PIN_REMOVED)
                keyPref?.isChecked = false
                presenter.onPasswordRemoved()
            } else {
                keyPref?.isChecked = true
            }
        }
    }

    private fun showResetNotesAppearanceDialog() {
        AlertDialog.Builder(requireContext())
                .setMessage(getString(R.string.fragment_global_settings_reset_appearance_confirmation))
                .setPositiveButton(R.string.reset) { dialogInterface, _ ->
                    presenter.onPrefResetNotesColorClick()
                    dialogInterface.dismiss()
                }
                .setNegativeButton(R.string.cancel) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .show()
    }

    override fun showFingerprintOptions() {
        findPreference<SwitchPreference>(PreferencesSource.FINGERPRINT_STATUS)?.isVisible = true
    }

    override fun hideFingerprintOptions() {
        findPreference<SwitchPreference>(PreferencesSource.FINGERPRINT_STATUS)?.isVisible = false
    }

    override fun showBackupEmail(email: String) {
        val keyPref = findPreference<SwitchPreference>(PreferencesSource.SECURITY_KEY)
        keyPref?.summaryOn = getString(R.string.global_settings_pin_on_summary, email)
    }

    override fun showCreatePasswordView() {
        findPreference<SwitchPreference>(PreferencesSource.SECURITY_KEY)?.isChecked = false
        startActivityForResult(PinActivity.newIntentModeCreate(requireActivity()), REQUEST_CODE_CREATE_PIN)
    }

    override fun showRemovePasswordView() {
        findPreference<SwitchPreference>(PreferencesSource.SECURITY_KEY)?.isChecked = true
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

    override fun onNotesAppearanceReset() {
        mHandler.postDelayed(mRecreateRunnable, RECREATE_DELAY)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is GlobalSettingsFragmentListener) {
            mListener = context
        } else {
            throw RuntimeException(context.toString()
                    + " must implement GlobalSettingsFragmentListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
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
        presenter.detachView()
    }

    interface GlobalSettingsFragmentListener {
        fun onThemeAttributeChanged()
    }
}