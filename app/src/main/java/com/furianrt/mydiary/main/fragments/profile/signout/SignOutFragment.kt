package com.furianrt.mydiary.main.fragments.profile.signout

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.furianrt.mydiary.R
import com.furianrt.mydiary.main.MainActivity
import kotlinx.android.synthetic.main.fragment_sign_out.view.*
import javax.inject.Inject

class SignOutFragment : Fragment(), SignOutContract.View {

    companion object {
        const val TAG = "SignOutFragment"
        private const val CLOSE_DELAY = 500L
    }

    @Inject
    lateinit var mPresenter: SignOutContract.Presenter

    private var mListener: OnSignOutFragmentInteractionListener? = null
    private val mHandler = Handler()
    private val mBottomSheetCloseRunnable: Runnable = Runnable {
        (activity as? MainActivity?)?.closeBottomSheet()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(context!!).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sign_out, container, false)

        view.button_sign_out.setOnClickListener {
            mListener?.onSignOut()
            mPresenter.onButtonSignOutClick()
        }
        view.button_sign_out_cancel.setOnClickListener { mPresenter.onButtonSignOutCancelClick() }

        return view
    }

    override fun close() {
        mHandler.postDelayed(mBottomSheetCloseRunnable, CLOSE_DELAY)
    }

    override fun returnToMenuView() {
        fragmentManager?.popBackStack()
    }

    override fun onResume() {
        super.onResume()
        mPresenter.attachView(this)
    }

    override fun onPause() {
        super.onPause()
        mHandler.removeCallbacks(mBottomSheetCloseRunnable)
        mPresenter.detachView()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnSignOutFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context.toString()
                    + " must implement OnSignOutFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface OnSignOutFragmentInteractionListener {

        fun onSignOut()
    }
}