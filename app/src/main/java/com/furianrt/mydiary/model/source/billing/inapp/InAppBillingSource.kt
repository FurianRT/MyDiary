package com.furianrt.mydiary.model.source.billing.inapp

import com.furianrt.mydiary.model.source.billing.entity.ConnectionState
import io.reactivex.rxjava3.core.Observable

interface InAppBillingSource {
    fun connectToBillingServices(): Observable<ConnectionState>
}