package com.furianrt.mydiary.data.repository.pin

import com.furianrt.mydiary.data.auth.AuthHelper
import com.furianrt.mydiary.data.encryption.EncryptionHelper
import com.furianrt.mydiary.data.prefs.PreferencesHelper
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single
import javax.inject.Inject

class PinRepositoryImp @Inject constructor(
        private val prefs: PreferencesHelper,
        private val auth: AuthHelper,
        private val encryption: EncryptionHelper,
        private val rxScheduler: Scheduler
) : PinRepository {

    override fun getPin(): Single<String> =
            Single.fromCallable { prefs.getPin() }
                    .map { encryption.decryptString(it) }
                    .subscribeOn(rxScheduler)

    override fun setPin(pin: String): Completable =
            Completable.fromAction { prefs.setPin(encryption.encryptString(pin)) }
                    .subscribeOn(rxScheduler)

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
            }.subscribeOn(rxScheduler)

    override fun isFingerprintEnabled(): Boolean = prefs.isFingerprintEnabled()
}