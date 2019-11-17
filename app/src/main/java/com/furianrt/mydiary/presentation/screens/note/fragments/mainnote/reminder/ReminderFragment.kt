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
import com.furianrt.mydiary.presentation.screens.note.fragments.mainnote.NoteFragment
import kotlinx.android.synthetic.main.fragment_reminder.*
import javax.inject.Inject

class ReminderFragment : BaseFragment(R.layout.fragment_reminder), ReminderContract.MvpView {

    companion object {
        const val TAG = "ReminderFragment"
        private const val ARG_NOTE_ID = "note_id"

        @JvmStatic
        fun newInstance(noteId: String) =
                ReminderFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_NOTE_ID, noteId)
                    }
                }
    }

    @Inject
    lateinit var presenter: ReminderContract.Presenter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button_reminder_close.setOnClickListener {
            (parentFragment as? NoteFragment?)?.onReminderButtonCloseClick()
        }
    }

    override fun onStart() {
        super.onStart()
        presenter.attachView(this)
        (parentFragment as? NoteFragment?)?.onReminderStart()
    }

    override fun onStop() {
        super.onStop()
        presenter.detachView()
    }
}
