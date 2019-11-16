/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.dialogs.tags.fragments.delete

import android.os.Bundle
import android.view.View
import com.furianrt.mydiary.R
import com.furianrt.mydiary.analytics.MyAnalytics
import com.furianrt.mydiary.presentation.base.BaseFragment
import com.furianrt.mydiary.model.entity.MyTag
import kotlinx.android.synthetic.main.fragment_tag_delete.*
import javax.inject.Inject

class TagDeleteFragment : BaseFragment(R.layout.fragment_tag_delete), TagDeleteContract.View {

    @Inject
    lateinit var mPresenter: TagDeleteContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
        mPresenter.init(requireArguments().getParcelable(ARG_TAG)!!)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button_delete_tag.setOnClickListener {
            analytics.sendEvent(MyAnalytics.EVENT_NOTE_TAG_DELETED)
            button_delete_tag.isEnabled = false
            mPresenter.onButtonDeleteClick()
        }
        button_delete_tag_cancel.setOnClickListener { mPresenter.onButtonCancelClick() }
    }

    override fun showTagName(name: String) {
        text_tag_delete_message.text = getString(R.string.fragment_tag_delete_message, name)
    }

    override fun closeView() {
        fragmentManager?.popBackStack()
    }

    override fun onStart() {
        super.onStart()
        mPresenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        mPresenter.detachView()
    }

    companion object {
        const val TAG = "TagDeleteFragment"

        private const val ARG_TAG = "tag"

        @JvmStatic
        fun newInstance(tag: MyTag) =
                TagDeleteFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_TAG, tag)
                    }
                }
    }
}
