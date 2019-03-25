package com.furianrt.mydiary.general

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.furianrt.mydiary.R

class DeleteConfirmDialog : DialogFragment() {

    companion object {
        const val TAG = "DeleteConfirmDialog"
        private const val ARG_MESSAGE = "message"

        @JvmStatic
        fun newInstance(message: String) =
                DeleteConfirmDialog().apply {
                    arguments = Bundle().apply {
                        putString(ARG_MESSAGE, message)
                    }
                }
    }

    private var mListener: OnDeleteConfirmListener? = null

    private lateinit var mMessage: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mMessage = arguments?.getString(ARG_MESSAGE) ?: ""
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(context!!)
                .setMessage(mMessage)
                .setPositiveButton(R.string.delete) { _, _ -> mListener?.onDialogButtonDeleteClick() }
                .setNegativeButton(R.string.cancel, null)
                .show()
    }

    fun setOnDeleteConfirmListener(listener: OnDeleteConfirmListener?) {
        mListener = listener
    }

    interface OnDeleteConfirmListener {
        fun onDialogButtonDeleteClick()
    }
}