package com.furianrt.mydiary.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

const val PLAY_SERVICES_RESOLUTION_REQUEST = 3223

fun Activity.isGoogleServiesAvailable(): Boolean {
    val googleApiAvailability = GoogleApiAvailability.getInstance()
    val status = googleApiAvailability.isGooglePlayServicesAvailable(this)
    if (status != ConnectionResult.SUCCESS) {
        if (googleApiAvailability.isUserResolvableError(status)) {
            googleApiAvailability.getErrorDialog(this, status, PLAY_SERVICES_RESOLUTION_REQUEST)
                    .show()
        }
        return false
    }
    return true
}

fun Context.isNetworkAvailable(): Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = cm.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnected
}

fun Context.isLocationEnabled(): Boolean {
    val locationMode: Int
    try {
        locationMode = Settings.Secure.getInt(contentResolver, Settings.Secure.LOCATION_MODE)
    } catch (e: SettingNotFoundException) {
        e.printStackTrace()
        return false
    }

    return locationMode != Settings.Secure.LOCATION_MODE_OFF
}