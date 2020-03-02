/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.dialogs.tags

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import com.furianrt.mydiary.R
import com.furianrt.mydiary.presentation.base.BaseDialog
import com.furianrt.mydiary.presentation.dialogs.tags.fragments.add.TagAddFragment
import com.furianrt.mydiary.presentation.dialogs.tags.fragments.delete.TagDeleteFragment
import com.furianrt.mydiary.presentation.dialogs.tags.fragments.edit.TagEditFragment
import com.furianrt.mydiary.presentation.dialogs.tags.fragments.list.TagListFragment
import com.furianrt.mydiary.utils.inTransaction
import javax.inject.Inject

class TagsDialog : BaseDialog(), TagsDialogContract.View {

    companion object {
        const val TAG = "TagsDialog"
        private const val ARG_NOTE_ID = "note_id"

        @JvmStatic
        fun newInstance(noteId: String) =
                TagsDialog().apply {
                    arguments = Bundle().apply {
                        putString(ARG_NOTE_ID, noteId)
                    }
                }
    }

    @Inject
    lateinit var presenter: TagsDialogContract.Presenter

    private var mView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
        presenter.init(arguments?.getString(ARG_NOTE_ID)!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return mView
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mView = requireActivity().layoutInflater.inflate(R.layout.dialog_tags, null)

        val dialog = AlertDialog.Builder(requireContext())
                .setView(mView)
                .create()

        dialog.setOnKeyListener { _, keyCode, event ->
            childFragmentManager.apply {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.action == KeyEvent.ACTION_UP
                        && (findFragmentByTag(TagDeleteFragment.TAG) != null
                                || findFragmentByTag(TagAddFragment.TAG) != null
                                || findFragmentByTag(TagEditFragment.TAG) != null)) {

                    popBackStack()
                    return@setOnKeyListener true
                }
            }
            return@setOnKeyListener false
        }

        return dialog
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

    override fun showTagListView(noteId: String) {
        if (childFragmentManager.findFragmentByTag(TagListFragment.TAG) == null) {
            childFragmentManager.inTransaction {
                add(R.id.container_tags, TagListFragment.newInstance(noteId), TagListFragment.TAG)
            }
        }
    }
}