/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.billing

import android.content.Context
import com.furianrt.mydiary.di.application.component.AppScope
import com.furianrt.mydiary.di.application.modules.app.AppContext
import com.furianrt.mydiary.model.gateway.biling.BillingGateway
import javax.inject.Inject

@AppScope
class BillingManager @Inject constructor(
        @AppContext private val context: Context,
        private val billingGateway: BillingGateway
)/* : PurchasesUpdatedListener, BillingClientStateListener*/ {

   /* private val mIsConnected = AtomicBoolean(false)

    private val mBillingClient = BillingClient.newBuilder(context)
            .setListener(this)
            .enablePendingPurchases()
            .build()

    fun connect() {
        mBillingClient.startConnection(this)
    }

    @MainThread
    fun launchPurchaseFlow(activity: Activity, sku: String): Completable = Completable.create { emitter ->
        val skuDetailsParams = SkuDetailsParams.newBuilder()
                .setSkusList(listOf(sku))
                .setType(BillingClient.SkuType.INAPP)
                .build()

        mBillingClient.querySkuDetailsAsync(skuDetailsParams) { result, details ->
            if (result.responseCode == BillingResponseCode.OK) {
                val skuDetails = details?.find { it.sku == sku } ?: return@querySkuDetailsAsync

                val params = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetails)
                        .build()

                mBillingClient.launchBillingFlow(activity, params)
                emitter.onComplete()
            } else {
                emitter.onError(IOException())
            }
        }
    }

    fun isConnected() = mIsConnected.get()

    override fun onBillingSetupFinished(result: BillingResult) {
        if (result.responseCode == BillingResponseCode.OK) {
            mIsConnected.set(true)
        } else {
            mIsConnected.set(false)
        }
    }*/

   /* override fun onBillingServiceDisconnected() {
        mIsConnected.set(false)
    }*/

   /* override fun onPurchasesUpdated(result: BillingResult, purchases: List<Purchase>?) {

    }*/
}