/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.dialogs.delete.image

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.furianrt.mydiary.R
import com.furianrt.mydiary.presentation.base.BaseDialog
import kotlinx.android.synthetic.main.dialog_delete_image.view.*
import javax.inject.Inject

class DeleteImageDialog : BaseDialog(), DeleteImageContract.View {

    companion object {
        const val TAG = "DeleteImageDialog"
        private const val ARG_IMAGE_NAMES = "image_names"

        @JvmStatic
        fun newInstance(imageNames: List<String>) =
                DeleteImageDialog().apply {
                    arguments = Bundle().apply {
                        putStringArrayList(ARG_IMAGE_NAMES, ArrayList(imageNames))
                    }
                }
    }

    @Inject
    lateinit var mPresenter: DeleteImageContract.Presenter

    private var mListener: OnDeleteImageConfirmListener? = null

    private lateinit var mImageNames: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
        mImageNames = arguments?.getStringArrayList(ARG_IMAGE_NAMES) ?: emptyList()
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_delete_image, null)

        view.button_image_delete.setOnClickListener {
            mListener?.onButtonDeleteConfirmClick()
            mPresenter.onButtonDeleteClick(mImageNames)
        }
        view.button_image_delete_cancel.setOnClickListener { mPresenter.onButtonCancelClick() }
        view.text_image_delete_title.text = resources.getQuantityString(
                R.plurals.image_delete_confirmation,
                mImageNames.size,
                mImageNames.size)

        return AlertDialog.Builder(requireContext())
                .setView(view)
                .show()
    }

    override fun closeView() {
        dismiss()
    }

    fun setOnDeleteConfirmListener(listener: OnDeleteImageConfirmListener?) {
        mListener = listener
    }

    override fun onStart() {
        super.onStart()
        mPresenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        mPresenter.detachView()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        mListener?.onDialogDeleteDismiss(mImageNames)
    }

    interface OnDeleteImageConfirmListener {
        fun onButtonDeleteConfirmClick()
        fun onDialogDeleteDismiss(imageNames: List<String>)
    }
}