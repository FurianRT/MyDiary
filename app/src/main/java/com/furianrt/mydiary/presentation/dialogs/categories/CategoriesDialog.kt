/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.dialogs.categories

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import com.furianrt.mydiary.R
import com.furianrt.mydiary.presentation.base.BaseDialog
import com.furianrt.mydiary.presentation.dialogs.categories.fragments.delete.CategoryDeleteFragment
import com.furianrt.mydiary.presentation.dialogs.categories.fragments.edit.CategoryEditFragment
import com.furianrt.mydiary.presentation.dialogs.categories.fragments.list.CategoryListFragment
import com.furianrt.mydiary.utils.inTransaction
import javax.inject.Inject

class CategoriesDialog : BaseDialog(), CategoriesDialogContract.View {

    @Inject
    lateinit var presenter: CategoriesDialogContract.Presenter

    private lateinit var mNoteIds: List<String>
    private var mView: View? = null
    private var mListener: OnCategorySelectedListener? = null

    companion object {

        const val TAG = "CategoriesDialog"
        private const val ARG_NOTE_IDS = "noteIds"

        @JvmStatic
        fun newInstance(noteIds: List<String>) =
                CategoriesDialog().apply {
                    arguments = Bundle().apply {
                        putStringArrayList(ARG_NOTE_IDS, ArrayList(noteIds))
                    }
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getPresenterComponent(requireContext()).inject(this)
        mNoteIds = requireArguments().getStringArrayList(ARG_NOTE_IDS)!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        childFragmentManager.apply {
            if (findFragmentByTag(CategoryListFragment.TAG) == null) {
                inTransaction {
                    add(R.id.container_categories, CategoryListFragment.newInstance(mNoteIds),
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

    fun onCategorySelected() {
        mListener?.onCategorySelected()
    }

    fun setOnCategorySelectedListener(listener: OnCategorySelectedListener) {
        mListener = listener
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        presenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        presenter.detachView()
    }

    interface OnCategorySelectedListener {
        fun onCategorySelected()
    }
}