package com.furianrt.mydiary.dialogs.categories.fragments.list

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyCategory
import com.furianrt.mydiary.dialogs.categories.fragments.edit.CategoryEditFragment
import com.furianrt.mydiary.utils.inTransaction
import kotlinx.android.synthetic.main.fragment_category_list.*
import kotlinx.android.synthetic.main.fragment_category_list.view.*
import javax.inject.Inject

class CategoryListFragment : Fragment(), View.OnClickListener,
        CategoriesListAdapter.OnCategoryListInteractionListener, CategoryListContract.View {

    @Inject
    lateinit var mPresenter: CategoryListContract.Presenter

    private val mListAdapter = CategoriesListAdapter(this)
    private lateinit var mNoteId: String
    private var mRecyclerViewState: Parcelable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getPresenterComponent(requireContext()).inject(this)
        mNoteId = arguments?.getString(ARG_NOTE_ID) ?: throw IllegalArgumentException()
        mRecyclerViewState = savedInstanceState?.getParcelable(BUNDLE_RECYCLER_VIEW_STATE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_category_list, container, false)

        view.button_add_category.setOnClickListener(this@CategoryListFragment)
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
        fragmentManager?.apply {
            if (findFragmentByTag(CategoryEditFragment.TAG) == null) {
                inTransaction {
                    replace(R.id.container_categories, CategoryEditFragment(), CategoryEditFragment.TAG)
                    addToBackStack(null)
                }
            }
        }
    }

    override fun showCategories(categories: List<MyCategory>) {
        mRecyclerViewState?.let {
            list_categories.layoutManager?.onRestoreInstanceState(mRecyclerViewState)
            mRecyclerViewState = null
        }
        mListAdapter.submitList(categories)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.button_add_category -> mPresenter.onAddCategoryButtonClick()
        }
    }

    override fun onCategoryClick(category: MyCategory) {
        mPresenter.onCategoryClick(category, mNoteId)
    }

    override fun onCategoryDelete(category: MyCategory) {
        mPresenter.onDeleteCategoryButtonClick(category)
    }

    override fun onCategoryEdit(category: MyCategory) {
        mPresenter.onEditCategoryButtonClick(category)
    }

    override fun showEditView(category: MyCategory) {
        fragmentManager?.apply {
            if (findFragmentByTag(CategoryEditFragment.TAG) == null) {
                inTransaction {
                    replace(R.id.container_categories, CategoryEditFragment.newInstance(category.id),
                            CategoryEditFragment.TAG)
                    addToBackStack(null)
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(BUNDLE_RECYCLER_VIEW_STATE,
                view?.list_categories?.layoutManager?.onSaveInstanceState())
    }

    override fun close() {
        (parentFragment as? DialogFragment?)?.dismiss()
    }

    companion object {

        const val TAG = "CategoryListFragment"
        private const val ARG_NOTE_ID = "noteId"
        private const val BUNDLE_RECYCLER_VIEW_STATE = "recyclerState"

        @JvmStatic
        fun newInstance(noteId: String) =
                CategoryListFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_NOTE_ID, noteId)
                    }
                }
    }
}
