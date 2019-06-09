package com.furianrt.mydiary.screens.main.fragments.premium

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.furianrt.mydiary.BuildConfig
import com.furianrt.mydiary.R
import com.furianrt.mydiary.screens.main.MainActivity
import kotlinx.android.synthetic.main.fragment_premium.view.*
import javax.inject.Inject

class PremiumFragment : Fragment(), PremiumContract.View {

    companion object {
        const val TAG = "PremiumFragment"
    }

    @Inject
    lateinit var mPresenter: PremiumContract.Presenter

    private var mListener: OnPremiumFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_premium, container, false)

        view.button_premium_close.setOnClickListener { mPresenter.onButtonCloseClick() }
        view.button_get_premium.setOnClickListener {
            //if (BuildConfig.DEBUG) {
            //    mListener?.onButtonPurshaseClick(BaseActivity.ITEM_TEST_SKU)
           // } else {
                mListener?.onButtonPurshaseClick(BuildConfig.ITEM_SYNC_SKU)
           // }
        }

        return view
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
        mPresenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        mPresenter.detachView()
    }

    override fun close() {
        (activity as? MainActivity?)?.closeBottomSheet()
    }

    interface OnPremiumFragmentInteractionListener {
        fun onButtonPurshaseClick(productId: String)
    }
}