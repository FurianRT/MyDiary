package com.furianrt.mydiary.view.dialogs.tags.fragments.delete

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.furianrt.mydiary.R
import com.furianrt.mydiary.analytics.MyAnalytics
import com.furianrt.mydiary.view.base.BaseFragment
import com.furianrt.mydiary.data.model.MyTag
import kotlinx.android.synthetic.main.fragment_tag_delete.*
import kotlinx.android.synthetic.main.fragment_tag_delete.view.*
import javax.inject.Inject

class TagDeleteFragment : BaseFragment(), TagDeleteContract.MvpView {

    @Inject
    lateinit var mPresenter: TagDeleteContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
        mPresenter.init(arguments?.getParcelable(ARG_TAG)!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tag_delete, container, false)

        view.button_delete_tag.setOnClickListener {
            analytics.sendEvent(MyAnalytics.EVENT_NOTE_TAG_DELETED)
            button_delete_tag.isEnabled = false
            mPresenter.onButtonDeleteClick()
        }
        view.button_delete_tag_cancel.setOnClickListener { mPresenter.onButtonCancelClick() }

        return view
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
