package com.furianrt.mydiary.data.repository.pin

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