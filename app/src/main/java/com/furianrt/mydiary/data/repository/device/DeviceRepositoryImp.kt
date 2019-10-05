/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.data.repository.device

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Looper
import androidx.core.location.LocationManagerCompat
import com.furianrt.mydiary.data.entity.MyLocation
import com.furianrt.mydiary.di.application.modules.app.AppContext
import com.google.android.gms.location.*
import javax.inject.Inject
import android.os.Build
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.TransactionDetails
import com.furianrt.mydiary.BuildConfig
import com.furianrt.mydiary.data.repository.device.DeviceRepository.*
import com.furianrt.mydiary.utils.generateUniqueId
import java.io.IOException

//todo переписать листенеры на субскрайберы
//todo перенести сюда всю работу с биллингом
class DeviceRepositoryImp @Inject constructor(
        @AppContext private val context: Context,
        private val fusedLocationClient: FusedLocationProviderClient,
        private val geocoder: Geocoder
) : DeviceRepository {

    companion object {
        private const val LOCATION_INTERVAL = 1000L
    }

    private val mBillingProcessor = BillingProcessor(context, BuildConfig.LICENSE_KEY, BuildConfig.MERCHANT_ID, this)

    private val mLocationListeners = mutableSetOf<OnLocationFoundListener>()
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
            result?.let { findAddress(it.lastLocation.latitude, it.lastLocation.longitude) }
            fusedLocationClient.removeLocationUpdates(this)
        }
    }

    private fun findAddress(latitude: Double, longitude: Double) {
        val addresses = try {
            geocoder.getFromLocation(latitude, longitude, 1)
        } catch (e: IOException) {
            e.printStackTrace()
            emptyList<Address>()
        }

        if (addresses.isNotEmpty()) {
            val address = addresses[0].getAddressLine(0)
            if (address != null) {
                val location = MyLocation(generateUniqueId(), address, latitude, longitude)
                mLocationListeners.forEach { it.onLocationFound(location) }
            }
        }
    }

    private fun requestLocation() {
        val locationRequest = LocationRequest.create()
        locationRequest.apply {
            interval = LOCATION_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper())
    }

    @Suppress("DEPRECATION")
    override fun isFingerprintEnabled(): Boolean =
            FingerprintManagerCompat.from(context).hasEnrolledFingerprints()

    @Suppress("DEPRECATION")
    override fun isFingerprintHardwareSupported(): Boolean =
            FingerprintManagerCompat.from(context).isHardwareDetected

    @Suppress("DEPRECATION")
    override fun isNetworkAvailable(): Boolean {
        try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val an = cm.activeNetwork ?: return false
                val capabilities = cm.getNetworkCapabilities(an) ?: return false
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            } else {
                val a = cm.activeNetworkInfo ?: return false
                a.isConnected && (a.type == ConnectivityManager.TYPE_WIFI || a.type == ConnectivityManager.TYPE_MOBILE)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    override fun isLocationAvailable(): Boolean {
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        return if (manager != null) {
            LocationManagerCompat.isLocationEnabled(manager)
        } else {
            false
        }
    }

    override fun findLocation(listener: OnLocationFoundListener) {
        mLocationListeners.add(listener)
        requestLocation()
    }

    override fun removeLocationListener(listener: OnLocationFoundListener) {
        mLocationListeners.remove(listener)
        if (mLocationListeners.isEmpty()) {
            fusedLocationClient.removeLocationUpdates(mLocationCallback)
        }
    }

    override fun isItemPurchased(productId: String): Boolean = mBillingProcessor.isPurchased(productId)

    override fun onBillingInitialized() {

    }

    override fun onPurchaseHistoryRestored() {

    }

    override fun onProductPurchased(productId: String, details: TransactionDetails?) {

    }

    override fun onBillingError(errorCode: Int, error: Throwable?) {

    }
}