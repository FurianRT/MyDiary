package com.furianrt.mydiary.dialogs.categories.fragments.delete

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyCategory
import com.furianrt.mydiary.general.Analytics
import kotlinx.android.synthetic.main.fragment_category_delete.view.*
import javax.inject.Inject

class CategoryDeleteFragment : Fragment(), CategoryDeleteContract.View {

    @Inject
    lateinit var mPresenter: CategoryDeleteContract.Presenter

    private lateinit var mCategory: MyCategory

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
        mCategory = arguments?.getParcelable(ARG_CATEGORY) ?: throw IllegalArgumentException()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_category_delete, container, false)

        view.text_category_delete_message.text =
                getString(R.string.fragment_category_delete_message, mCategory.name)

        view.button_delete_category.setOnClickListener {
            Analytics.sendEvent(requireContext(), Analytics.EVENT_NOTE_CATEGORY_DELETED)
            mPresenter.onButtonDeleteClick(mCategory)
        }
        view.button_delete_category_cancel.setOnClickListener { mPresenter.onButtonCancelClick() }

        return view
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
