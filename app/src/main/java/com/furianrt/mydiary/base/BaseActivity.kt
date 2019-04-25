package com.furianrt.mydiary.base

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.prefs.PreferencesHelper
import com.furianrt.mydiary.pin.PinActivity

abstract class BaseActivity : AppCompatActivity() {

    open var needLockScreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        application.setTheme(R.style.AppTheme)
        applyStyleToTheme()
        super.onCreate(savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        application.setTheme(R.style.AppTheme)
        applyStyleToTheme()
        super.onCreate(savedInstanceState, persistentState)
    }

    override fun onResume() {
        super.onResume()
        if (needLockScreen) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            val isPinEnabled = prefs.getBoolean(PreferencesHelper.SECURITY_KEY, false)
            val isAuthorized = prefs.getBoolean(PreferencesHelper.SECURITY_IS_AUTHORIZED, true)
            if (isPinEnabled && !isAuthorized) {
                val intent = Intent(this, PinActivity::class.java)
                intent.putExtra(PinActivity.EXTRA_MODE, PinActivity.MODE_LOCK)
                startActivity(intent)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putBoolean(PreferencesHelper.SECURITY_IS_AUTHORIZED, true)
                .apply()
    }

    // Похоже, что динамическое создание стиля в андроиде не предусмотрено,
    // поэтому приходится хардкодить этот бред
    private fun applyStyleToTheme() {
        when (PreferenceManager
                .getDefaultSharedPreferences(this)
                .getInt(PreferencesHelper.COLOR_PRIMARY, 0)) {
            ContextCompat.getColor(this, R.color.r1) ->
                theme.applyStyle(R.style.OverlayPrimaryColorR1, true)
            ContextCompat.getColor(this, R.color.r2) ->
                theme.applyStyle(R.style.OverlayPrimaryColorR2, true)
            ContextCompat.getColor(this, R.color.r3) ->
                theme.applyStyle(R.style.OverlayPrimaryColorR3, true)
            ContextCompat.getColor(this, R.color.r4) ->
                theme.applyStyle(R.style.OverlayPrimaryColorR4, true)
            ContextCompat.getColor(this, R.color.r5) ->
                theme.applyStyle(R.style.OverlayPrimaryColorR5, true)
            ContextCompat.getColor(this, R.color.r6) ->
                theme.applyStyle(R.style.OverlayPrimaryColorR6, true)
            ContextCompat.getColor(this, R.color.r7) ->
                theme.applyStyle(R.style.OverlayPrimaryColorR7, true)
            ContextCompat.getColor(this, R.color.p1) ->
                theme.applyStyle(R.style.OverlayPrimaryColorP1, true)
            ContextCompat.getColor(this, R.color.p2) ->
                theme.applyStyle(R.style.OverlayPrimaryColorP2, true)
            ContextCompat.getColor(this, R.color.p3) ->
                theme.applyStyle(R.style.OverlayPrimaryColorP3, true)
            ContextCompat.getColor(this, R.color.b1) ->
                theme.applyStyle(R.style.OverlayPrimaryColorB1, true)
            ContextCompat.getColor(this, R.color.b2) ->
                theme.applyStyle(R.style.OverlayPrimaryColorB2, true)
            ContextCompat.getColor(this, R.color.b3) ->
                theme.applyStyle(R.style.OverlayPrimaryColorB3, true)
            ContextCompat.getColor(this, R.color.b4) ->
                theme.applyStyle(R.style.OverlayPrimaryColorB4, true)
            ContextCompat.getColor(this, R.color.b5) ->
                theme.applyStyle(R.style.OverlayPrimaryColorB5, true)
            ContextCompat.getColor(this, R.color.g1) ->
                theme.applyStyle(R.style.OverlayPrimaryColorG1, true)
            ContextCompat.getColor(this, R.color.g2) ->
                theme.applyStyle(R.style.OverlayPrimaryColorG2, true)
            ContextCompat.getColor(this, R.color.g3) ->
                theme.applyStyle(R.style.OverlayPrimaryColorG3, true)
            ContextCompat.getColor(this, R.color.g4) ->
                theme.applyStyle(R.style.OverlayPrimaryColorG4, true)
            ContextCompat.getColor(this, R.color.g5) ->
                theme.applyStyle(R.style.OverlayPrimaryColorG5, true)
            ContextCompat.getColor(this, R.color.g6) ->
                theme.applyStyle(R.style.OverlayPrimaryColorG6, true)
            ContextCompat.getColor(this, R.color.e1) ->
                theme.applyStyle(R.style.OverlayPrimaryColorE1, true)
            ContextCompat.getColor(this, R.color.e2) ->
                theme.applyStyle(R.style.OverlayPrimaryColorE2, true)
            ContextCompat.getColor(this, R.color.e3) ->
                theme.applyStyle(R.style.OverlayPrimaryColorE3, true)
            ContextCompat.getColor(this, R.color.e4) ->
                theme.applyStyle(R.style.OverlayPrimaryColorE4, true)
            ContextCompat.getColor(this, R.color.e5) ->
                theme.applyStyle(R.style.OverlayPrimaryColorE5, true)
            ContextCompat.getColor(this, R.color.br1) ->
                theme.applyStyle(R.style.OverlayPrimaryColorBr1, true)
            ContextCompat.getColor(this, R.color.br2) ->
                theme.applyStyle(R.style.OverlayPrimaryColorBr2, true)
            ContextCompat.getColor(this, R.color.gr1) ->
                theme.applyStyle(R.style.OverlayPrimaryColorGr1, true)
            ContextCompat.getColor(this, R.color.gr2) ->
                theme.applyStyle(R.style.OverlayPrimaryColorGr2, true)
            ContextCompat.getColor(this, R.color.u1) ->
                theme.applyStyle(R.style.OverlayPrimaryColorU1, true)
            ContextCompat.getColor(this, R.color.u2) ->
                theme.applyStyle(R.style.OverlayPrimaryColorU2, true)
            ContextCompat.getColor(this, R.color.u3) ->
                theme.applyStyle(R.style.OverlayPrimaryColorU3, true)
            ContextCompat.getColor(this, R.color.black) ->
                theme.applyStyle(R.style.OverlayPrimaryColorBlack, true)
        }

        when (PreferenceManager
                .getDefaultSharedPreferences(this)
                .getInt(PreferencesHelper.COLOR_ACCENT, 0)) {
            ContextCompat.getColor(this, R.color.r1) ->
                theme.applyStyle(R.style.OverlayAccentColorR1, true)
            ContextCompat.getColor(this, R.color.r2) ->
                theme.applyStyle(R.style.OverlayAccentColorR2, true)
            ContextCompat.getColor(this, R.color.r3) ->
                theme.applyStyle(R.style.OverlayAccentColorR3, true)
            ContextCompat.getColor(this, R.color.r4) ->
                theme.applyStyle(R.style.OverlayAccentColorR4, true)
            ContextCompat.getColor(this, R.color.r5) ->
                theme.applyStyle(R.style.OverlayAccentColorR5, true)
            ContextCompat.getColor(this, R.color.r6) ->
                theme.applyStyle(R.style.OverlayAccentColorR6, true)
            ContextCompat.getColor(this, R.color.r7) ->
                theme.applyStyle(R.style.OverlayAccentColorR7, true)
            ContextCompat.getColor(this, R.color.p1) ->
                theme.applyStyle(R.style.OverlayAccentColorP1, true)
            ContextCompat.getColor(this, R.color.p2) ->
                theme.applyStyle(R.style.OverlayAccentColorP2, true)
            ContextCompat.getColor(this, R.color.p3) ->
                theme.applyStyle(R.style.OverlayAccentColorP3, true)
            ContextCompat.getColor(this, R.color.b1) ->
                theme.applyStyle(R.style.OverlayAccentColorB1, true)
            ContextCompat.getColor(this, R.color.b2) ->
                theme.applyStyle(R.style.OverlayAccentColorB2, true)
            ContextCompat.getColor(this, R.color.b3) ->
                theme.applyStyle(R.style.OverlayAccentColorB3, true)
            ContextCompat.getColor(this, R.color.b4) ->
                theme.applyStyle(R.style.OverlayAccentColorB4, true)
            ContextCompat.getColor(this, R.color.b5) ->
                theme.applyStyle(R.style.OverlayAccentColorB5, true)
            ContextCompat.getColor(this, R.color.g1) ->
                theme.applyStyle(R.style.OverlayAccentColorG1, true)
            ContextCompat.getColor(this, R.color.g2) ->
                theme.applyStyle(R.style.OverlayAccentColorG2, true)
            ContextCompat.getColor(this, R.color.g3) ->
                theme.applyStyle(R.style.OverlayAccentColorG3, true)
            ContextCompat.getColor(this, R.color.g4) ->
                theme.applyStyle(R.style.OverlayAccentColorG4, true)
            ContextCompat.getColor(this, R.color.g5) ->
                theme.applyStyle(R.style.OverlayAccentColorG5, true)
            ContextCompat.getColor(this, R.color.g6) ->
                theme.applyStyle(R.style.OverlayAccentColorG6, true)
            ContextCompat.getColor(this, R.color.e1) ->
                theme.applyStyle(R.style.OverlayAccentColorE1, true)
            ContextCompat.getColor(this, R.color.e2) ->
                theme.applyStyle(R.style.OverlayAccentColorE2, true)
            ContextCompat.getColor(this, R.color.e3) ->
                theme.applyStyle(R.style.OverlayAccentColorE3, true)
            ContextCompat.getColor(this, R.color.e4) ->
                theme.applyStyle(R.style.OverlayAccentColorE4, true)
            ContextCompat.getColor(this, R.color.e5) ->
                theme.applyStyle(R.style.OverlayAccentColorE5, true)
            ContextCompat.getColor(this, R.color.br1) ->
                theme.applyStyle(R.style.OverlayAccentColorBr1, true)
            ContextCompat.getColor(this, R.color.br2) ->
                theme.applyStyle(R.style.OverlayAccentColorBr2, true)
            ContextCompat.getColor(this, R.color.gr1) ->
                theme.applyStyle(R.style.OverlayAccentColorGr1, true)
            ContextCompat.getColor(this, R.color.gr2) ->
                theme.applyStyle(R.style.OverlayAccentColorGr2, true)
            ContextCompat.getColor(this, R.color.u1) ->
                theme.applyStyle(R.style.OverlayAccentColorU1, true)
            ContextCompat.getColor(this, R.color.u2) ->
                theme.applyStyle(R.style.OverlayAccentColorU2, true)
            ContextCompat.getColor(this, R.color.u3) ->
                theme.applyStyle(R.style.OverlayAccentColorU3, true)
            ContextCompat.getColor(this, R.color.black) ->
                theme.applyStyle(R.style.OverlayAccentColorBlack, true)
        }
    }
}