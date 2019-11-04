/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.pin.fragments.done

import android.os.Bundle
import android.view.View
import com.furianrt.mydiary.R
import com.furianrt.mydiary.presentation.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_done_email.*

class DoneEmailFragment : BaseFragment(R.layout.fragment_done_email) {

    companion object {
        const val TAG = "DoneEmailFragment"
        private const val ARG_MESSAGE = "message"

        @JvmStatic
        fun newInstance(message: String) =
                DoneEmailFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_MESSAGE, message)
                    }
                }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        text_success_message.text = requireArguments().getString(ARG_MESSAGE, "")
    }
}
