/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.model.gateway.general

import com.furianrt.mydiary.model.entity.SyncProgressMessage

interface GeneralGateway {
    fun setLastAppLaunchTime(time: Long)
    fun getLastAppLaunchTime(): Long
    fun getNumberOfLaunches(): Int
    fun setNumberOfLaunches(count: Int)
    fun setLastSyncMessage(message: SyncProgressMessage?)
    fun getLastSyncMessage(): SyncProgressMessage?
    fun enableRateOffer(enable: Boolean)
    fun isRateOfferEnabled(): Boolean
    fun isNeedDefaultValues(): Boolean
    fun setNeedDefaultValues(need: Boolean)
}