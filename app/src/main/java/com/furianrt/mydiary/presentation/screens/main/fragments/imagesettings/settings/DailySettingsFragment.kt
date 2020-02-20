/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.main.fragments.imagesettings.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceManager

import com.furianrt.mydiary.R
import com.furianrt.mydiary.analytics.MyAnalytics
import com.furianrt.mydiary.model.source.preferences.PreferencesSource
import com.furianrt.mydiary.presentation.base.BasePreference
import javax.inject.Inject

class DailySettingsFragment : BasePreference(), DailySettingsContract.View,
        SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        const val TAG = "DailySettingsFragment"
    }

    @Inject
    lateinit var presenter: DailySettingsContract.Presenter

    private var mListener: OnImageSettingsInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_daily_image, rootKey)
        PreferenceManager.setDefaultValues(activity, R.xml.pref_daily_image, false)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            PreferencesSource.LOAD_DAILY_IMAGE -> {
                mListener?.onDailyImageLoadStateChange()

                val value = sharedPreferences?.getBoolean(PreferencesSource.LOAD_DAILY_IMAGE, true) ?: true
                if (value) {
                    analytics.sendEvent(MyAnalytics.EVENT_DAILY_IMAGE_TURN_ON)
                } else {
                    analytics.sendEvent(MyAnalytics.EVENT_DAILY_IMAGE_TURN_OFF)
                }
            }
            PreferencesSource.DAILY_IMAGE_CATEGORY -> {
                val value = sharedPreferences?.getString(PreferencesSource.DAILY_IMAGE_CATEGORY, PreferencesSource.DAILY_IMAGE_CATEGORY_DEFAULT)
                        ?: PreferencesSource.DAILY_IMAGE_CATEGORY_DEFAULT
                val bundle = Bundle()
                bundle.putString(MyAnalytics.BUNDLE_CATEGORY, value)
                analytics.sendEvent(MyAnalytics.EVENT_DAILY_IMAGE_CATEGORY_CHANGED, bundle)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnImageSettingsInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException("$context must implement OnImageSettingsInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onStart() {
        super.onStart()
        presenter.attachView(this)
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onStop() {
        super.onStop()
        presenter.detachView()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    interface OnImageSettingsInteractionListener {
        fun onDailyImageLoadStateChange()
    }
}
