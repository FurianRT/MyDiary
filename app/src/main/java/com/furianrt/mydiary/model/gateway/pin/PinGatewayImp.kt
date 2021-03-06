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

import com.furianrt.mydiary.model.encryption.MyCipher
import com.furianrt.mydiary.model.source.auth.AuthSource
import com.furianrt.mydiary.model.source.preferences.PreferencesSource
import com.furianrt.mydiary.utils.MyRxUtils
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class PinGatewayImp @Inject constructor(
        private val prefs: PreferencesSource,
        private val auth: AuthSource,
        private val encryption: MyCipher,
        private val scheduler: MyRxUtils.BaseSchedulerProvider
) : PinGateway {

    override fun getPin(): Single<String> =
            Single.fromCallable { prefs.getPin() }
                    .map { encryption.decryptString(it) }
                    .subscribeOn(scheduler.io())

    override fun setPin(pin: String): Completable =
            Completable.fromAction { prefs.setPin(encryption.encryptString(pin)) }
                    .subscribeOn(scheduler.io())

    override fun getBackupEmail(): String = prefs.getBackupEmail()

    override fun setBackupEmail(email: String) {
        prefs.setBackupEmail(email)
    }

    override fun isAuthorized(): Boolean = prefs.isAuthorized()

    override fun setAuthorized(authorized: Boolean) {
        prefs.setAuthorized(authorized)
    }

    override fun getPinRequestDelay(): Long = prefs.getPasswordRequestDelay()

    override fun isPinEnabled(): Boolean = prefs.isPinEnabled()

    override fun setPinEnabled(enable: Boolean) {
        prefs.setPinEnabled(enable)
    }

    override fun sendPinResetEmail(): Completable =
            Completable.fromAction {
                auth.sendPinResetEmail(prefs.getBackupEmail(), encryption.decryptString(prefs.getPin()))
            }.subscribeOn(scheduler.io())

    override fun isFingerprintEnabled(): Boolean = prefs.isFingerprintEnabled()
}