package com.furianrt.mydiary.dialogs.delete.note

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyNote

class DeleteNoteDialog : DialogFragment() {

    companion object {
        const val TAG = "DeleteNoteDialog"
        private const val ARG_NOTES = "notes"

        @JvmStatic
        fun newInstance(notes: List<MyNote>) =
                DeleteNoteDialog().apply {
                    arguments = Bundle().apply {
                        putParcelableArrayList(ARG_NOTES, ArrayList(notes))
                    }
                }
    }

    private var mListener: OnDeleteNoteConfirmListener? = null

    private lateinit var mNotes: List<MyNote>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNotes = arguments?.getParcelableArrayList(ARG_NOTES) ?: emptyList()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
                .setMessage(resources.getQuantityString(
                        R.plurals.note_delete_confirmation,
                        mNotes.size,
                        mNotes.size))
                .setPositiveButton(R.string.delete) { _, _ -> mListener?.onDialogButtonDeleteClick(mNotes) }
                .setNegativeButton(R.string.cancel, null)
                .show()
    }

    fun setOnDeleteConfirmListener(listener: OnDeleteNoteConfirmListener?) {
        mListener = listener
    }

    interface OnDeleteNoteConfirmListener {
        fun onDialogButtonDeleteClick(notes: List<MyNote>)
    }
}