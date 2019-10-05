/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.dialogs.tags.fragments.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.furianrt.mydiary.R
import com.furianrt.mydiary.view.base.BaseFragment
import com.furianrt.mydiary.data.entity.MyTag
import com.furianrt.mydiary.view.dialogs.tags.TagsDialog
import com.furianrt.mydiary.view.dialogs.tags.fragments.add.TagAddFragment
import com.furianrt.mydiary.view.dialogs.tags.fragments.delete.TagDeleteFragment
import com.furianrt.mydiary.view.dialogs.tags.fragments.edit.TagEditFragment
import com.furianrt.mydiary.utils.inTransaction
import kotlinx.android.synthetic.main.fragment_tag_list.view.*
import javax.inject.Inject

class TagListFragment : BaseFragment(), TagListContract.MvpView,
        TagListAdapter.OnTagListItemInteractionListener {

    @Inject
    lateinit var mPresenter: TagListContract.Presenter

    private val mListAdapter = TagListAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
        mPresenter.init(arguments?.getString(ARG_NOTE_ID)!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tag_list, container, false)

        view.button_tags_close.setOnClickListener { mPresenter.onButtonCloseClick() }
        view.button_tags_add.setOnClickListener { mPresenter.onButtonAddClick() }
        view.search_tags.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

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

    override fun showItems(items: List<TagListAdapter.ViewItem>) {
        mListAdapter.showList(items.toMutableList())
    }

    override fun onItemCheckChange(item: TagListAdapter.ViewItem) {
        mPresenter.onItemCheckChange(item)
    }

    override fun onItemEditClick(item: TagListAdapter.ViewItem) {
        mPresenter.onButtonEditTagClick(item)
    }

    override fun onItemDeleteClick(item: TagListAdapter.ViewItem) {
        mPresenter.onButtonDeleteTagClick(item)
    }

    override fun closeView() {
        (parentFragment as? TagsDialog?)?.dismiss()
    }

    override fun showAddTagView(noteId: String) {
        if (fragmentManager?.findFragmentByTag(TagAddFragment.TAG) == null) {
            fragmentManager?.inTransaction {
                replace(R.id.container_tags, TagAddFragment.newInstance(noteId), TagAddFragment.TAG)
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

    override fun onStart() {
        super.onStart()
        mPresenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
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
