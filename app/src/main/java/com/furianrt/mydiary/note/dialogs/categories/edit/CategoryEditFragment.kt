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

class CategoryEditFragment : Fragment(), View.OnClickListener, CategoryEditContract.View {

    @Inject
    lateinit var mPresenter: CategoryEditContract.Presenter

    private var mCategoryId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(context!!).inject(this)
        super.onCreate(savedInstanceState)
        arguments?.let { mCategoryId = it.getString(ARG_CATEGORY_ID, "") }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_category_edit, container, false)

        view.apply {
            color_picker_category.addSVBar(svbar_category)
            color_picker_category.showOldCenterColor = false
            button_category_back.setOnClickListener { fragmentManager?.popBackStack() }
            button_category_done.setOnClickListener(this@CategoryEditFragment)
        }

        mPresenter.attachView(this)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            view.edit_category.setText(savedInstanceState.getString(BUNDLE_CATEGORY_NAME, ""))
            view.color_picker_category.color =
                    savedInstanceState.getInt(BUNDLE_CATEGORY_COLOR, MyCategory.DEFAULT_COLOR)
        } else {
            mPresenter.loadCategory(mCategoryId)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        view?.let {
            outState.putString(BUNDLE_CATEGORY_NAME, it.edit_category.text.toString())
            outState.putInt(BUNDLE_CATEGORY_COLOR, it.color_picker_category.color)
        }
    }

    override fun showCategory(category: MyCategory) {
        view?.apply {
            edit_category.setText(category.name)
            color_picker_category.color = category.color
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.button_category_done ->
                view?.let {
                    val color = it.color_picker_category.color
                    val name = it.edit_category.text?.toString() ?: ""
                    val category = MyCategory(mCategoryId, name, color)
                    mPresenter.onButtonDoneClick(category)
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

        const val TAG = "CategoryEditFragment"
        private const val ARG_CATEGORY_ID = "category_ID"
        private const val BUNDLE_CATEGORY_NAME = "category_name"
        private const val BUNDLE_CATEGORY_COLOR = "category_color"

        @JvmStatic
        fun newInstance(categoryId: String) =
                CategoryEditFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_CATEGORY_ID, categoryId)
                    }
                }
    }
}
