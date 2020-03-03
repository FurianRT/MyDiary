/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.pin.fragments.sendemail

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import com.furianrt.mydiary.R
import com.furianrt.mydiary.presentation.base.BaseFragment
import com.furianrt.mydiary.presentation.screens.pin.PinBottomSheetHolder
import com.furianrt.mydiary.presentation.screens.pin.fragments.done.DoneEmailFragment
import com.furianrt.mydiary.utils.animateShake
import com.furianrt.mydiary.utils.inTransaction
import kotlinx.android.synthetic.main.fragment_send_email.*
import javax.inject.Inject

class SendEmailFragment : BaseFragment(R.layout.fragment_send_email), SendEmailContract.View {

    companion object {
        const val TAG = "SendEmailFragment"
        private const val CLOSE_AFTER_DONE_DELAY = 2000L
    }

    @Inject
    lateinit var presenter: SendEmailContract.Presenter

    private var mListener: PinBottomSheetHolder? = null
    private val mHandler = Handler()
    private val mBottomSheetCloseRunnable: Runnable = Runnable { mListener?.closeBottomSheet() }

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button_send_email.setOnClickListener { presenter.onButtonSendClick() }
        button_send_email_cancel.setOnClickListener { presenter.onButtonCancelClick() }
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

        mHandler.postDelayed(mBottomSheetCloseRunnable, CLOSE_AFTER_DONE_DELAY)
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
        mListener?.closeBottomSheet()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is PinBottomSheetHolder) {
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
