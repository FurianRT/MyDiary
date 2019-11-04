/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.main.fragments.profile.signout

import android.os.Bundle
import android.os.Handler
import android.view.View
import com.furianrt.mydiary.R
import com.furianrt.mydiary.presentation.base.BaseFragment
import com.furianrt.mydiary.presentation.screens.main.MainActivity
import kotlinx.android.synthetic.main.fragment_sign_out.*
import javax.inject.Inject

class SignOutFragment : BaseFragment(R.layout.fragment_sign_out), SignOutContract.MvpView {

    companion object {
        const val TAG = "SignOutFragment"
        private const val CLOSE_DELAY = 500L
    }

    @Inject
    lateinit var mPresenter: SignOutContract.Presenter

    private val mHandler = Handler()
    private val mBottomSheetCloseRunnable: Runnable = Runnable {
        (activity as? MainActivity?)?.closeBottomSheet()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button_sign_out.setOnClickListener { mPresenter.onButtonSignOutClick() }
        button_sign_out_cancel.setOnClickListener { mPresenter.onButtonSignOutCancelClick() }
    }

    override fun close() {
        mHandler.postDelayed(mBottomSheetCloseRunnable, CLOSE_DELAY)
    }

    override fun returnToMenuView() {
        fragmentManager?.popBackStack()
    }

    override fun onStart() {
        super.onStart()
        mPresenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        mHandler.removeCallbacks(mBottomSheetCloseRunnable)
        mPresenter.detachView()
    }
}
