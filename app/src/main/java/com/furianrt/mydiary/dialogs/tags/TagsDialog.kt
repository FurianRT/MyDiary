package com.furianrt.mydiary.dialogs.tags

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import com.furianrt.mydiary.R
import com.furianrt.mydiary.base.BaseDialog
import com.furianrt.mydiary.dialogs.tags.fragments.add.TagAddFragment
import com.furianrt.mydiary.dialogs.tags.fragments.delete.TagDeleteFragment
import com.furianrt.mydiary.dialogs.tags.fragments.edit.TagEditFragment
import com.furianrt.mydiary.dialogs.tags.fragments.list.TagListFragment
import com.furianrt.mydiary.utils.inTransaction
import javax.inject.Inject

class TagsDialog : BaseDialog(), TagsDialogContract.MvpView {

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
    lateinit var mPresenter: TagsDialogContract.Presenter

    private lateinit var mNoteId: String
    private var mView: View? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
        mNoteId = arguments?.getString(ARG_NOTE_ID) ?: throw IllegalArgumentException()
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
        mPresenter.attachView(this)
        mPresenter.onViewCreated(mNoteId)
    }

    override fun onStop() {
        super.onStop()
        mPresenter.detachView()
    }

    override fun showTagListView(noteId: String) {
        if (childFragmentManager.findFragmentByTag(TagListFragment.TAG) == null) {
            childFragmentManager.inTransaction {
                add(R.id.container_tags, TagListFragment.newInstance(noteId), TagListFragment.TAG)
            }
        }
    }
}