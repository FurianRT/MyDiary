/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.main.fragments.authentication.registration

import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import com.furianrt.mydiary.R
import com.furianrt.mydiary.presentation.base.BaseFragment
import com.furianrt.mydiary.presentation.screens.main.fragments.authentication.AuthFragment
import com.furianrt.mydiary.presentation.screens.main.fragments.authentication.privacy.PrivacyFragment
import com.furianrt.mydiary.utils.*
import kotlinx.android.synthetic.main.fragment_registration.*
import javax.inject.Inject

class RegistrationFragment : BaseFragment(R.layout.fragment_registration), RegistrationContract.MvpView {

    companion object {
        const val TAG = "RegistrationFragment"
        private const val CHANGE_ACTIVITY_FLAG_DELAY = 200L
    }

    @Inject
    lateinit var mPresenter: RegistrationContract.Presenter

    private val mHandler = Handler()
    private val mChangeActivityFlag: Runnable = Runnable {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }
    private val mOnEditFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
        if (hasFocus) {
            (parentFragment as AuthFragment).pushContainerUp()
            mHandler.postDelayed(mChangeActivityFlag, CHANGE_ACTIVITY_FLAG_DELAY)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button_cancel.setOnClickListener { mPresenter.onButtonCancelClick() }
        button_sign_up.setOnClickListener {
            mPresenter.onButtonSignUpClick(
                    edit_email.text.toString(),
                    edit_password.text.toString(),
                    edit_password_repeat.text.toString()
            )
        }
        edit_email.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            mOnEditFocusChangeListener.onFocusChange(v, hasFocus)
            mPresenter.onEmailFocusChange(edit_email.text.toString(), hasFocus)
        }
        edit_password.onFocusChangeListener = mOnEditFocusChangeListener
        edit_password_repeat.onFocusChangeListener = mOnEditFocusChangeListener
        edit_email.setOnClickListener { mOnEditFocusChangeListener.onFocusChange(it, true) }
        edit_password.setOnClickListener { mOnEditFocusChangeListener.onFocusChange(it, true) }
        edit_password_repeat.setOnClickListener { mOnEditFocusChangeListener.onFocusChange(it, true) }
        view_alpha.setOnTouchListener { _, _ -> true }
    }

    override fun showErrorNetworkConnection() {
        text_error.text = getString(R.string.network_error)
    }

    override fun showErrorPassword() {
        layout_password.animateShake()
        layout_password_repeat.animateShake()
        text_error.text = getString(R.string.fragment_registration_password_error)
    }

    override fun showErrorEmailFormat() {
        showEmailError()
        text_error.text = getString(R.string.wrong_email_format)
    }

    override fun showErrorEmailExists() {
        showEmailError()
        text_error.text = getString(R.string.fragment_registration_error_email_exists)
    }

    override fun showMessageCorrectEmail() {
        showEmailSuccess()
    }

    override fun showErrorShortPassword() {
        layout_password.animateShake()
        text_error.text = getString(R.string.fragment_registration_error_short_password)
    }

    override fun showErrorEmptyPassword() {
        layout_password.animateShake()
        text_error.text = getString(R.string.empty_password)
    }

    override fun showErrorEmptyPasswordRepeat() {
        layout_password_repeat.animateShake()
        text_error.text = getString(R.string.empty_password)
    }

    override fun showErrorEmptyEmail() {
        showEmailError()
        layout_email.animateShake()
        text_error.text = getString(R.string.empty_email)
    }

    private fun showEmailError() {
        image_email.setImageResource(R.drawable.ic_error)
        image_email.setColorFilter(requireContext().getColorSupport(R.color.red), PorterDuff.Mode.SRC_IN)
        hideLoadingEmail()
        image_email.visibility = View.VISIBLE
    }

    private fun showEmailSuccess() {
        text_error.text = ""
        image_email.setImageResource(R.drawable.ic_done)
        image_email.setColorFilter(requireContext().getThemeAccentColor(), PorterDuff.Mode.SRC_IN)
        hideLoadingEmail()
        image_email.visibility = View.VISIBLE
    }

    override fun clearEmailMessages() {
        text_error.text = ""
        image_email.visibility = View.GONE
        hideLoadingEmail()
    }

    override fun showLoadingEmail() {
        text_error.text = ""
        image_email.visibility = View.GONE
        progress_email.visibility = View.VISIBLE
    }

    override fun hideLoadingEmail() {
        progress_email.visibility = View.GONE
    }

    override fun showLoading() {
        view_alpha.visibility = View.VISIBLE
        progress_sign_up.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        view_alpha.visibility = View.GONE
        progress_sign_up.visibility = View.GONE
    }

    override fun showPrivacyView(email: String, password: String) {
        activity?.currentFocus?.hideKeyboard()
        activity?.currentFocus?.clearFocus()
        if (fragmentManager?.findFragmentByTag(PrivacyFragment.TAG) == null) {
            fragmentManager?.inTransaction {
                setCustomAnimations(R.anim.from_right, R.anim.to_left, R.anim.from_left, R.anim.to_right)
                replace(R.id.auth_container, PrivacyFragment.newInstance(email, password), PrivacyFragment.TAG)
                addToBackStack(null)
            }
        }
    }

    override fun close() {
        fragmentManager?.popBackStack()
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
        (parentFragment as? AuthFragment?)?.showRegistrationButton()
        activity?.currentFocus?.hideKeyboard()
        activity?.currentFocus?.clearFocus()
    }
}
