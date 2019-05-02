package com.furianrt.mydiary.dialogs.tags.fragments.delete


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyTag
import kotlinx.android.synthetic.main.fragment_tag_delete.view.*
import javax.inject.Inject

class TagDeleteFragment : Fragment(), TagDeleteContract.View {

    @Inject
    lateinit var mPresenter: TagDeleteContract.Presenter

    private lateinit var mTag: MyTag

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mTag = arguments?.getParcelable(ARG_TAG) ?: throw IllegalArgumentException()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tag_delete, container, false)

        view.text_tag_delete_message.text = getString(R.string.fragment_tag_delete_message, mTag.name)
        view.button_delete_tag.setOnClickListener { mPresenter.onButtonDeleteClick(mTag) }
        view.button_delete_tag_cancel.setOnClickListener { mPresenter.onButtonCancelClick() }

        return view
    }

    override fun closeView() {
        fragmentManager?.popBackStack()
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
