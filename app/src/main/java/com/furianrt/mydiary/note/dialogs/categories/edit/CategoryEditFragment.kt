package com.furianrt.mydiary.note.dialogs.categories.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyCategory
import kotlinx.android.synthetic.main.fragment_category_edit.view.*
import javax.inject.Inject

private const val ARG_CATEGORY = "category"

class CategoryEditFragment : Fragment(), View.OnClickListener, CategoryEditContract.View {

    @Inject
    lateinit var mPresenter: CategoryEditContract.Presenter

    private lateinit var mCategory: MyCategory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getPresenterComponent(context!!).inject(this)
        mCategory = arguments?.getParcelable(ARG_CATEGORY) ?: MyCategory(0, "")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_category_edit, container, false)

        mPresenter.attachView(this)

        setupUi(view)

        return view
    }

    private fun setupUi(view: View) {
        view.apply {
            button_category_back.setOnClickListener { fragmentManager?.popBackStack() }
            button_category_done.setOnClickListener(this@CategoryEditFragment)
            edit_category.setText(mCategory.name)
            color_picker_category.color = mCategory.color
            color_picker_category.addSVBar(svbar_category)
            color_picker_category.showOldCenterColor = false
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.button_category_done ->
                view?.let {
                    mCategory.color = it.color_picker_category.color
                    mCategory.name = it.edit_category.text?.toString() ?: ""
                    mPresenter.onButtonDoneClick(mCategory)
                }
        }
    }

    override fun close() {
        fragmentManager?.popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mPresenter.detachView()
    }

    companion object {

        val TAG = CategoryEditFragment::class.toString()

        @JvmStatic
        fun newInstance(category: MyCategory) =
                CategoryEditFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_CATEGORY, category)
                    }
                }
    }
}
