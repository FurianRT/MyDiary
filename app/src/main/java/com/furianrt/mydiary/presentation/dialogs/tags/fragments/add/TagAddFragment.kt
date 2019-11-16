/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.dialogs.tags.fragments.add

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.furianrt.mydiary.R
import com.furianrt.mydiary.presentation.base.BaseFragment
import com.furianrt.mydiary.utils.animateShake
import com.furianrt.mydiary.utils.hideKeyboard
import com.furianrt.mydiary.utils.showKeyboard
import kotlinx.android.synthetic.main.fragment_tag_add.*
import javax.inject.Inject

class TagAddFragment : BaseFragment(R.layout.fragment_tag_add), TagAddContract.View {

    @Inject
    lateinit var mPresenter: TagAddContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
        mPresenter.init(requireArguments().getString(ARG_NOTE_ID)!!)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button_tag_add.setOnClickListener {
            mPresenter.onButtonAddClick(edit_add_tag.text?.toString() ?: "")
        }
        button_tag_add_close.setOnClickListener { mPresenter.onButtonCloseClick() }
    }

    override fun showErrorEmptyTagName() {
        input_add_tag.animateShake()
        Toast.makeText(requireContext(), getString(R.string.fragment_tag_error_empty_name), Toast.LENGTH_SHORT).show()
    }

    override fun showErrorExistingTagName() {
        input_add_tag.animateShake()
        Toast.makeText(requireContext(), getString(R.string.fragment_add_tag_error_existing_name), Toast.LENGTH_SHORT).show()
    }

    override fun closeView() {
        fragmentManager?.popBackStack()
    }

    override fun onStart() {
        super.onStart()
        mPresenter.attachView(this)
        edit_add_tag.requestFocus()
        edit_add_tag.showKeyboard()
    }

    override fun onStop() {
        super.onStop()
        edit_add_tag.clearFocus()
        edit_add_tag.hideKeyboard()
        mPresenter.detachView()
    }

    companion object {
        const val TAG = "TagAddFragment"
        private const val ARG_NOTE_ID = "note_id"

        @JvmStatic
        fun newInstance(noteId: String) =
                TagAddFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_NOTE_ID, noteId)
                    }
                }
    }
}
