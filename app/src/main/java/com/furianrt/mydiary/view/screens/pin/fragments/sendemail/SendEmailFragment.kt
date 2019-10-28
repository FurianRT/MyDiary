/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.screens.pin.fragments.sendemail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.furianrt.mydiary.R
import com.furianrt.mydiary.view.base.BaseFragment
import com.furianrt.mydiary.view.screens.pin.fragments.done.DoneEmailFragment
import com.furianrt.mydiary.utils.animateShake
import com.furianrt.mydiary.utils.inTransaction
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.bottom_sheet_pin.*
import kotlinx.android.synthetic.main.fragment_send_email.*
import javax.inject.Inject

class SendEmailFragment : BaseFragment(R.layout.fragment_send_email), SendEmailContract.MvpView {

    companion object {
        const val TAG = "SendEmailFragment"
        private const val CLOSE_AFTER_DONE_DELAY = 2000L
    }

    @Inject
    lateinit var mPresenter: SendEmailContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button_send_email.setOnClickListener { mPresenter.onButtonSendClick() }
        button_send_email_cancel.setOnClickListener { mPresenter.onButtonCancelClick() }
        layout_loading.setOnTouchListener { _, _ -> true }
    }

    override fun showDoneView() {
        if (fragmentManager?.findFragmentByTag(DoneEmailFragment.TAG) == null) {
            fragmentManager?.inTransaction {
                val message = getString(R.string.fragment_send_email_done)
                setCustomAnimations(R.anim.scale_up, R.anim.scale_up)
                add(R.id.pin_sheet_container, DoneEmailFragment.newInstance(message), DoneEmailFragment.TAG)
            }
        }

        activity?.pin_sheet_container?.postDelayed({
            activity?.let {
                BottomSheetBehavior.from(it.pin_sheet_container).state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }, CLOSE_AFTER_DONE_DELAY)
    }

    override fun showErrorMessageSend() {
        button_send_email.animateShake()
        Toast.makeText(requireContext(), getString(R.string.fragment_send_email_error), Toast.LENGTH_SHORT).show()
    }

    override fun showErrorNetworkConnection() {
        Toast.makeText(requireContext(), getString(R.string.network_error), Toast.LENGTH_SHORT).show()
    }

    override fun showLoading() {
        layout_loading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        layout_loading.visibility = View.INVISIBLE
    }

    override fun closeView() {
        BottomSheetBehavior.from(requireActivity().pin_sheet_container).state =
                BottomSheetBehavior.STATE_COLLAPSED
    }

    override fun onStart() {
        super.onStart()
        mPresenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        mPresenter.detachView()
    }
}
