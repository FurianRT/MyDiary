/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.model.gateway.device

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.net.*
import android.os.Looper
import androidx.core.location.LocationManagerCompat
import com.furianrt.mydiary.model.entity.MyLocation
import com.furianrt.mydiary.di.application.modules.app.AppContext
import com.google.android.gms.location.*
import javax.inject.Inject
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.biometric.BiometricManager
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.TransactionDetails
import com.furianrt.mydiary.BuildConfig
import com.furianrt.mydiary.R
import com.furianrt.mydiary.analytics.MyAnalytics
import com.furianrt.mydiary.di.application.component.AppScope
import com.furianrt.mydiary.model.gateway.device.DeviceGateway.*
import com.furianrt.mydiary.utils.generateUniqueId
import com.hbisoft.pickit.PickiT
import com.hbisoft.pickit.PickiTCallbacks
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.subjects.PublishSubject
import java.io.IOException

@AppScope
class DeviceGatewayImp @Inject constructor(
        @AppContext private val context: Context,
        private val fusedLocationClient: FusedLocationProviderClient,
        private val geocoder: Geocoder,
        private val analytics: MyAnalytics
) : DeviceGateway {

    companion object {
        private const val TAG = "DeviceGatewayImp"
        private const val LOCATION_INTERVAL = 1000L
    }

    private val mLocationSubject = PublishSubject.create<MyLocation>()
    private val mBillingProcessor = BillingProcessor(context, BuildConfig.LICENSE_KEY, BuildConfig.MERCHANT_ID, this)
    private val mPickits = mutableListOf<PickiT>()
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
            fusedLocationClient.removeLocationUpdates(this)
            Thread(Runnable {
                result?.let {
                    findAddress(it.lastLocation.latitude, it.lastLocation.longitude)
                }
            }).start()
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
                mLocationSubject.onNext(MyLocation(generateUniqueId(), address, latitude, longitude))
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

    override fun isFingerprintEnabled(): Boolean =
            BiometricManager.from(context).canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS

    @Suppress("DEPRECATION")
    override fun isNetworkAvailable(): Boolean {
        try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager?
            val an = cm?.activeNetwork ?: return false
            val capabilities = cm.getNetworkCapabilities(an) ?: return false
            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    override fun isLocationAvailable(): Boolean {
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager?
        return if (manager != null) {
            LocationManagerCompat.isLocationEnabled(manager)
        } else {
            false
        }
    }

    override fun findLocation(): Maybe<MyLocation> {
        requestLocation()
        return mLocationSubject
                .firstElement()
    }

    override fun getTutorialNoteBitmap(): Bitmap =
            BitmapFactory.decodeResource(context.resources, R.drawable.tutorial_header_image)

    override fun getTutorialNoteMoodId(): Int = context.resources.getStringArray(R.array.moods).size

    override fun getTutorialNoteTitle(): String = context.getString(R.string.tutorial_note_title)

    override fun getTutorialNoteContent(): String = context.getString(R.string.tutorial_note_content)

    override fun isItemPurchased(productId: String): Boolean = mBillingProcessor.isPurchased(productId)

    override fun onBillingInitialized() = Unit
    override fun onPurchaseHistoryRestored() = Unit
    override fun onProductPurchased(productId: String, details: TransactionDetails?) = Unit
    override fun onBillingError(errorCode: Int, error: Throwable?) = Unit

    @Synchronized
    override fun getRealPathFromUri(uri: String, callback: OnUriConvertCallback) {
        val pickit = PickiT(context, object : PickiTCallbacks {
            override fun PickiTonProgressUpdate(progress: Int) {
                Log.e(TAG, "PickiTonProgressUpdate $progress")
            }

            override fun PickiTonStartListener() {
                Log.e(TAG, "PickiTonStartListener")
            }

            override fun PickiTonCompleteListener(path: String?, wasDriveFile: Boolean, wasUnknownProvider: Boolean, wasSuccessful: Boolean, Reason: String?) {
                if (wasSuccessful && path != null) {
                    callback.onUriRealPathReceived(path)
                } else {
                    callback.onUriRealPathError()
                    var bundle: Bundle? = null
                    Reason?.let { bundle = Bundle().apply { putString(MyAnalytics.BUNDLE_ERROR_TEXT, it) } }
                    analytics.sendEvent(MyAnalytics.EVENT_IMAGE_COPY_ERROR, bundle)
                }
            }
        })
        mPickits.add(pickit)
        pickit.getPath(Uri.parse(uri), Build.VERSION.SDK_INT)
    }

    @Synchronized
    override fun clearUriTempFiles() {
        val iterator = mPickits.iterator()
        while (iterator.hasNext()) {
            iterator.next().deleteTemporaryFile()
        }
        mPickits.clear()
    }
}