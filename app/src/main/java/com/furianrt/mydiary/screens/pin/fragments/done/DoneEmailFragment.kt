package com.furianrt.mydiary.screens.pin.fragments.done

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.furianrt.mydiary.R
import kotlinx.android.synthetic.main.fragment_done_email.view.*

class DoneEmailFragment : Fragment() {

    companion object {
        const val TAG = "DoneEmailFragment"
        private const val ARG_MESSSAGE = "message"

        @JvmStatic
        fun newInstance(message: String) =
                DoneEmailFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_MESSSAGE, message)
                    }
                }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_done_email, container, false)

        view.text_success_message.text = arguments?.getString(ARG_MESSSAGE) ?: ""

        return view
    }
}
