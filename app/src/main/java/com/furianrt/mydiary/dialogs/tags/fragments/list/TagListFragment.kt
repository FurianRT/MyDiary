package com.furianrt.mydiary.dialogs.tags.fragments.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyTag
import com.furianrt.mydiary.dialogs.tags.TagsDialog
import com.furianrt.mydiary.dialogs.tags.fragments.add.TagAddFragment
import com.furianrt.mydiary.dialogs.tags.fragments.delete.TagDeleteFragment
import com.furianrt.mydiary.dialogs.tags.fragments.edit.TagEditFragment
import com.furianrt.mydiary.utils.inTransaction
import kotlinx.android.synthetic.main.fragment_tag_list.view.*
import javax.inject.Inject

class TagListFragment : Fragment(), TagListContract.View, TagListAdapter.OnTagListItemInteractionListener {

    @Inject
    lateinit var mPresenter: TagListContract.Presenter

    private lateinit var mNoteId: String
    private val mListAdapter = TagListAdapter(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
        mNoteId = arguments?.getString(ARG_NOTE_ID) ?: throw IllegalArgumentException()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tag_list, container, false)

        view.button_tags_close.setOnClickListener { mPresenter.onButtonCloseClick() }
        view.button_tags_add.setOnClickListener { mPresenter.onButtonAddClick() }
        view.search_tags.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean { return false }
            override fun onQueryTextChange(newText: String?): Boolean {
                mPresenter.onSearchQueryChange(newText)
                return true
            }
        })

        with(view.list_tags) {
            val manager = LinearLayoutManager(context)
            layoutManager = manager
            adapter = mListAdapter
            addItemDecoration(DividerItemDecoration(context, manager.orientation))
        }

        return view
    }

    override fun showTags(tags: List<MyTag>) {
        mListAdapter.showList(tags.toMutableList())
    }

    override fun onItemCheckChange(tag: MyTag, checked: Boolean) {
        mPresenter.onItemCheckChange(mNoteId, tag, checked)
    }

    override fun onItemEditClick(tag: MyTag) {
        mPresenter.onButtonEditTagClick(tag)
    }

    override fun onItemDeleteClick(tag: MyTag) {
        mPresenter.onButtonDeleteTagClick(tag)
    }

    override fun closeView() {
        (parentFragment as? TagsDialog?)?.dismiss()
    }

    override fun showAddTagView() {
        if (fragmentManager?.findFragmentByTag(TagAddFragment.TAG) == null) {
            fragmentManager?.inTransaction {
                replace(R.id.container_tags, TagAddFragment.newInstance(mNoteId), TagAddFragment.TAG)
                addToBackStack(null)
            }
        }
    }

    override fun showEditTagView(tag: MyTag) {
        if (fragmentManager?.findFragmentByTag(TagEditFragment.TAG) == null) {
            fragmentManager?.inTransaction {
                replace(R.id.container_tags, TagEditFragment.newInstance(tag), TagEditFragment.TAG)
                addToBackStack(null)
            }
        }
    }

    override fun showDeleteTagView(tag: MyTag) {
        if (fragmentManager?.findFragmentByTag(TagDeleteFragment.TAG) == null) {
            fragmentManager?.inTransaction {
                setCustomAnimations(R.anim.scale_up, R.anim.scale_up, R.anim.scale_down, R.anim.scale_down)
                add(R.id.container_tags, TagDeleteFragment.newInstance(tag), TagDeleteFragment.TAG)
                addToBackStack(null)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mPresenter.attachView(this)
        mPresenter.onViewResume(mNoteId)
    }

    override fun onPause() {
        super.onPause()
        mPresenter.detachView()
    }


    companion object {

        const val TAG = "TagListFragment"
        private const val ARG_NOTE_ID = "note_id"

        @JvmStatic
        fun newInstance(noteId: String) =
                TagListFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_NOTE_ID, noteId)
                    }
                }
    }
}
