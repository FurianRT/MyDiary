package com.furianrt.mydiary.screens.pin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import com.furianrt.mydiary.R
import com.furianrt.mydiary.base.BaseActivity
import com.furianrt.mydiary.general.LockableBottomSheetBehavior
import com.furianrt.mydiary.screens.pin.PinActivity.Mode.*
import com.furianrt.mydiary.screens.pin.fragments.backupemail.BackupEmailFragment
import com.furianrt.mydiary.screens.pin.fragments.done.DoneEmailFragment
import com.furianrt.mydiary.screens.pin.fragments.sendemail.SendEmailFragment
import com.furianrt.mydiary.utils.animateShake
import com.furianrt.mydiary.utils.inTransaction
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.activity_pin.*
import kotlinx.android.synthetic.main.bottom_sheet_pin.*
import javax.inject.Inject

class PinActivity : BaseActivity(), PinContract.View, View.OnClickListener,
        BackupEmailFragment.OnBackupEmailFragmentListener {

    companion object {
        private const val ANIMATION_SHAKE_DURATION = 400L
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
    lateinit var mPresenter: PinContract.Presenter

    private enum class Mode { MODE_CREATE, MODE_REMOVE, MODE_LOCK }

    private lateinit var mBottomSheet: LockableBottomSheetBehavior<MaterialCardView>
    private lateinit var mMode: Mode
    private val mHandler = Handler()
    private val mBottomSheetOpenRunnable: Runnable = Runnable {
        mBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override var needLockScreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(this).inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin)

        mMode = intent.getSerializableExtra(EXTRA_MODE) as Mode

        mBottomSheet = BottomSheetBehavior.from(pin_sheet_container) as LockableBottomSheetBehavior

        if (mMode == MODE_CREATE || mMode == MODE_REMOVE) {
            button_forgot_pin.visibility = View.INVISIBLE
            mBottomSheet.locked = true
        } else {
            mBottomSheet.locked = false
        }

        mBottomSheet.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    supportFragmentManager.findFragmentByTag(SendEmailFragment.TAG)?.let {
                        supportFragmentManager.inTransaction { remove(it) }
                    }
                    supportFragmentManager.findFragmentByTag(DoneEmailFragment.TAG)?.let {
                        supportFragmentManager.inTransaction { remove(it) }
                    }
                }
            }
        })

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
        button_forgot_pin.setOnClickListener(this)
        button_pin_close.setOnClickListener(this)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt(BUNDLE_BOTTOM_SHEET_STATE, mBottomSheet.state)
        mPresenter.onSaveInstanceState(outState)
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
        layout_pins.animateShake(ANIMATION_SHAKE_DURATION)
    }

    override fun showErrorPinsDoNotMatch() {
        Toast.makeText(this, getString(R.string.activity_pin_do_not_match), Toast.LENGTH_SHORT).show()
        layout_pins.animateShake(ANIMATION_SHAKE_DURATION)
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

    override fun onClick(v: View) {
        when (v.id) {
            R.id.button_one -> valueEntered(1)
            R.id.button_two -> valueEntered(2)
            R.id.button_three -> valueEntered(3)
            R.id.button_four -> valueEntered(4)
            R.id.button_five -> valueEntered(5)
            R.id.button_six -> valueEntered(6)
            R.id.button_seven -> valueEntered(7)
            R.id.button_eight -> valueEntered(8)
            R.id.button_nine -> valueEntered(9)
            R.id.button_zero -> valueEntered(0)
            R.id.button_backspace -> mPresenter.onButtonBackspaceClick()
            R.id.button_forgot_pin -> mPresenter.onButtonForgotPinClick()
            R.id.button_pin_close -> mPresenter.onButtonCloseClick()
        }
    }

    private fun valueEntered(value: Int) {
        when (mMode) {
            MODE_CREATE -> mPresenter.onValueEnteredModeCreate(value)
            MODE_REMOVE -> mPresenter.onValueEnteredModeRemove(value)
            MODE_LOCK -> mPresenter.onValueEnteredModeLock(value)
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
        if (mBottomSheet.state == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            close()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_stay_slide_bottom, R.anim.slide_bottom_down)
    }

    override fun onResume() {
        super.onResume()
        mPresenter.attachView(this)
        when (mMode) {
            MODE_CREATE -> mPresenter.onViewResumedModeCreate()
            MODE_REMOVE -> mPresenter.onViewResumedModeRemove()
            MODE_LOCK -> mPresenter.onViewResumedModeLock()
        }
    }

    override fun onPause() {
        super.onPause()
        mHandler.removeCallbacks(mBottomSheetOpenRunnable)
        mPresenter.detachView()
    }
}
