/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.note.fragments.mainnote.reminder

import android.os.Bundle
import android.view.View

import com.furianrt.mydiary.R
import com.furianrt.mydiary.presentation.base.BaseFragment

class ReminderFragment : BaseFragment(R.layout.fragment_reminder) {

    companion object {
        const val TAG = "ReminderFragment"

        @JvmStatic
        fun newInstance() =
                ReminderFragment().apply {
                    arguments = Bundle().apply {

                    }
                }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}
