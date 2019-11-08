/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.dialogs.categories.fragments.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.furianrt.mydiary.R
import com.furianrt.mydiary.presentation.base.BaseFragment
import com.furianrt.mydiary.model.entity.MyCategory
import com.furianrt.mydiary.model.entity.MyNote
import com.furianrt.mydiary.presentation.dialogs.categories.CategoriesDialog
import com.furianrt.mydiary.presentation.dialogs.categories.fragments.add.CategoryAddFragment
import com.furianrt.mydiary.presentation.dialogs.categories.fragments.delete.CategoryDeleteFragment
import com.furianrt.mydiary.presentation.dialogs.categories.fragments.edit.CategoryEditFragment
import com.furianrt.mydiary.utils.inTransaction
import kotlinx.android.synthetic.main.fragment_category_list.*
import javax.inject.Inject

class CategoryListFragment : BaseFragment(R.layout.fragment_category_list),
        CategoriesListAdapter.OnCategoryListInteractionListener, CategoryListContract.MvpView {

    companion object {

        const val TAG = "CategoryListFragment"
        private const val ARG_NOTE_IDS = "noteIds"

        @JvmStatic
        fun newInstance(noteIds: List<String>) =
                CategoryListFragment().apply {
                    arguments = Bundle().apply {
                        putStringArrayList(ARG_NOTE_IDS, ArrayList(noteIds))
                    }
                }
    }

    @Inject
    lateinit var mPresenter: CategoryListContract.Presenter

    private val mListAdapter = CategoriesListAdapter(this)
    private lateinit var mNoteIds: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getPresenterComponent(requireContext()).inject(this)
        mNoteIds = requireArguments().getStringArrayList(ARG_NOTE_IDS)!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button_add_category.setOnClickListener { mPresenter.onButtonAddCategoryClick() }
        button_categories_close.setOnClickListener { mPresenter.onButtonCloseClick() }
        val manager = LinearLayoutManager(context)
        list_categories.layoutManager = manager
        list_categories.adapter = mListAdapter
        list_categories.addItemDecoration(DividerItemDecoration(context, manager.orientation))
    }

    override fun onStart() {
        super.onStart()
        mPresenter.attachView(this)
        mPresenter.onViewStart()
    }

    override fun onStop() {
        super.onStop()
        mPresenter.detachView()
    }

    override fun showViewAddCategory() {
        replaceFragment(CategoryAddFragment(), CategoryAddFragment.TAG)
    }

    override fun showCategories(notes: List<MyNote>, categories: List<MyCategory>) {
        val items = categories
                .map { category ->
                    val count = notes.count { note -> note.categoryId == category.id }
                    CategoriesListAdapter.CategoryItemView(category, count)
                }
                .sortedByDescending { it.noteCount }
                .toMutableList()
                .apply {
                    val count = notes.count { note -> note.categoryId.isEmpty() }
                    add(CategoriesListAdapter.CategoryItemView(noteCount = count))
                }
        mListAdapter.submitList(items)
    }

    override fun onCategoryClick(category: MyCategory) {
        (parentFragment as? CategoriesDialog?)?.onCategorySelected()
        mPresenter.onCategoryClick(category, mNoteIds)
    }

    override fun onNoCategoryClick() {
        (parentFragment as? CategoriesDialog?)?.onCategorySelected()
        mPresenter.onButtonNoCategoryClick(mNoteIds)
    }

    override fun onCategoryDelete(category: MyCategory) {
        mPresenter.onButtonDeleteCategoryClick(category)
    }

    override fun onCategoryEdit(category: MyCategory) {
        mPresenter.onButtonEditCategoryClick(category)
    }

    override fun showEditView(category: MyCategory) {
        replaceFragment(CategoryEditFragment.newInstance(category), CategoryEditFragment.TAG)
    }

    override fun showDeleteCategoryView(category: MyCategory) {
        addFragment(CategoryDeleteFragment.newInstance(category), CategoryDeleteFragment.TAG)
    }

    override fun close() {
        (parentFragment as? DialogFragment?)?.dismiss()
    }

    private fun replaceFragment(fragment: Fragment, tag: String) {
        fragmentManager?.let {
            if (it.findFragmentByTag(tag) == null) {
                it.inTransaction {
                    replace(R.id.container_categories, fragment, tag)
                    addToBackStack(null)
                }
            }
        }
    }

    private fun addFragment(fragment: Fragment, tag: String) {
        fragmentManager?.let {
            if (it.findFragmentByTag(tag) == null) {
                it.inTransaction {
                    setCustomAnimations(R.anim.scale_up, R.anim.scale_up, R.anim.scale_down, R.anim.scale_down)
                    add(R.id.container_categories, fragment, tag)
                    addToBackStack(null)
                }
            }
        }
    }
}
