/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.model.repository.pin

import io.reactivex.Completable
import io.reactivex.Single

interface PinRepository {
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