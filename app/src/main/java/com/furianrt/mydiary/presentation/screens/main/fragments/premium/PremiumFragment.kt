/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.main.fragments.premium

import android.content.Context
import android.os.Bundle
import android.view.View
import com.furianrt.mydiary.BuildConfig
import com.furianrt.mydiary.R
import com.furianrt.mydiary.presentation.base.BaseFragment
import com.furianrt.mydiary.presentation.screens.main.MainActivity
import kotlinx.android.synthetic.main.fragment_premium.*
import javax.inject.Inject

class PremiumFragment : BaseFragment(R.layout.fragment_premium), PremiumContract.View {

    companion object {
        const val TAG = "PremiumFragment"
    }

    @Inject
    lateinit var presenter: PremiumContract.Presenter

    private var mListener: OnPremiumFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button_premium_close.setOnClickListener { presenter.onButtonCloseClick() }
        button_get_premium.setOnClickListener {
            mListener?.onButtonPurchaseClick(BuildConfig.ITEM_PREMIUM_SKU)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnPremiumFragmentInteractionListener) {
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

    override fun close() {
        (activity as? MainActivity?)?.closeBottomSheet()
    }

    interface OnPremiumFragmentInteractionListener {
        fun onButtonPurchaseClick(productId: String)
    }
}