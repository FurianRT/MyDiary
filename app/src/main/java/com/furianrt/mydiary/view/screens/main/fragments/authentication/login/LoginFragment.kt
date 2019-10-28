/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.screens.main.fragments.authentication.login

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.furianrt.mydiary.R
import com.furianrt.mydiary.analytics.MyAnalytics
import com.furianrt.mydiary.view.base.BaseFragment
import com.furianrt.mydiary.view.screens.main.fragments.authentication.AuthFragment
import com.furianrt.mydiary.view.screens.main.fragments.authentication.done.DoneAuthFragment
import com.furianrt.mydiary.view.screens.main.fragments.authentication.forgot.ForgotPassFragment
import com.furianrt.mydiary.utils.animateShake
import com.furianrt.mydiary.utils.hideKeyboard
import com.furianrt.mydiary.utils.inTransaction
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.bottom_sheet_main.*
import kotlinx.android.synthetic.main.fragment_login.*
import javax.inject.Inject

class LoginFragment : BaseFragment(R.layout.fragment_login), LoginContract.MvpView {

    companion object {
        const val TAG = "LoginFragment"
        private const val CLOSE_AFTER_DONE_DELAY = 2000L
        private const val CHANGE_ACTIVITY_FLAG_DELAY = 200L
    }

    @Inject
    lateinit var mPresenter: LoginContract.Presenter

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
        button_forgot_password.setOnClickListener {
            (parentFragment as AuthFragment).hideRegistrationButton()
            mPresenter.onButtonForgotClick(edit_email.text?.toString() ?: "")
        }
        button_sing_in.setOnClickListener {
            mPresenter.onButtonSignInClick(
                    edit_email.text?.toString() ?: "",
                    edit_password.text?.toString() ?: ""
            )
        }
        edit_email.onFocusChangeListener = mOnEditFocusChangeListener
        edit_password.onFocusChangeListener = mOnEditFocusChangeListener
        edit_email.setOnClickListener { mOnEditFocusChangeListener.onFocusChange(it, true) }
        edit_password.setOnClickListener { mOnEditFocusChangeListener.onFocusChange(it, true) }
        view_alpha.setOnTouchListener { _, _ -> true }
    }

    override fun showErrorNetworkConnection() {
        image_logo.setImageResource(R.drawable.image_disconnected)
        Toast.makeText(requireContext(), getString(R.string.network_error), Toast.LENGTH_SHORT).show()
    }

    override fun showErrorEmptyPassword() {
        layout_password.animateShake()
        Toast.makeText(requireContext(), getString(R.string.empty_password), Toast.LENGTH_SHORT).show()
    }

    override fun showErrorEmptyEmail() {
        layout_email.animateShake()
        Toast.makeText(requireContext(), getString(R.string.empty_email), Toast.LENGTH_SHORT).show()
    }

    override fun showErrorWrongCredential() {
        layout_password.animateShake()
        layout_email.animateShake()
        Toast.makeText(requireContext(), getString(R.string.fragment_login_wrong_credential), Toast.LENGTH_SHORT).show()
    }

    override fun showLoading() {
        image_logo.setImageResource(R.drawable.diary_logo)
        view_alpha.visibility = View.VISIBLE
        progress_sign_in.visibility = View.VISIBLE
        (parentFragment as? AuthFragment?)?.enableSignUpButton(false)
    }

    override fun hideLoading() {
        view_alpha.visibility = View.GONE
        progress_sign_in.visibility = View.INVISIBLE
        (parentFragment as? AuthFragment?)?.enableSignUpButton(true)
    }

    override fun showLoginSuccess() {
        analytics.sendEvent(MyAnalytics.EVENT_SIGNED_IN)
        if (fragmentManager?.findFragmentByTag(DoneAuthFragment.TAG) == null) {
            fragmentManager?.inTransaction {
                val message = getString(R.string.fragment_done_auth_done)
                setCustomAnimations(R.anim.scale_up, R.anim.scale_up)
                add(R.id.auth_container, DoneAuthFragment.newInstance(message), DoneAuthFragment.TAG)
            }
        }

        activity?.main_sheet_container?.postDelayed({
            activity?.let {
                BottomSheetBehavior.from(it.main_sheet_container).state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }, CLOSE_AFTER_DONE_DELAY)
    }

    override fun showForgotPassView(email: String) {
        analytics.sendEvent(MyAnalytics.EVENT_FORGOT_PASSWORD)
        if (fragmentManager?.findFragmentByTag(ForgotPassFragment.TAG) == null) {
            activity?.supportFragmentManager?.inTransaction {
                setPrimaryNavigationFragment(parentFragment)
            }
            fragmentManager?.inTransaction {
                setCustomAnimations(R.anim.from_right, R.anim.to_left, R.anim.from_left, R.anim.to_right)
                replace(R.id.auth_container, ForgotPassFragment.newInstance(email), ForgotPassFragment.TAG)
                addToBackStack(null)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mPresenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        mHandler.removeCallbacks(mChangeActivityFlag)
        mPresenter.detachView()
        activity?.currentFocus?.hideKeyboard()
        activity?.currentFocus?.clearFocus()
    }
}
