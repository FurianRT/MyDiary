package com.furianrt.mydiary.model.source.billing.inapp

import android.content.Context
import com.furianrt.mydiary.di.application.modules.app.AppContext
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class InAppBillingSourceImp @Inject constructor(@AppContext context: Context): InAppBillingSource {

    companion object {
        private var sIsConnected = false
    }

   /* private val billingClient = BillingClient.newBuilder(context)
            .setListener { billingResult, purchases ->
                if (billingResult.responseCode == BillingResponseCode.OK && purchases != null) {

                } else if (billingResult.responseCode == BillingResponseCode.USER_CANCELED) {

                } else {

                }
            }
            .build()*/

   //override fun connectToBillingServices(): Observable<ConnectionState> = Observable.create { emitter ->
       /* billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingResponseCode.OK) {
                    sIsConnected = true
                    emitter.onNext(ConnectionState.Connected)
                } else {
                    sIsConnected = false
                    emitter.onNext(ConnectionState.Error.Unknown())
                }
            }

            override fun onBillingServiceDisconnected() {
                sIsConnected = false
                emitter.onNext(ConnectionState.Disconnected)
            }
        })*/
  //  }
}