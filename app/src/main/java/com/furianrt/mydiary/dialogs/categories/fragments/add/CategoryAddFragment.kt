package com.furianrt.mydiary.dialogs.categories.fragments.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.furianrt.mydiary.R
import com.furianrt.mydiary.utils.animateShake
import com.furianrt.mydiary.utils.hideKeyboard
import com.furianrt.mydiary.utils.showKeyboard
import kotlinx.android.synthetic.main.fragment_category_add.*
import kotlinx.android.synthetic.main.fragment_category_add.view.*
import javax.inject.Inject

class CategoryAddFragment : Fragment(), CategoryAddContract.View {

    @Inject
    lateinit var mPresenter: CategoryAddContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_category_add, container, false)

        view.color_picker_category.addSVBar(view.svbar_category)
        view.color_picker_category.showOldCenterColor = false
        view.button_category_add_cancel.setOnClickListener {
            mPresenter.onButtonCancelClick()
        }
        view.button_category_add.setOnClickListener {
            val color = color_picker_category.color
            val name = edit_category.text?.toString() ?: ""
            mPresenter.onButtonDoneClick(name, color)
        }

        return view
    }

    override fun close() {
        fragmentManager?.popBackStack()
    }

    override fun showErrorEmptyName() {
        text_input_category.animateShake()
        Toast.makeText(requireContext(), getString(R.string.fragment_category_edit_error_empty_name), Toast.LENGTH_SHORT).show()
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
        const val TAG = "CategoryAddFragment"
    }
}
