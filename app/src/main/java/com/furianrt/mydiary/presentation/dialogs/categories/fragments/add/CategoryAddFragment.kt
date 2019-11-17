/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.dialogs.categories.fragments.add

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.furianrt.mydiary.R
import com.furianrt.mydiary.presentation.base.BaseFragment
import com.furianrt.mydiary.utils.animateShake
import com.furianrt.mydiary.utils.hideKeyboard
import com.furianrt.mydiary.utils.showKeyboard
import kotlinx.android.synthetic.main.fragment_category_add.*
import javax.inject.Inject

class CategoryAddFragment : BaseFragment(R.layout.fragment_category_add), CategoryAddContract.View {

    @Inject
    lateinit var presenter: CategoryAddContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        color_picker_category.addSVBar(svbar_category)
        color_picker_category.showOldCenterColor = false
        button_category_add_cancel.setOnClickListener {
            presenter.onButtonCancelClick()
        }
        button_category_add.setOnClickListener {
            val color = color_picker_category.color
            val name = edit_category.text?.toString() ?: ""
            presenter.onButtonDoneClick(name, color)
        }
    }

    override fun close() {
        fragmentManager?.popBackStack()
    }

    override fun showErrorEmptyName() {
        text_input_category.animateShake()
        Toast.makeText(requireContext(), getString(R.string.fragment_category_edit_error_empty_name), Toast.LENGTH_SHORT).show()
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
        const val TAG = "CategoryAddFragment"
    }
}
