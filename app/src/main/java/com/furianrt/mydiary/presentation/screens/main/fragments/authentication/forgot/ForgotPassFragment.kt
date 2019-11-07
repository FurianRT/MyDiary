/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.main.fragments.authentication.forgot

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.furianrt.mydiary.R
import com.furianrt.mydiary.presentation.base.BaseFragment
import com.furianrt.mydiary.presentation.screens.main.fragments.authentication.AuthFragment
import com.furianrt.mydiary.presentation.screens.main.fragments.authentication.done.DoneAuthFragment
import com.furianrt.mydiary.utils.animateShake
import com.furianrt.mydiary.utils.hideKeyboard
import com.furianrt.mydiary.utils.inTransaction
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.bottom_sheet_main.*
import kotlinx.android.synthetic.main.fragment_forgot_pass.*
import javax.inject.Inject

class ForgotPassFragment : BaseFragment(R.layout.fragment_forgot_pass), ForgotPassContract.MvpView {

    companion object {
        const val TAG = "ForgotPassFragment"
        private const val ARG_EMAIL = "email"
        private const val CLOSE_AFTER_DONE_DELAY = 2000L
        private const val CHANGE_ACTIVITY_FLAG_DELAY = 200L

        @JvmStatic
        fun newInstance(email: String) =
                ForgotPassFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_EMAIL, email)
                    }
                }
    }

    @Inject
    lateinit var mPresenter: ForgotPassContract.Presenter

    private val mHandler = Handler()
    private val mChangeActivityFlag: Runnable = Runnable {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }
    private val mOnEditFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
        if (hasFocus) {
            (parentFragment as? AuthFragment?)?.pushContainerUp()
            mHandler.postDelayed(mChangeActivityFlag, CHANGE_ACTIVITY_FLAG_DELAY)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let { edit_email.setText(it.getString(ARG_EMAIL, "")) }

        button_forgot_cancel.setOnClickListener { mPresenter.onButtonCancelClick() }
        button_send.setOnClickListener {
            mPresenter.onButtonSendClick(edit_email.text?.toString() ?: "")
        }
        edit_email.onFocusChangeListener = mOnEditFocusChangeListener
        edit_email.setOnClickListener { mOnEditFocusChangeListener.onFocusChange(it, true) }
    }

    override fun showErrorEmailFormat() {
        layout_email.animateShake()
        Toast.makeText(requireContext(), getString(R.string.wrong_email_format), Toast.LENGTH_SHORT).show()
    }

    override fun showErrorEmptyEmail() {
        layout_email.animateShake()
        Toast.makeText(requireContext(), getString(R.string.empty_email), Toast.LENGTH_SHORT).show()
    }

    override fun showErrorNetworkConnection() {
        Toast.makeText(requireContext(), getString(R.string.network_error), Toast.LENGTH_SHORT).show()
    }

    override fun showEmailSent() {
        if (fragmentManager?.findFragmentByTag(DoneAuthFragment.TAG) == null) {
            fragmentManager?.inTransaction {
                val message = getString(R.string.fragment_done_email_sent)
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

    override fun showLoading() {
        view_alpha.visibility = View.VISIBLE
        progress_send.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        view_alpha.visibility = View.GONE
        progress_send.visibility = View.GONE
    }

    override fun close() {
        fragmentManager?.popBackStack()
    }

    override fun onDetach() {
        super.onDetach()
        (parentFragment as? AuthFragment?)?.showRegistrationButton()
        activity?.currentFocus?.hideKeyboard()
        activity?.currentFocus?.clearFocus()
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
}