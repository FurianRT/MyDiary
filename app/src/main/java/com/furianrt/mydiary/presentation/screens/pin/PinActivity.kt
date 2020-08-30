/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.pin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import com.furianrt.mydiary.R
import com.furianrt.mydiary.analytics.MyAnalytics
import com.furianrt.mydiary.presentation.base.BaseActivity
import com.furianrt.mydiary.presentation.general.LockableBottomSheetBehavior
import com.furianrt.mydiary.presentation.screens.pin.PinActivity.Mode.*
import com.furianrt.mydiary.presentation.screens.pin.fragments.backupemail.BackupEmailFragment
import com.furianrt.mydiary.presentation.screens.pin.fragments.done.DoneEmailFragment
import com.furianrt.mydiary.presentation.screens.pin.fragments.sendemail.SendEmailFragment
import com.furianrt.mydiary.utils.animateShake
import com.furianrt.mydiary.utils.inTransaction
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.activity_pin.*
import kotlinx.android.synthetic.main.bottom_sheet_pin.*
import java.util.concurrent.Executors
import javax.inject.Inject

class PinActivity : BaseActivity(R.layout.activity_pin), PinContract.View,
        BackupEmailFragment.OnBackupEmailFragmentListener, PinBottomSheetHolder {

    companion object {
        private const val BOTTOM_SHEET_EXPAND_DELAY = 200L
        private const val BUNDLE_BOTTOM_SHEET_STATE = "bottomSheetState"
        private const val EXTRA_MODE = "mode"

        fun newIntentModeCreate(context: Context) =
                Intent(context, PinActivity::class.java).apply { putExtra(EXTRA_MODE, MODE_CREATE) }

        fun newIntentModeRemove(context: Context) =
                Intent(context, PinActivity::class.java).apply { putExtra(EXTRA_MODE, MODE_REMOVE) }

        fun newIntentModeLock(context: Context) =
                Intent(context, PinActivity::class.java).apply { putExtra(EXTRA_MODE, MODE_LOCK) }
    }

    @Inject
    lateinit var presenter: PinContract.Presenter

    private enum class Mode { MODE_CREATE, MODE_REMOVE, MODE_LOCK }

    private lateinit var mBottomSheet: LockableBottomSheetBehavior<MaterialCardView>
    private lateinit var mMode: Mode
    private val mHandler = Handler()
    private val mBottomSheetOpenRunnable: Runnable = Runnable {
        mBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private val mBiometricPrompt = BiometricPrompt(this, Executors.newSingleThreadExecutor(),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    analytics.sendEvent(MyAnalytics.EVENT_FINGERPRINT_SUCCESS)
                    presenter.onFingerprintAccepted()
                }
            })

    private val mBottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_HIDDEN) {
                supportFragmentManager.findFragmentByTag(SendEmailFragment.TAG)?.let {
                    supportFragmentManager.inTransaction { remove(it) }
                }
                supportFragmentManager.findFragmentByTag(DoneEmailFragment.TAG)?.let {
                    supportFragmentManager.inTransaction { remove(it) }
                }
            }
        }
    }

    override var needLockScreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(this).inject(this)
        super.onCreate(savedInstanceState)

        mMode = intent.getSerializableExtra(EXTRA_MODE) as Mode

        mBottomSheet = BottomSheetBehavior.from(pin_sheet_container) as LockableBottomSheetBehavior

        if (mMode == MODE_CREATE || mMode == MODE_REMOVE) {
            button_forgot_pin.visibility = View.INVISIBLE
            mBottomSheet.locked = true
        } else {
            mBottomSheet.locked = false
        }

        savedInstanceState?.let {
            mBottomSheet.state = it.getInt(BUNDLE_BOTTOM_SHEET_STATE, BottomSheetBehavior.STATE_COLLAPSED)
            presenter.onRestoreInstanceState(it)
        }

        button_one.setOnClickListener { valueEntered(1) }
        button_two.setOnClickListener { valueEntered(2) }
        button_three.setOnClickListener { valueEntered(3) }
        button_four.setOnClickListener { valueEntered(4) }
        button_five.setOnClickListener { valueEntered(5) }
        button_six.setOnClickListener { valueEntered(6) }
        button_seven.setOnClickListener { valueEntered(7) }
        button_eight.setOnClickListener { valueEntered(8) }
        button_nine.setOnClickListener { valueEntered(9) }
        button_zero.setOnClickListener { valueEntered(0) }
        button_backspace.setOnClickListener { presenter.onButtonBackspaceClick() }
        button_forgot_pin.setOnClickListener { presenter.onButtonForgotPinClick() }
        button_pin_close.setOnClickListener {
            when (mMode) {
                MODE_CREATE -> presenter.onButtonCloseClickModeCreate()
                MODE_REMOVE -> presenter.onButtonCloseClickModeRemove()
                MODE_LOCK -> presenter.onButtonCloseClickModeLock()
            }
        }
        button_fingerprint.setOnClickListener { presenter.onButtonFingerprintClick() }
    }

    private fun valueEntered(value: Int) {
        require(value in 0..9)
        when (mMode) {
            MODE_CREATE -> presenter.onValueEnteredModeCreate(value)
            MODE_REMOVE -> presenter.onValueEnteredModeRemove(value)
            MODE_LOCK -> presenter.onValueEnteredModeLock(value)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(BUNDLE_BOTTOM_SHEET_STATE, mBottomSheet.state)
        presenter.onSaveInstanceState(outState)
    }

    override fun showPin(pin: String) {
        val length = pin.length
        if (length > 0) image_pin_one.setImageResource(R.drawable.circle_white_background)
        if (length > 1) image_pin_two.setImageResource(R.drawable.circle_white_background)
        if (length > 2) image_pin_three.setImageResource(R.drawable.circle_white_background)
        if (length > 3) image_pin_four.setImageResource(R.drawable.circle_white_background)

        if (length < 4) image_pin_four.setImageResource(R.drawable.circle_stroke_background)
        if (length < 3) image_pin_three.setImageResource(R.drawable.circle_stroke_background)
        if (length < 2) image_pin_two.setImageResource(R.drawable.circle_stroke_background)
        if (length < 1) image_pin_one.setImageResource(R.drawable.circle_stroke_background)
    }

    override fun showMessagePinCorrect() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun showErrorWrongPin() {
        Toast.makeText(this, getString(R.string.activity_pin_wrong_pin), Toast.LENGTH_SHORT).show()
        layout_pins.animateShake()
    }

    override fun showErrorPinsDoNotMatch() {
        Toast.makeText(this, getString(R.string.activity_pin_do_not_match), Toast.LENGTH_SHORT).show()
        layout_pins.animateShake()
    }

    override fun showMessageRepeatPin() {
        text_pin_message.text = getString(R.string.activity_pin_repeat)
    }

    override fun showMessageEnterPin() {
        text_pin_message.text = getString(R.string.activity_pin_enter)
    }

    override fun showMessagePinCreated() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun showMessageCreatePin() {
        text_pin_message.text = getString(R.string.activity_pin_create_pin)
    }

    override fun showMessageCurrentPin() {
        text_pin_message.text = getString(R.string.activity_pin_enter_current_pin)
    }

    override fun showForgotPinView() {
        analytics.sendEvent(MyAnalytics.EVENT_FORGOT_PIN)
        if (supportFragmentManager.findFragmentByTag(SendEmailFragment.TAG) == null) {
            supportFragmentManager.inTransaction {
                add(R.id.pin_sheet_container, SendEmailFragment(), SendEmailFragment.TAG)
            }
        }
        mHandler.postDelayed(mBottomSheetOpenRunnable, BOTTOM_SHEET_EXPAND_DELAY)
    }

    override fun showEnterEmailView() {
        if (supportFragmentManager.findFragmentByTag(BackupEmailFragment.TAG) == null) {
            supportFragmentManager.inTransaction {
                add(R.id.pin_sheet_container, BackupEmailFragment(), BackupEmailFragment.TAG)
            }
        }
        mHandler.postDelayed(mBottomSheetOpenRunnable, BOTTOM_SHEET_EXPAND_DELAY)
    }

    override fun onEmailEntered(email: String) {
        presenter.onEmailEntered(email)
    }

    override fun showFingerprintScanner() {
        mBiometricPrompt.authenticate(BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.activity_pin_fingerprint_title))
                .setNegativeButtonText(getString(R.string.activity_pin_use_pin))
                .build())
    }

    override fun hideFingerprintButton() {
        button_fingerprint.visibility = View.INVISIBLE
    }

    override fun showFingerprintButton() {
        button_fingerprint.visibility = View.VISIBLE
    }

    override fun closeView() {
        finish()
    }

    override fun closeApp() {
        moveTaskToBack(true)
    }

    override fun closeBottomSheet() {
        mBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    override fun onBackPressed() {
        if (mMode == MODE_LOCK && mBottomSheet.state == BottomSheetBehavior.STATE_EXPANDED) {
            closeBottomSheet()
        } else if (mMode == MODE_LOCK) {
            presenter.onButtonCloseClickModeLock()
        } else {
            super.onBackPressed()
        }
    }

    override fun onStart() {
        super.onStart()
        mBottomSheet.addBottomSheetCallback(mBottomSheetCallback)
        presenter.attachView(this)
        when (mMode) {
            MODE_CREATE -> presenter.onViewStartedModeCreate()
            MODE_REMOVE -> presenter.onViewStartedModeRemove()
            MODE_LOCK -> presenter.onViewStartedModeLock()
        }
    }

    override fun onStop() {
        super.onStop()
        presenter.detachView()
        mHandler.removeCallbacks(mBottomSheetOpenRunnable)
        mBottomSheet.removeBottomSheetCallback(mBottomSheetCallback)
    }

    override fun onPause() {
        super.onPause()
        window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }
}
