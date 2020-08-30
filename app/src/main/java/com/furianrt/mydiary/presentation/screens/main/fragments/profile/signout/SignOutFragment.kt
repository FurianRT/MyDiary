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

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.furianrt.mydiary.R
import com.furianrt.mydiary.presentation.base.BaseFragment
import com.furianrt.mydiary.presentation.screens.main.MainBottomSheetHolder
import kotlinx.android.synthetic.main.fragment_sign_out.*
import javax.inject.Inject

class SignOutFragment : BaseFragment(R.layout.fragment_sign_out), SignOutContract.View {

    companion object {
        const val TAG = "SignOutFragment"
        private const val CLOSE_DELAY = 500L
    }

    @Inject
    lateinit var presenter: SignOutContract.Presenter

    private var mListener: MainBottomSheetHolder? = null
    private val mHandler = Handler()
    private val mBottomSheetCloseRunnable: Runnable = Runnable { mListener?.closeBottomSheet() }

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button_sign_out.setOnClickListener { presenter.onButtonSignOutClick() }
        button_sign_out_cancel.setOnClickListener { presenter.onButtonSignOutCancelClick() }
    }

    override fun close() {
        mHandler.postDelayed(mBottomSheetCloseRunnable, CLOSE_DELAY)
    }

    override fun returnToMenuView() {
        parentFragmentManager.popBackStack()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainBottomSheetHolder) {
            mListener = context
        } else {
            throw IllegalStateException()
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onStart() {
        super.onStart()
        presenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        mHandler.removeCallbacks(mBottomSheetCloseRunnable)
        presenter.detachView()
    }
}
