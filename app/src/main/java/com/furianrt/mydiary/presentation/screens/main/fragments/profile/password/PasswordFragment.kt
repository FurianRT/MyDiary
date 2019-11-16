/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.main.fragments.profile.password

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import com.furianrt.mydiary.R
import com.furianrt.mydiary.analytics.MyAnalytics
import com.furianrt.mydiary.presentation.base.BaseFragment
import com.furianrt.mydiary.presentation.screens.main.fragments.profile.ProfileFragment
import com.furianrt.mydiary.presentation.screens.main.fragments.profile.password.success.PasswordSuccessFragment
import com.furianrt.mydiary.utils.animateShake
import com.furianrt.mydiary.utils.hideKeyboard
import com.furianrt.mydiary.utils.inTransaction
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.bottom_sheet_main.*
import kotlinx.android.synthetic.main.fragment_password.*
import javax.inject.Inject

class PasswordFragment : BaseFragment(R.layout.fragment_password), PasswordContract.View {

    companion object {
        const val TAG = "PasswordFragment"
        private const val CLOSE_AFTER_DONE_DELAY = 2000L
        private const val CHANGE_ACTIVITY_FLAG_DELAY = 200L
    }

    @Inject
    lateinit var mPresenter: PasswordContract.Presenter

    private val mHandler = Handler()
    private val mChangeActivityFlag: Runnable = Runnable {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }
    private val mOnEditFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
        if (hasFocus) {
            (parentFragment as ProfileFragment).pushContainerUp()
            mHandler.postDelayed(mChangeActivityFlag, CHANGE_ACTIVITY_FLAG_DELAY)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button_password_cancel.setOnClickListener { mPresenter.onButtonCancelClick() }
        button_password_save.setOnClickListener {
            val oldPassword = edit_old_password.text?.toString() ?: ""
            val newPassword = edit_new_password.text?.toString() ?: ""
            val repeatPassword = edit_password_repeat.text?.toString() ?: ""
            mPresenter.onButtonSaveClick(oldPassword, newPassword, repeatPassword)
        }
        edit_old_password.onFocusChangeListener = mOnEditFocusChangeListener
        edit_new_password.onFocusChangeListener = mOnEditFocusChangeListener
        edit_password_repeat.onFocusChangeListener = mOnEditFocusChangeListener
        edit_old_password.setOnClickListener { mOnEditFocusChangeListener.onFocusChange(it, true) }
        edit_new_password.setOnClickListener { mOnEditFocusChangeListener.onFocusChange(it, true) }
        edit_password_repeat.setOnClickListener { mOnEditFocusChangeListener.onFocusChange(it, true) }
        view_alpha.setOnTouchListener { _, _ -> true }
    }

    override fun showLoading() {
        view_alpha.visibility = View.VISIBLE
        progress_change_password.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        view_alpha.visibility = View.GONE
        progress_change_password.visibility = View.INVISIBLE
    }

    override fun showErrorEmptyOldPassword() {
        layout_old_password.animateShake()
        text_error.text = getString(R.string.fragment_password_enter_old_password)
    }

    override fun showErrorEmptyNewPassword() {
        layout_new_password.animateShake()
        text_error.text = getString(R.string.fragment_password_enter_new_password)
    }

    override fun showErrorEmptyRepeatPassword() {
        layout_password_repeat.animateShake()
        text_error.text = getString(R.string.fragment_registration_repeat_password)
    }

    override fun showErrorWrongOldPassword() {
        layout_old_password.animateShake()
        text_error.text = getString(R.string.wrong_password)
    }

    override fun showErrorWrongPasswordRepeat() {
        layout_new_password.animateShake()
        layout_password_repeat.animateShake()
        text_error.text = getString(R.string.fragment_registration_password_error)
    }

    override fun showErrorNetworkConnection() {
        text_error.text = getString(R.string.network_error)
    }

    override fun showErrorShortNewPassword() {
        layout_new_password.animateShake()
        text_error.text = getString(R.string.fragment_registration_error_short_password)
    }

    override fun showSuccessPasswordChange() {
        analytics.sendEvent(MyAnalytics.EVENT_PASSWORD_CHANGED)
        fragmentManager?.let {
            if (it.findFragmentByTag(PasswordSuccessFragment.TAG) == null) {
                it.inTransaction {
                    setCustomAnimations(R.anim.scale_up, R.anim.scale_up)
                    add(R.id.profile_container, PasswordSuccessFragment(), PasswordSuccessFragment.TAG)
                }
            }
        }
        activity?.main_sheet_container?.postDelayed({
            activity?.let {
                BottomSheetBehavior.from(it.main_sheet_container).state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }, CLOSE_AFTER_DONE_DELAY)
    }

    override fun clearErrorMessage() {
        text_error.text = ""
    }

    override fun returnToMenuView() {
        fragmentManager?.popBackStack()
    }

    override fun close() {
        BottomSheetBehavior.from(requireActivity().main_sheet_container).state =
                BottomSheetBehavior.STATE_COLLAPSED
    }

    override fun onStart() {
        super.onStart()
        mPresenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        mHandler.removeCallbacks(mChangeActivityFlag)
        mPresenter.detachView()
    }

    override fun onDetach() {
        super.onDetach()
        activity?.currentFocus?.hideKeyboard()
        activity?.currentFocus?.clearFocus()
    }
}
