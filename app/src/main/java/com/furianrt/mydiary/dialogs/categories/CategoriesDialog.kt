package com.furianrt.mydiary.dialogs.categories

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.furianrt.mydiary.R
import com.furianrt.mydiary.dialogs.categories.fragments.delete.CategoryDeleteFragment
import com.furianrt.mydiary.dialogs.categories.fragments.edit.CategoryEditFragment
import com.furianrt.mydiary.dialogs.categories.fragments.list.CategoryListFragment
import com.furianrt.mydiary.utils.inTransaction
import javax.inject.Inject

class CategoriesDialog : DialogFragment(), CategoriesDialogContract.View {

    @Inject
    lateinit var mPresenter: CategoriesDialogContract.Presenter

    private lateinit var mNoteId: String
    private var mView: View? = null

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
        getPresenterComponent(requireContext()).inject(this)
        mNoteId = arguments?.getString(ARG_NOTE_ID) ?: throw IllegalArgumentException()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        childFragmentManager.apply {
            if (findFragmentByTag(CategoryListFragment.TAG) == null) {
                inTransaction {
                    add(R.id.container_categories, CategoryListFragment.newInstance(mNoteId),
                            CategoryListFragment.TAG)
                }
            }
        }
        return mView
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mView = requireActivity().layoutInflater.inflate(R.layout.dialog_categories, null)

        val dialog = AlertDialog.Builder(requireContext())
                .setView(mView)
                .create()

        dialog.setOnKeyListener { _, keyCode, event ->
            childFragmentManager.apply {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.action == KeyEvent.ACTION_UP
                        && (findFragmentByTag(CategoryEditFragment.TAG) != null
                                || findFragmentByTag(CategoryDeleteFragment.TAG) != null)) {

                    popBackStack()
                    return@setOnKeyListener true
                }
            }
            return@setOnKeyListener false
        }

        return dialog
    }

    override fun onResume() {
        super.onResume()
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        mPresenter.attachView(this)
    }

    override fun onPause() {
        super.onPause()
        mPresenter.detachView()
    }
}