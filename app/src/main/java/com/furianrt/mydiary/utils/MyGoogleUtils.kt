package com.furianrt.mydiary.utils

import android.app.Activity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

fun Activity.isGoogleServicesAvailable(resolutionCode: Int): Boolean {
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