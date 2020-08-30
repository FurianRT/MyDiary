/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.model.gateway.pin

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface PinGateway {
    fun getPin(): Single<String>
    fun setPin(pin: String): Completable
    fun getBackupEmail(): String
    fun setBackupEmail(email: String)
    fun isAuthorized(): Boolean
    fun setAuthorized(authorized: Boolean)
    fun getPinRequestDelay(): Long
    fun isPinEnabled(): Boolean
    fun setPinEnabled(enable: Boolean)
    fun sendPinResetEmail(): Completable
    fun isFingerprintEnabled(): Boolean
}