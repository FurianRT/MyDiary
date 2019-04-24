package com.furianrt.mydiary.dialogs.delete.image

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyImage

class DeleteImageDialog : DialogFragment() {

    companion object {
        const val TAG = "DeleteImageDialog"
        private const val ARG_IMAGES = "images"

        @JvmStatic
        fun newInstance(images: List<MyImage>) =
                DeleteImageDialog().apply {
                    arguments = Bundle().apply {
                        putParcelableArrayList(ARG_IMAGES, ArrayList(images))
                    }
                }
    }

    private var mListener: OnDeleteImageConfirmListener? = null

    private lateinit var mImages: List<MyImage>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mImages = arguments?.getParcelableArrayList(ARG_IMAGES) ?: emptyList()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
                .setMessage(resources.getQuantityString(
                        R.plurals.image_delete_confirmation,
                        mImages.size,
                        mImages.size))
                .setPositiveButton(R.string.delete) { _, _ -> mListener?.onDialogButtonDeleteClick(mImages) }
                .setNegativeButton(R.string.cancel) { _, _ -> mListener?.onDialogButtonCancelClick(mImages) }
                .show()
    }

    fun setOnDeleteConfirmListener(listener: OnDeleteImageConfirmListener?) {
        mListener = listener
    }

    interface OnDeleteImageConfirmListener {
        fun onDialogButtonDeleteClick(images: List<MyImage>)
        fun onDialogButtonCancelClick(images: List<MyImage>)
    }
}