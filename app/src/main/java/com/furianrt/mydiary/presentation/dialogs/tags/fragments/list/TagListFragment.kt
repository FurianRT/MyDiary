/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.dialogs.tags.fragments.list

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.furianrt.mydiary.R
import com.furianrt.mydiary.presentation.base.BaseFragment
import com.furianrt.mydiary.model.entity.MyTag
import com.furianrt.mydiary.presentation.dialogs.tags.TagsDialog
import com.furianrt.mydiary.presentation.dialogs.tags.fragments.add.TagAddFragment
import com.furianrt.mydiary.presentation.dialogs.tags.fragments.delete.TagDeleteFragment
import com.furianrt.mydiary.presentation.dialogs.tags.fragments.edit.TagEditFragment
import com.furianrt.mydiary.utils.inTransaction
import com.furianrt.mydiary.utils.replaceFragmentIfNot
import kotlinx.android.synthetic.main.fragment_tag_list.*
import javax.inject.Inject

class TagListFragment : BaseFragment(R.layout.fragment_tag_list), TagListContract.View,
        TagListAdapter.OnTagListItemInteractionListener {

    @Inject
    lateinit var presenter: TagListContract.Presenter

    private val mListAdapter = TagListAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
        presenter.init(requireArguments().getString(ARG_NOTE_ID)!!)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button_tags_close.setOnClickListener { presenter.onButtonCloseClick() }
        button_tags_add.setOnClickListener { presenter.onButtonAddClick() }
        search_tags.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                presenter.onSearchQueryChange(newText)
                return true
            }
        })

        val manager = LinearLayoutManager(context)
        list_tags.layoutManager = manager
        list_tags.adapter = mListAdapter
        list_tags.addItemDecoration(DividerItemDecoration(context, manager.orientation))
    }

    override fun showItems(items: List<TagListAdapter.ViewItem>) {
        mListAdapter.showList(items.toMutableList())
    }

    override fun onItemCheckChange(item: TagListAdapter.ViewItem) {
        presenter.onItemCheckChange(item)
    }

    override fun onItemEditClick(item: TagListAdapter.ViewItem) {
        presenter.onButtonEditTagClick(item)
    }

    override fun onItemDeleteClick(item: TagListAdapter.ViewItem) {
        presenter.onButtonDeleteTagClick(item)
    }

    override fun closeView() {
        (parentFragment as? TagsDialog?)?.dismiss()
    }

    override fun showAddTagView(noteId: String) {
        replaceFragmentIfNot(R.id.container_tags, TagAddFragment.newInstance(noteId), TagAddFragment.TAG, true)
    }

    override fun showEditTagView(tag: MyTag) {
        replaceFragmentIfNot(R.id.container_tags, TagEditFragment.newInstance(tag), TagEditFragment.TAG, true)
    }

    override fun showDeleteTagView(tag: MyTag) {
        if (parentFragmentManager.findFragmentByTag(TagDeleteFragment.TAG) == null) {
            parentFragmentManager.inTransaction {
                setCustomAnimations(R.anim.scale_up, R.anim.scale_up, R.anim.scale_down, R.anim.scale_down)
                add(R.id.container_tags, TagDeleteFragment.newInstance(tag), TagDeleteFragment.TAG)
                addToBackStack(null)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        presenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        presenter.detachView()
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
