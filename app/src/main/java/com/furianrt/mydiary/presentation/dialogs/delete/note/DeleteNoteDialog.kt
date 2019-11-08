/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.dialogs.delete.note

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.furianrt.mydiary.R
import com.furianrt.mydiary.analytics.MyAnalytics
import com.furianrt.mydiary.presentation.base.BaseDialog
import kotlinx.android.synthetic.main.dialog_delete_note.view.*
import javax.inject.Inject

class DeleteNoteDialog : BaseDialog(), DeleteNoteContract.MvpView {

    companion object {
        const val TAG = "DeleteNoteDialog"
        private const val ARG_NOTES_IDS = "notes_ids"

        @JvmStatic
        fun newInstance(notesIds: List<String>) =
                DeleteNoteDialog().apply {
                    arguments = Bundle().apply {
                        putStringArrayList(ARG_NOTES_IDS, ArrayList(notesIds))
                    }
                }
    }

    @Inject
    lateinit var mPresenter: DeleteNoteContract.Presenter

    private lateinit var mNotesIds: List<String>
    private var mListener: OnDeleteNoteConfirmListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
        mNotesIds = arguments?.getStringArrayList(ARG_NOTES_IDS) ?: emptyList()
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_delete_note, null)

        view.button_note_delete.setOnClickListener {
            analytics.sendEvent(MyAnalytics.EVENT_NOTE_DELETED)
            mPresenter.onButtonDeleteClick(mNotesIds)
            mListener?.onDialogButtonDeleteClick()
        }
        view.button_note_delete_cancel.setOnClickListener { mPresenter.onButtonCancelClick() }
        view.text_note_delete_title.text = resources.getQuantityString(
                R.plurals.note_delete_confirmation,
                mNotesIds.size,
                mNotesIds.size)

        return AlertDialog.Builder(requireContext())
                .setView(view)
                .show()
    }

    override fun closeView() {
        dismiss()
    }

    override fun onStart() {
        super.onStart()
        mPresenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        mPresenter.detachView()
    }

    fun setOnDeleteConfirmListener(listener: OnDeleteNoteConfirmListener?) {
        mListener = listener
    }

    interface OnDeleteNoteConfirmListener {
        fun onDialogButtonDeleteClick()
    }
}