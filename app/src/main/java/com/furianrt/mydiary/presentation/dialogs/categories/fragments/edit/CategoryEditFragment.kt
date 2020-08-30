/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.dialogs.categories.fragments.edit

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.furianrt.mydiary.R
import com.furianrt.mydiary.analytics.MyAnalytics
import com.furianrt.mydiary.presentation.base.BaseFragment
import com.furianrt.mydiary.model.entity.MyCategory
import com.furianrt.mydiary.utils.animateShake
import com.furianrt.mydiary.utils.hideKeyboard
import com.furianrt.mydiary.utils.showKeyboard
import kotlinx.android.synthetic.main.fragment_category_edit.*
import javax.inject.Inject

class CategoryEditFragment : BaseFragment(R.layout.fragment_category_edit), CategoryEditContract.View {

    @Inject
    lateinit var presenter: CategoryEditContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
        val category = requireArguments().getParcelable(ARG_CATEGORY) as MyCategory?
        savedInstanceState?.let { state ->
            category!!.name = state.getString(BUNDLE_CATEGORY_NAME, category.name)
            category.color = state.getInt(BUNDLE_CATEGORY_COLOR, category.color)
        }
        presenter.init(arguments?.getParcelable(ARG_CATEGORY)!!)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        color_picker_category.addSVBar(svbar_category)
        color_picker_category.showOldCenterColor = false
        button_category_edit_save_cancel.setOnClickListener {
            presenter.onButtonCancelClick()
        }
        button_category_edit_save.setOnClickListener {
            analytics.sendEvent(MyAnalytics.EVENT_NOTE_CATEGORY_EDITED)
            val color = color_picker_category.color
            val name = edit_category.text?.toString() ?: ""
            presenter.onButtonDoneClick(name, color)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(BUNDLE_CATEGORY_NAME, edit_category.text?.toString() ?: "")
        outState.putInt(BUNDLE_CATEGORY_COLOR, color_picker_category.color)
    }

    override fun showCategory(category: MyCategory) {
        edit_category.setText(category.name)
        color_picker_category.color = category.color
    }

    override fun showErrorEmptyName() {
        text_input_category.animateShake()
        Toast.makeText(requireContext(), getString(R.string.fragment_category_edit_error_empty_name), Toast.LENGTH_SHORT).show()
    }

    override fun close() {
        parentFragmentManager.popBackStack()
    }

    override fun onStart() {
        super.onStart()
        edit_category.requestFocus()
        edit_category.showKeyboard()
        presenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        edit_category.clearFocus()
        edit_category.hideKeyboard()
        presenter.detachView()
    }

    companion object {

        const val TAG = "CategoryEditFragment"
        private const val ARG_CATEGORY = "category"
        private const val BUNDLE_CATEGORY_NAME = "category_name"
        private const val BUNDLE_CATEGORY_COLOR = "category_color"

        @JvmStatic
        fun newInstance(category: MyCategory) =
                CategoryEditFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_CATEGORY, category)
                    }
                }
    }
}
