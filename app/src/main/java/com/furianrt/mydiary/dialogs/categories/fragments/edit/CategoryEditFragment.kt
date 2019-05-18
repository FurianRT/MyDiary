package com.furianrt.mydiary.dialogs.categories.fragments.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyCategory
import com.furianrt.mydiary.utils.animateShake
import com.furianrt.mydiary.utils.hideKeyboard
import com.furianrt.mydiary.utils.showKeyboard
import kotlinx.android.synthetic.main.fragment_category_edit.*
import kotlinx.android.synthetic.main.fragment_category_edit.view.*
import javax.inject.Inject

class CategoryEditFragment : Fragment(), CategoryEditContract.View {

    @Inject
    lateinit var mPresenter: CategoryEditContract.Presenter

    private lateinit var mCategory: MyCategory

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
        mCategory = arguments?.getParcelable(ARG_CATEGORY) ?: throw IllegalArgumentException()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_category_edit, container, false)

        view.edit_category.setText(mCategory.name)

        view.color_picker_category.addSVBar(view.svbar_category)
        view.color_picker_category.showOldCenterColor = false
        view.button_category_edit_save_cancel.setOnClickListener {
            mPresenter.onButtonCancelClick()
        }
        view.button_category_edit_save.setOnClickListener {
            val color = color_picker_category.color
            val name = edit_category.text?.toString() ?: ""
            mPresenter.onButtonDoneClick(mCategory, name, color)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            color_picker_category.color =
                    savedInstanceState.getInt(BUNDLE_CATEGORY_COLOR, MyCategory.DEFAULT_COLOR)
        } else {
            color_picker_category.color = mCategory.color
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
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
        fragmentManager?.popBackStack()
    }

    override fun onResume() {
        super.onResume()
        edit_category.requestFocus()
        edit_category.showKeyboard()
        mPresenter.attachView(this)
    }

    override fun onPause() {
        super.onPause()
        edit_category.clearFocus()
        edit_category.hideKeyboard()
        mPresenter.detachView()
    }

    companion object {

        const val TAG = "CategoryEditFragment"
        private const val ARG_CATEGORY = "category"
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
