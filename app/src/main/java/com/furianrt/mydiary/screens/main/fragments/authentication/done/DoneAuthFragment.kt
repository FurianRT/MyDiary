package com.furianrt.mydiary.screens.main.fragments.authentication.done

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.furianrt.mydiary.R
import com.furianrt.mydiary.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_done_auth.view.*

class DoneAuthFragment : BaseFragment() {

    companion object {
        const val TAG = "DoneAuthFragment"
        private const val ARG_MESSAGE = "message"

        @JvmStatic
        fun newInstance(message: String) =
                DoneAuthFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_MESSAGE, message)
                    }
                }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_done_auth, container, false)

        view.text_success_message.text = arguments?.getString(ARG_MESSAGE) ?: ""

        return view
    }
}
