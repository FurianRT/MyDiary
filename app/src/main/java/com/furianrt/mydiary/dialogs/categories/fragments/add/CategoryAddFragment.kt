package com.furianrt.mydiary.dialogs.categories.fragments.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyCategory
import javax.inject.Inject

class CategoryAddFragment : Fragment(), CategoryAddContract.View {

    @Inject
    lateinit var mPresenter: CategoryAddContract.Presenter

    private lateinit var mCategory: MyCategory

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
        mCategory = arguments?.getParcelable(ARG_CATEGORY) ?: throw IllegalArgumentException()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_category_add, container, false)



        return view
    }

    override fun close() {
        fragmentManager?.popBackStack()
    }

    override fun showErrorEmptyName() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onResume() {
        super.onResume()
        mPresenter.attachView(this)
    }

    override fun onPause() {
        super.onPause()
        mPresenter.detachView()
    }

    companion object {
        const val TAG = "CategoryAddFragment"

        private const val ARG_CATEGORY = "category"

        @JvmStatic
        fun newInstance(category: MyCategory) =
                CategoryAddFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_CATEGORY, category)
                    }
                }
    }
}
