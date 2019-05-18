package com.furianrt.mydiary.dialogs.tags.fragments.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyTag
import com.furianrt.mydiary.utils.animateShake
import com.furianrt.mydiary.utils.hideKeyboard
import com.furianrt.mydiary.utils.showKeyboard
import kotlinx.android.synthetic.main.fragment_tag_edit.*
import kotlinx.android.synthetic.main.fragment_tag_edit.view.*
import javax.inject.Inject

class TagEditFragment : Fragment(), TagEditContract.View {

    @Inject
    lateinit var mPresenter: TagEditContract.Presenter

    private lateinit var mTag: MyTag

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
        mTag = arguments?.getParcelable(ARG_TAG) ?: throw IllegalArgumentException()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tag_edit, container, false)

        view.edit_edit_tag.setText(mTag.name)

        view.button_tag_edit_confirm.setOnClickListener {
            mPresenter.onButtonConfirmClick(mTag, edit_edit_tag.text?.toString() ?: "")
        }
        view.button_tag_edit_close.setOnClickListener { mPresenter.onButtonCloseClick() }

        return view
    }

    override fun showErrorEmptyTagName() {
        input_edit_tag.animateShake()
        Toast.makeText(requireContext(), getString(R.string.fragment_tag_error_empty_name), Toast.LENGTH_SHORT).show()
    }

    override fun showErrorExistingTagName() {
        input_edit_tag.animateShake()
        Toast.makeText(requireContext(), getString(R.string.fragment_add_tag_error_existing_name), Toast.LENGTH_SHORT).show()
    }

    override fun closeView() {
        fragmentManager?.popBackStack()
    }

    override fun onResume() {
        super.onResume()
        edit_edit_tag.requestFocus()
        edit_edit_tag.showKeyboard()
        mPresenter.attachView(this)
    }

    override fun onPause() {
        super.onPause()
        edit_edit_tag.clearFocus()
        edit_edit_tag.hideKeyboard()
        mPresenter.detachView()
    }

    companion object {
        const val TAG = "TagEditFragment"
        private const val ARG_TAG = "tag"

        @JvmStatic
        fun newInstance(tag: MyTag) =
                TagEditFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_TAG, tag)
                    }
                }
    }
}
