/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.dialogs.tags.fragments.edit

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.furianrt.mydiary.R
import com.furianrt.mydiary.analytics.MyAnalytics
import com.furianrt.mydiary.presentation.base.BaseFragment
import com.furianrt.mydiary.model.entity.MyTag
import com.furianrt.mydiary.utils.animateShake
import com.furianrt.mydiary.utils.hideKeyboard
import com.furianrt.mydiary.utils.showKeyboard
import kotlinx.android.synthetic.main.fragment_tag_edit.*
import javax.inject.Inject

class TagEditFragment : BaseFragment(R.layout.fragment_tag_edit), TagEditContract.View {

    @Inject
    lateinit var presenter: TagEditContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
        val tag = requireArguments().getParcelable(ARG_TAG) as MyTag?
        savedInstanceState?.let { state ->
            tag!!.name = state.getString(BUNDLE_TAG_NAME, tag.name)
        }
        presenter.init(tag!!)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button_tag_edit_confirm.setOnClickListener {
            analytics.sendEvent(MyAnalytics.EVENT_NOTE_TAG_EDITED)
            presenter.onButtonConfirmClick(edit_edit_tag.text?.toString() ?: "")
        }
        button_tag_edit_close.setOnClickListener { presenter.onButtonCloseClick() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(BUNDLE_TAG_NAME, edit_edit_tag.text?.toString() ?: "")
    }

    override fun showTagName(name: String) {
        edit_edit_tag.setText(name)
    }

    override fun showErrorEmptyTagName() {
        input_edit_tag.animateShake()
        Toast.makeText(requireContext(), getString(R.string.fragment_tag_error_empty_name), Toast.LENGTH_SHORT).show()
    }

    override fun showErrorExistingTagName() {
        input_edit_tag.animateShake()
        Toast.makeText(requireContext(), getString(R.string.fragment_add_tag_error_existing_name), Toast.LENGTH_SHORT).show()
    }

    override fun closeView() {
        parentFragmentManager.popBackStack()
    }

    override fun onStart() {
        super.onStart()
        edit_edit_tag.requestFocus()
        edit_edit_tag.showKeyboard()
        presenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        edit_edit_tag.clearFocus()
        edit_edit_tag.hideKeyboard()
        presenter.detachView()
    }

    companion object {
        const val TAG = "TagEditFragment"
        private const val ARG_TAG = "tag"
        private const val BUNDLE_TAG_NAME = "tag_name"

        @JvmStatic
        fun newInstance(tag: MyTag) =
                TagEditFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_TAG, tag)
                    }
                }
    }
}
