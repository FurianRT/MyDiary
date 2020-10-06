package com.furianrt.mydiary.model.gateway.biling

import com.furianrt.mydiary.model.source.billing.inapp.InAppBillingSource
import javax.inject.Inject

class BillingGatewayImp @Inject constructor(
        private val inAppBillingSource: InAppBillingSource
) : BillingGateway {
}