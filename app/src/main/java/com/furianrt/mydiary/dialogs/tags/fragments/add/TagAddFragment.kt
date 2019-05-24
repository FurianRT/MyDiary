package com.furianrt.mydiary.dialogs.tags.fragments.add

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
import kotlinx.android.synthetic.main.fragment_tag_add.*
import kotlinx.android.synthetic.main.fragment_tag_add.view.*
import javax.inject.Inject

class TagAddFragment : Fragment(), TagAddContract.View {

    @Inject
    lateinit var mPresenter: TagAddContract.Presenter

    private lateinit var mNoteId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
        mNoteId = arguments?.getString(ARG_NOTE_ID) ?: throw IllegalArgumentException()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tag_add, container, false)

        view.button_tag_add.setOnClickListener {
            mPresenter.onButtonAddClick(mNoteId, edit_add_tag.text?.toString() ?: "")
        }
        view.button_tag_add_close.setOnClickListener { mPresenter.onButtonCloseClick() }

        return view
    }

    override fun showErrorEmptyTagName() {
        input_add_tag.animateShake()
        Toast.makeText(requireContext(), getString(R.string.fragment_tag_error_empty_name), Toast.LENGTH_SHORT).show()
    }

    override fun showErrorExistingTagName() {
        input_add_tag.animateShake()
        Toast.makeText(requireContext(), getString(R.string.fragment_add_tag_error_existing_name), Toast.LENGTH_SHORT).show()
    }

    override fun closeView() {
        fragmentManager?.popBackStack()
    }

    override fun onStart() {
        super.onStart()
        mPresenter.attachView(this)
        edit_add_tag.requestFocus()
        edit_add_tag.showKeyboard()
    }

    override fun onStop() {
        super.onStop()
        edit_add_tag.clearFocus()
        edit_add_tag.hideKeyboard()
        mPresenter.detachView()
    }

    companion object {
        const val TAG = "TagAddFragment"
        private const val ARG_NOTE_ID = "note_id"

        @JvmStatic
        fun newInstance(noteId: String) =
                TagAddFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_NOTE_ID, noteId)
                    }
                }
    }
}
