package com.furianrt.mydiary.utils

import android.content.Context
import android.net.ConnectivityManager
import android.provider.Settings

fun Context.isNetworkAvailable(): Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = cm.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnected
}

fun Context.isLocationEnabled(): Boolean {
    try {
        val locationMode = Settings.Secure.getInt(contentResolver, Settings.Secure.LOCATION_MODE)
        if (locationMode == Settings.Secure.LOCATION_MODE_HIGH_ACCURACY) {
            return true
        }
    } catch (e: Settings.SettingNotFoundException) {
        e.printStackTrace()
    }
    return false
}