/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain.check

import com.furianrt.mydiary.BuildConfig
import com.furianrt.mydiary.model.gateway.device.DeviceGateway
import javax.inject.Inject

class IsPremiumPurchasedUseCase @Inject constructor(
        private val deviceGateway: DeviceGateway
) {

    operator fun invoke(): Boolean = deviceGateway.isItemPurchased(BuildConfig.ITEM_PREMIUM_SKU)
}