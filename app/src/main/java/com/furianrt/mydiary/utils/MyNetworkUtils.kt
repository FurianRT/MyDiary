package com.furianrt.mydiary.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.provider.Settings
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

fun Activity.isGoogleServiesAvailable(resolutionCode: Int): Boolean {
    val googleApiAvailability = GoogleApiAvailability.getInstance()
    val status = googleApiAvailability.isGooglePlayServicesAvailable(this)
    if (status != ConnectionResult.SUCCESS) {
        if (googleApiAvailability.isUserResolvableError(status)) {
            googleApiAvailability.getErrorDialog(this, status, resolutionCode)
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