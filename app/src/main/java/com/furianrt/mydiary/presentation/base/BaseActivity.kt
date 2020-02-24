/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.base

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.TransactionDetails
import com.furianrt.mydiary.BuildConfig
import com.furianrt.mydiary.R
import com.furianrt.mydiary.analytics.MyAnalytics
import com.furianrt.mydiary.domain.auth.AuthorizeUseCase
import com.furianrt.mydiary.domain.check.IsAuthorizedUseCase
import com.furianrt.mydiary.domain.check.IsPinEnabledUseCase
import com.furianrt.mydiary.domain.get.GetAppAccentColorUseCase
import com.furianrt.mydiary.domain.get.GetAppFontStyleUseCase
import com.furianrt.mydiary.domain.get.GetAppPrimaryColorUseCase
import com.furianrt.mydiary.utils.getColorCompat
import com.furianrt.mydiary.presentation.screens.pin.PinActivity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import javax.inject.Inject

abstract class BaseActivity(@LayoutRes contentLayoutId: Int) : AppCompatActivity(contentLayoutId),
        BaseView, BillingProcessor.IBillingHandler {

    companion object {
        const val TAG = "BaseActivity"
    }

    @Inject
    lateinit var analytics: MyAnalytics

    @Inject
    lateinit var isPinEnabledUseCase: IsPinEnabledUseCase

    @Inject
    lateinit var isAuthorizedUseCase: IsAuthorizedUseCase

    @Inject
    lateinit var authorizeUseCase: AuthorizeUseCase

    @Inject
    lateinit var getAppPrimaryColorUseCase: GetAppPrimaryColorUseCase

    @Inject
    lateinit var getAppAccentColorUseCase: GetAppAccentColorUseCase

    @Inject
    lateinit var getAppFontStyleUseCase: GetAppFontStyleUseCase

    private lateinit var mBillingProcessor: BillingProcessor
    private val mCompositeDisposable = CompositeDisposable()

    protected open var needLockScreen = true
    protected open var skipOneLock = false

    protected fun addDisposable(disposable: Disposable) {
        mCompositeDisposable.add(disposable)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(this).inject(this)
        applyStyleToTheme()
        super.onCreate(savedInstanceState)
        mBillingProcessor = BillingProcessor(this, BuildConfig.LICENSE_KEY, BuildConfig.MERCHANT_ID, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!mBillingProcessor.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
        overridePendingTransition(R.anim.screen_right_in, R.anim.screen_left_out)
    }

    override fun startActivityForResult(intent: Intent?, requestCode: Int) {
        super.startActivityForResult(intent, requestCode)
        overridePendingTransition(R.anim.screen_right_in, R.anim.screen_left_out)
    }

    protected fun isOneTimePurchaseSupported() =
            BillingProcessor.isIabServiceAvailable(this) && mBillingProcessor.isOneTimePurchaseSupported

    protected fun loadOwnedPurchasesFromGoogle(): Boolean = mBillingProcessor.loadOwnedPurchasesFromGoogle()

    protected fun purchaseItem(productId: String) = mBillingProcessor.purchase(this, productId)

    protected fun isItemPurchased(productId: String) = mBillingProcessor.isPurchased(productId)

    protected fun isBillingInitialized() = mBillingProcessor.isInitialized

    override fun onBillingInitialized() {
        Log.e(TAG, "onBillingInitialized")
    }

    override fun onPurchaseHistoryRestored() {
        Log.e(TAG, "onPurchaseHistoryRestored")
    }

    override fun onProductPurchased(productId: String, details: TransactionDetails?) {
        analytics.sendEvent(MyAnalytics.EVENT_PREMIUM_PURCHASED)
        Log.e(TAG, "onProductPurchased")
    }

    override fun onBillingError(errorCode: Int, error: Throwable?) {
        Log.e(TAG, "onBillingError: ${error?.printStackTrace()}")
    }

    override fun onStart() {
        super.onStart()
        if (skipOneLock) {
            authorizeUseCase(true)
            skipOneLock = false
        } else if (needLockScreen) {
            openPinScreen()
        }
    }

    override fun onStop() {
        super.onStop()
        mCompositeDisposable.clear()
    }

    private fun openPinScreen() {
        if (isPinEnabledUseCase() && !isAuthorizedUseCase()) {
            startActivity(PinActivity.newIntentModeLock(this))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBillingProcessor.release()
    }

    // Похоже, что динамическое создание стиля в андроиде не предусмотрено,
    // поэтому приходится хардкодить этот бред
    private fun applyStyleToTheme() {
        getAppFontStyleUseCase()?.let { style -> theme.applyStyle(style, true) }

        when (getAppPrimaryColorUseCase()) {
            getColorCompat(R.color.r1) -> theme.applyStyle(R.style.OverlayPrimaryColorR1, true)
            getColorCompat(R.color.r4) -> theme.applyStyle(R.style.OverlayPrimaryColorR4, true)
            getColorCompat(R.color.r5) -> theme.applyStyle(R.style.OverlayPrimaryColorR5, true)
            getColorCompat(R.color.p1) -> theme.applyStyle(R.style.OverlayPrimaryColorP1, true)
            getColorCompat(R.color.p2) -> theme.applyStyle(R.style.OverlayPrimaryColorP2, true)
            getColorCompat(R.color.p3) -> theme.applyStyle(R.style.OverlayPrimaryColorP3, true)
            getColorCompat(R.color.b1) -> theme.applyStyle(R.style.OverlayPrimaryColorB1, true)
            getColorCompat(R.color.b4) -> theme.applyStyle(R.style.OverlayPrimaryColorB4, true)
            getColorCompat(R.color.b5) -> theme.applyStyle(R.style.OverlayPrimaryColorB5, true)
            getColorCompat(R.color.g1) -> theme.applyStyle(R.style.OverlayPrimaryColorG1, true)
            getColorCompat(R.color.g3) -> theme.applyStyle(R.style.OverlayPrimaryColorG3, true)
            getColorCompat(R.color.g4) -> theme.applyStyle(R.style.OverlayPrimaryColorG4, true)
            getColorCompat(R.color.g5) -> theme.applyStyle(R.style.OverlayPrimaryColorG5, true)
            getColorCompat(R.color.e1) -> theme.applyStyle(R.style.OverlayPrimaryColorE1, true)
            getColorCompat(R.color.e3) -> theme.applyStyle(R.style.OverlayPrimaryColorE3, true)
            getColorCompat(R.color.e4) -> theme.applyStyle(R.style.OverlayPrimaryColorE4, true)
            getColorCompat(R.color.e5) -> theme.applyStyle(R.style.OverlayPrimaryColorE5, true)
            getColorCompat(R.color.br1) -> theme.applyStyle(R.style.OverlayPrimaryColorBr1, true)
            getColorCompat(R.color.br2) -> theme.applyStyle(R.style.OverlayPrimaryColorBr2, true)
            getColorCompat(R.color.gr1) -> theme.applyStyle(R.style.OverlayPrimaryColorGr1, true)
            getColorCompat(R.color.gr2) -> theme.applyStyle(R.style.OverlayPrimaryColorGr2, true)
            getColorCompat(R.color.u1) -> theme.applyStyle(R.style.OverlayPrimaryColorU1, true)
            getColorCompat(R.color.u2) -> theme.applyStyle(R.style.OverlayPrimaryColorU2, true)
            getColorCompat(R.color.black) -> theme.applyStyle(R.style.OverlayPrimaryColorBlack, true)
        }

        when (getAppAccentColorUseCase()) {
            getColorCompat(R.color.r1) -> theme.applyStyle(R.style.OverlayAccentColorR1, true)
            getColorCompat(R.color.r4) -> theme.applyStyle(R.style.OverlayAccentColorR4, true)
            getColorCompat(R.color.r5) -> theme.applyStyle(R.style.OverlayAccentColorR5, true)
            getColorCompat(R.color.p1) -> theme.applyStyle(R.style.OverlayAccentColorP1, true)
            getColorCompat(R.color.p2) -> theme.applyStyle(R.style.OverlayAccentColorP2, true)
            getColorCompat(R.color.p3) -> theme.applyStyle(R.style.OverlayAccentColorP3, true)
            getColorCompat(R.color.b1) -> theme.applyStyle(R.style.OverlayAccentColorB1, true)
            getColorCompat(R.color.b4) -> theme.applyStyle(R.style.OverlayAccentColorB4, true)
            getColorCompat(R.color.b5) -> theme.applyStyle(R.style.OverlayAccentColorB5, true)
            getColorCompat(R.color.g1) -> theme.applyStyle(R.style.OverlayAccentColorG1, true)
            getColorCompat(R.color.g3) -> theme.applyStyle(R.style.OverlayAccentColorG3, true)
            getColorCompat(R.color.g4) -> theme.applyStyle(R.style.OverlayAccentColorG4, true)
            getColorCompat(R.color.g5) -> theme.applyStyle(R.style.OverlayAccentColorG5, true)
            getColorCompat(R.color.e1) -> theme.applyStyle(R.style.OverlayAccentColorE1, true)
            getColorCompat(R.color.e3) -> theme.applyStyle(R.style.OverlayAccentColorE3, true)
            getColorCompat(R.color.e4) -> theme.applyStyle(R.style.OverlayAccentColorE4, true)
            getColorCompat(R.color.e5) -> theme.applyStyle(R.style.OverlayAccentColorE5, true)
            getColorCompat(R.color.br1) -> theme.applyStyle(R.style.OverlayAccentColorBr1, true)
            getColorCompat(R.color.br2) -> theme.applyStyle(R.style.OverlayAccentColorBr2, true)
            getColorCompat(R.color.gr1) -> theme.applyStyle(R.style.OverlayAccentColorGr1, true)
            getColorCompat(R.color.gr2) -> theme.applyStyle(R.style.OverlayAccentColorGr2, true)
            getColorCompat(R.color.u1) -> theme.applyStyle(R.style.OverlayAccentColorU1, true)
            getColorCompat(R.color.u2) -> theme.applyStyle(R.style.OverlayAccentColorU2, true)
            getColorCompat(R.color.black) -> theme.applyStyle(R.style.OverlayAccentColorBlack, true)
        }
    }
}