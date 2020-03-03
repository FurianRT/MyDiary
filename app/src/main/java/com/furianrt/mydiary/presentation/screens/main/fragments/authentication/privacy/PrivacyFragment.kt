/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.main.fragments.authentication.privacy

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.furianrt.mydiary.R
import com.furianrt.mydiary.analytics.MyAnalytics
import com.furianrt.mydiary.presentation.base.BaseFragment
import com.furianrt.mydiary.presentation.screens.main.MainBottomSheetHolder
import com.furianrt.mydiary.utils.animateShake
import kotlinx.android.synthetic.main.fragment_privacy.*
import javax.inject.Inject

class PrivacyFragment : BaseFragment(R.layout.fragment_privacy), PrivacyContract.View {

    companion object {
        const val TAG = "PrivacyFragment"

        private const val ARG_EMAIL = "email"
        private const val ARG_PASSWORD = "password"

        @JvmStatic
        fun newInstance(email: String, password: String) =
                PrivacyFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_EMAIL, email)
                        putString(ARG_PASSWORD, password)
                    }
                }
    }

    @Inject
    lateinit var presenter: PrivacyContract.Presenter

    private var mListener: MainBottomSheetHolder? = null
    private lateinit var mEmail: String
    private lateinit var mPassword: String

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
        mEmail = arguments?.getString(ARG_EMAIL)!!
        mPassword = arguments?.getString(ARG_PASSWORD)!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button_privacy_accept.setOnClickListener { presenter.onButtonAcceptClick(mEmail, mPassword) }
        button_privacy_cancel.setOnClickListener { presenter.onButtonCancelClick() }
        text_privacy_title.setOnClickListener { presenter.onPrivacyLinkClick() }
        view_alpha.setOnTouchListener { _, _ -> true }
    }

    override fun showLoading() {
        view_alpha.visibility = View.VISIBLE
        progress_privacy.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        view_alpha.visibility = View.GONE
        progress_privacy.visibility = View.GONE
    }

    override fun showMessageSuccessRegistration() {
        analytics.sendEvent(MyAnalytics.EVENT_SIGNED_UP)
        mListener?.closeBottomSheet()
    }

    override fun showErrorNetworkConnection() {
        card_privacy.animateShake()
        Toast.makeText(requireContext(), R.string.network_error, Toast.LENGTH_SHORT).show()
    }

    override fun openLink(link: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
    }

    override fun close() {
        fragmentManager?.popBackStack()
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
        presenter.detachView()
    }
}
