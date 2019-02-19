package com.furianrt.mydiary.main.fragments.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.furianrt.mydiary.R
import kotlinx.android.synthetic.main.fragment_profile.view.*
import javax.inject.Inject

class ProfileFragment : Fragment(), ProfileContract.View {

    companion object {
        const val TAG = "ProfileFragment"
    }

    @Inject
    lateinit var mPresenter: ProfileContract.Presenter

    private var mListener: OnProfileFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(context!!).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        view.button_sign_out.setOnClickListener { mPresenter.onButtonSignOutClick() }

        return view
    }

    override fun showSignOut() {
        mListener?.onSignOut()
    }

    override fun onStart() {
        super.onStart()
        mPresenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        mPresenter.detachView()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnProfileFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context.toString()
                    + " must implement OnProfileFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface OnProfileFragmentInteractionListener {

        fun onSignOut()
    }
}