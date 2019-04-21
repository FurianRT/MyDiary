package com.furianrt.mydiary.main.fragments.premium

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.furianrt.mydiary.R
import com.furianrt.mydiary.main.MainActivity
import kotlinx.android.synthetic.main.fragment_premium.view.*
import javax.inject.Inject

class PremiumFragment : Fragment(), PremiumContract.View {

    companion object {
        const val TAG = "PremiumFragment"
    }

    @Inject
    lateinit var mPresenter: PremiumContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_premium, container, false)

        view.button_premium_close.setOnClickListener { mPresenter.onButtonCloseClick() }

        return view
    }

    override fun onResume() {
        super.onResume()
        mPresenter.attachView(this)
    }

    override fun onPause() {
        super.onPause()
        mPresenter.detachView()
    }

    override fun close() {
        (activity as? MainActivity?)?.closeBottomSheet()
    }
}