/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.dialogs.categories.fragments.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.furianrt.mydiary.R
import com.furianrt.mydiary.view.base.BaseFragment
import com.furianrt.mydiary.data.entity.MyCategory
import com.furianrt.mydiary.view.dialogs.categories.CategoriesDialog
import com.furianrt.mydiary.view.dialogs.categories.fragments.add.CategoryAddFragment
import com.furianrt.mydiary.view.dialogs.categories.fragments.delete.CategoryDeleteFragment
import com.furianrt.mydiary.view.dialogs.categories.fragments.edit.CategoryEditFragment
import com.furianrt.mydiary.utils.inTransaction
import kotlinx.android.synthetic.main.fragment_category_list.view.*
import javax.inject.Inject

class CategoryListFragment : BaseFragment(), CategoriesListAdapter.OnCategoryListInteractionListener,
        CategoryListContract.MvpView {

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
        mNoteIds = arguments?.getStringArrayList(ARG_NOTE_IDS) ?: throw IllegalArgumentException()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_category_list, container, false)

        view.button_add_category.setOnClickListener { mPresenter.onButtonAddCategoryClick() }
        view.button_categories_close.setOnClickListener { mPresenter.onButtonCloseClick() }
        view.button_no_category.setOnClickListener {
            (parentFragment as? CategoriesDialog?)?.onCategorySelected()
            mPresenter.onButtonNoCategoryClick(mNoteIds)
        }
        with(view.list_categories) {
            val manager = LinearLayoutManager(context)
            layoutManager = manager
            adapter = mListAdapter
            addItemDecoration(DividerItemDecoration(context, manager.orientation))
        }

        return view
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

    override fun showCategories(categories: List<MyCategory>) {
        mListAdapter.submitList(categories)
    }

    override fun onCategoryClick(category: MyCategory) {
        (parentFragment as? CategoriesDialog?)?.onCategorySelected()
        mPresenter.onCategoryClick(category, mNoteIds)
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
