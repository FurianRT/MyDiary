/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.dialogs.categories.fragments.delete

import android.os.Bundle
import android.view.View
import com.furianrt.mydiary.R
import com.furianrt.mydiary.analytics.MyAnalytics
import com.furianrt.mydiary.presentation.base.BaseFragment
import com.furianrt.mydiary.model.entity.MyCategory
import kotlinx.android.synthetic.main.fragment_category_delete.*
import javax.inject.Inject

class CategoryDeleteFragment : BaseFragment(R.layout.fragment_category_delete), CategoryDeleteContract.View {

    @Inject
    lateinit var presenter: CategoryDeleteContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
        presenter.init(requireArguments().getParcelable(ARG_CATEGORY)!!)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button_delete_category.setOnClickListener {
            analytics.sendEvent(MyAnalytics.EVENT_NOTE_CATEGORY_DELETED)
            presenter.onButtonDeleteClick()
        }
        button_delete_category_cancel.setOnClickListener { presenter.onButtonCancelClick() }
    }

    override fun showDeleteMessage(name: String) {
        text_category_delete_message.text = getString(R.string.fragment_category_delete_message, name)
    }

    override fun closeView() {
        parentFragmentManager.popBackStack()
    }

    override fun onStart() {
        super.onStart()
        presenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        presenter.detachView()
    }

    companion object {
        const val TAG = "CategoryDeleteFragment"

        private const val ARG_CATEGORY = "category"

        @JvmStatic
        fun newInstance(category: MyCategory) =
                CategoryDeleteFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_CATEGORY, category)
                    }
                }
    }
}
