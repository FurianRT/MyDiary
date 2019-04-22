package com.furianrt.mydiary.pin

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.furianrt.mydiary.R
import com.furianrt.mydiary.pin.fragments.email.BackupEmailFragment
import com.furianrt.mydiary.utils.animateShake
import com.furianrt.mydiary.utils.inTransaction
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yanzhenjie.album.mvp.BaseActivity
import kotlinx.android.synthetic.main.activity_pin.*
import kotlinx.android.synthetic.main.bottom_sheet_pin.*
import javax.inject.Inject

class PinActivity : BaseActivity(), PinContract.View, View.OnClickListener,
        BackupEmailFragment.OnBackupEmailFragmentListener {

    companion object {
        private const val SHAKE_DURATION = 400L
        private const val BOTTOM_SHEET_EXPAND_DELAY = 200L
        private const val BUNDLE_BOTTOM_SHEET_STATE = "bottomSheetState"
        const val MODE_CREATE = 0
        const val MODE_REMOVE = 1
        const val MODE_LOCK = 2
        const val EXTRA_MODE = "mode"
    }

    @Inject
    lateinit var mPresenter: PinContract.Presenter

    private lateinit var mBottomSheet: BottomSheetBehavior<CardView>
    private var mMode = MODE_CREATE
    private val mHandler = Handler()
    private val mBottomSheetOpenRunnable: Runnable = Runnable {
        mBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(this).inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin)

        mMode = intent.getIntExtra(EXTRA_MODE, MODE_CREATE)
        mPresenter.setMode(mMode)

        mBottomSheet = BottomSheetBehavior.from(pin_sheet_container)

        savedInstanceState?.let {
            mBottomSheet.state = it.getInt(BUNDLE_BOTTOM_SHEET_STATE, BottomSheetBehavior.STATE_COLLAPSED)
            mPresenter.onRestoreInstanceState(it)
        }

        button_one.setOnClickListener(this)
        button_two.setOnClickListener(this)
        button_three.setOnClickListener(this)
        button_four.setOnClickListener(this)
        button_five.setOnClickListener(this)
        button_six.setOnClickListener(this)
        button_seven.setOnClickListener(this)
        button_eight.setOnClickListener(this)
        button_nine.setOnClickListener(this)
        button_zero.setOnClickListener(this)
        button_backspace.setOnClickListener(this)
        button_forgot_password.setOnClickListener(this)
        button_pin_close.setOnClickListener(this)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt(BUNDLE_BOTTOM_SHEET_STATE, mBottomSheet.state)
        mPresenter.onSaveInstanceState(outState)
    }

    override fun showPassword(password: String) {
        val length = password.length
        if (length > 0) image_pin_one.setImageResource(R.drawable.circle_white_background)
        if (length > 1) image_pin_two.setImageResource(R.drawable.circle_white_background)
        if (length > 2) image_pin_three.setImageResource(R.drawable.circle_white_background)
        if (length > 3) image_pin_four.setImageResource(R.drawable.circle_white_background)

        if (length < 4) image_pin_four.setImageResource(R.drawable.cirlcle_stroke_background)
        if (length < 3) image_pin_three.setImageResource(R.drawable.cirlcle_stroke_background)
        if (length < 2) image_pin_two.setImageResource(R.drawable.cirlcle_stroke_background)
        if (length < 1) image_pin_one.setImageResource(R.drawable.cirlcle_stroke_background)
    }

    override fun showMessagePinCorrect() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun showErrorWrongPin() {
        Toast.makeText(this, getString(R.string.activity_pin_wrong_pin), Toast.LENGTH_SHORT).show()
        layout_pins.animateShake(SHAKE_DURATION)
    }

    override fun showErrorPinsDoNotMatch() {
        Toast.makeText(this, getString(R.string.activity_pin_dont_match), Toast.LENGTH_SHORT).show()
        layout_pins.animateShake(SHAKE_DURATION)
    }

    override fun showMessageRepeatPassword() {
        text_pin_message.text = getString(R.string.activity_pin_repeat)
    }

    override fun showMessageEnterPassword() {
        text_pin_message.text = getString(R.string.activity_pin_enter)
    }

    override fun showMessagePasswordCreated() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun showMessageCreatePassword() {
        text_pin_message.text = getString(R.string.activity_pin_create_pin)
    }

    override fun showMessageCurrentPassword() {
        text_pin_message.text = getString(R.string.activity_pin_enter_current_pin)
    }

    override fun showForgotPinView() {

    }

    override fun showEnterEmailView() {
        if (supportFragmentManager.findFragmentByTag(BackupEmailFragment.TAG) == null) {
            supportFragmentManager.inTransaction {
                add(R.id.pin_sheet_container, BackupEmailFragment(), BackupEmailFragment.TAG)
            }
        }
        mHandler.postDelayed(mBottomSheetOpenRunnable, BOTTOM_SHEET_EXPAND_DELAY)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.button_one -> mPresenter.onValueEntered(1)
            R.id.button_two -> mPresenter.onValueEntered(2)
            R.id.button_three -> mPresenter.onValueEntered(3)
            R.id.button_four -> mPresenter.onValueEntered(4)
            R.id.button_five -> mPresenter.onValueEntered(5)
            R.id.button_six -> mPresenter.onValueEntered(6)
            R.id.button_seven -> mPresenter.onValueEntered(7)
            R.id.button_eight -> mPresenter.onValueEntered(8)
            R.id.button_nine -> mPresenter.onValueEntered(9)
            R.id.button_zero -> mPresenter.onValueEntered(0)
            R.id.button_backspace -> mPresenter.onButtonBackspaceClick()
            R.id.button_forgot_password -> mPresenter.onButtonForgotPasswordClick()
            R.id.button_pin_close -> mPresenter.onButtonCloseClick()
        }
    }

    override fun onEmailEntered(email: String) {
        mPresenter.onEmailEntered(email)
    }

    override fun close() {
        if (mMode == MODE_LOCK) {
            moveTaskToBack(true)
        } else {
            finish()
        }
    }

    override fun onBackPressed() {
        close()
    }

    override fun onResume() {
        super.onResume()
        mPresenter.attachView(this)
        mPresenter.onViewResumed()
    }

    override fun onPause() {
        super.onPause()
        mHandler.removeCallbacks(mBottomSheetOpenRunnable)
        mPresenter.detachView()
    }
}
