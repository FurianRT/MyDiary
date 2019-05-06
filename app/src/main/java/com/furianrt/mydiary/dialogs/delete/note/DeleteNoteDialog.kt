package com.furianrt.mydiary.dialogs.delete.note

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.furianrt.mydiary.R
import kotlinx.android.synthetic.main.dialog_delete_note.view.*
import javax.inject.Inject

class DeleteNoteDialog : DialogFragment(), DeleteNoteContract.View {

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

    override fun onResume() {
        super.onResume()
        mPresenter.attachView(this)
    }

    override fun onPause() {
        super.onPause()
        mPresenter.detachView()
    }

    fun setOnDeleteConfirmListener(listener: OnDeleteNoteConfirmListener?) {
        mListener = listener
    }

    interface OnDeleteNoteConfirmListener {
        fun onDialogButtonDeleteClick()
    }
}