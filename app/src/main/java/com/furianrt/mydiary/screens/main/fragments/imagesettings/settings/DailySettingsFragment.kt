package com.furianrt.mydiary.screens.main.fragments.imagesettings.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle

import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

import com.furianrt.mydiary.R
import com.furianrt.mydiary.analytics.MyAnalytics
import com.furianrt.mydiary.base.BaseView
import com.furianrt.mydiary.data.prefs.PreferencesHelper
import javax.inject.Inject

class DailySettingsFragment : PreferenceFragmentCompat(), BaseView, DailySettingsContract.MvpView,
        SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        const val TAG = "DailySettingsFragment"
    }

    @Inject
    lateinit var mPresenter: DailySettingsContract.Presenter

    @Inject
    lateinit var mAnalytics: MyAnalytics

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
            PreferencesHelper.LOAD_DAILY_IMAGE -> {
                mListener?.onDailyImageLoadStateChange()

                val value = sharedPreferences?.getBoolean(PreferencesHelper.LOAD_DAILY_IMAGE, true) ?: true
                if (value) {
                    mAnalytics.sendEvent(MyAnalytics.EVENT_DAILY_IMAGE_TURN_ON)
                } else {
                    mAnalytics.sendEvent(MyAnalytics.EVENT_DAILY_IMAGE_TURN_OFF)
                }
            }
            PreferencesHelper.DAILY_IMAGE_CATEGORY -> {
                val value = sharedPreferences?.getString(PreferencesHelper.DAILY_IMAGE_CATEGORY, PreferencesHelper.DAILY_IMAGE_CATEGORY_DEFAULT)
                        ?: PreferencesHelper.DAILY_IMAGE_CATEGORY_DEFAULT
                val bundle = Bundle()
                bundle.putString(MyAnalytics.BUNDLE_CATEGORY, value)
                mAnalytics.sendEvent(MyAnalytics.EVENT_DAILY_IMAGE_CATEGORY_CHANGED, bundle)
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
        mPresenter.attachView(this)
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onStop() {
        super.onStop()
        mPresenter.detachView()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    interface OnImageSettingsInteractionListener {
        fun onDailyImageLoadStateChange()
    }
}
