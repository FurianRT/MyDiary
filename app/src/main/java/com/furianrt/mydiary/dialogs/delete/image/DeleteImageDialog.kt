package com.furianrt.mydiary.dialogs.delete.image

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyImage
import kotlinx.android.synthetic.main.dialog_delete_image.view.*
import javax.inject.Inject

class DeleteImageDialog : DialogFragment(), DeleteImageContract.View {

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

    @Inject
    lateinit var mPresenter: DeleteImageContract.Presenter

    private var mListener: OnDeleteImageConfirmListener? = null

    private lateinit var mImages: List<MyImage>

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
        mImages = arguments?.getParcelableArrayList(ARG_IMAGES) ?: emptyList()
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_delete_image, null)

        view.button_image_delete.setOnClickListener {
            mListener?.onButtonDeleteConfirmClick()
            mPresenter.onButtonDeleteClick(mImages)
        }
        view.button_image_delete_cancel.setOnClickListener { mPresenter.onButtonCancelClick() }
        view.text_image_delete_title.text = resources.getQuantityString(
                R.plurals.image_delete_confirmation,
                mImages.size,
                mImages.size)

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

    override fun onResume() {
        super.onResume()
        mPresenter.attachView(this)
    }

    override fun onPause() {
        super.onPause()
        mPresenter.detachView()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        mListener?.onDialogDeleteDismiss(mImages)
    }

    interface OnDeleteImageConfirmListener {
        fun onButtonDeleteConfirmClick()
        fun onDialogDeleteDismiss(images: List<MyImage>)
    }
}