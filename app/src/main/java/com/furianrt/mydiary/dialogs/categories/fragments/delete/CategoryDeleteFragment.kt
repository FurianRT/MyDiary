package com.furianrt.mydiary.dialogs.categories.fragments.delete

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.furianrt.mydiary.R
import com.furianrt.mydiary.analytics.MyAnalytics
import com.furianrt.mydiary.base.BaseFragment
import com.furianrt.mydiary.data.model.MyCategory
import kotlinx.android.synthetic.main.fragment_category_delete.*
import kotlinx.android.synthetic.main.fragment_category_delete.view.*
import javax.inject.Inject

class CategoryDeleteFragment : BaseFragment(), CategoryDeleteContract.MvpView {

    @Inject
    lateinit var mPresenter: CategoryDeleteContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
        mPresenter.init(arguments?.getParcelable(ARG_CATEGORY)!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_category_delete, container, false)



        view.button_delete_category.setOnClickListener {
            analytics.sendEvent(MyAnalytics.EVENT_NOTE_CATEGORY_DELETED)
            mPresenter.onButtonDeleteClick()
        }
        view.button_delete_category_cancel.setOnClickListener { mPresenter.onButtonCancelClick() }

        return view
    }

    override fun showDeleteMessage(name: String) {
        text_category_delete_message.text = getString(R.string.fragment_category_delete_message, name)
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
