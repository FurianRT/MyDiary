package com.furianrt.mydiary.note.dialogs.categories

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyCategory
import com.furianrt.mydiary.note.dialogs.categories.edit.CategoryEditFragment
import com.furianrt.mydiary.note.dialogs.categories.list.CategoryListFragment
import com.furianrt.mydiary.utils.inTransaction
import javax.inject.Inject

class CategoriesDialog : DialogFragment(), CategoriesDialogContract.View {

    @Inject
    lateinit var mPresenter: CategoriesDialogContract.Presenter

    private lateinit var mNoteId: String
    private var mView: View? = null
    private var mListener: OnCategoriesDialogInteractionListener? = null

    companion object {

        const val TAG = "CategoriesDialog"
        private const val ARG_NOTE_ID = "noteId"

        @JvmStatic
        fun newInstance(noteId: String) =
                CategoriesDialog().apply {
                    arguments = Bundle().apply {
                        putString(ARG_NOTE_ID, noteId)
                    }
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getPresenterComponent(context!!).inject(this)
        mNoteId = arguments?.getString(ARG_NOTE_ID) ?: throw IllegalArgumentException()
    }

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mPresenter.onViewCreate(mNoteId)
        return mView
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mView = activity?.layoutInflater?.inflate(R.layout.dialog_categories, null)

        mPresenter.attachView(this)

        val dialog = AlertDialog.Builder(context!!)
                .setView(mView)
                .setPositiveButton(getString(R.string.close), null)
                .setNegativeButton(getString(R.string.no_category)) { _, _ ->
                    mListener?.onNoCategoryPicked()
                }
                .create()

        dialog.setOnKeyListener { _, keyCode, event ->
            childFragmentManager.apply {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.action == KeyEvent.ACTION_UP
                        && findFragmentByTag(CategoryEditFragment.TAG) != null) {

                    popBackStack()
                    return@setOnKeyListener true
                }
            }
            return@setOnKeyListener false
        }

        return dialog
    }

    override fun showViewCategoryList(noteId: String) {
        childFragmentManager.apply {
            if (findFragmentByTag(CategoryListFragment.TAG) == null) {
                inTransaction {
                    add(R.id.container_categories, CategoryListFragment.newInstance(noteId),
                            CategoryListFragment.TAG)
                }
            }
        }
    }

    fun setOnCategoriesDialogListener(listener: OnCategoriesDialogInteractionListener?) {
        mListener = listener
    }

    override fun onStart() {
        super.onStart()
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        mListener = null
        mPresenter.detachView()
    }

    interface OnCategoriesDialogInteractionListener {

        fun onCategoryPicked(category: MyCategory)

        fun onNoCategoryPicked()
    }
}