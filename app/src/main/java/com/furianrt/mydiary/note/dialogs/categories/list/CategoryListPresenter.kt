package com.furianrt.mydiary.note.dialogs.categories.list

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyCategory
import io.reactivex.disposables.CompositeDisposable

class CategoryListPresenter(
        private val mDataManager: DataManager
) : CategoryListContract.Presenter {

    private var mView: CategoryListContract.View? = null
    private val mCompositeDisposable = CompositeDisposable()

    override fun attachView(view: CategoryListContract.View) {
        mView = view
    }

    override fun detachView() {
        mCompositeDisposable.clear()
        mView = null
    }

    override fun onAddCategoryButtonClick() {
        mView?.showViewAddCategory()
    }

    override fun onDeleteCategoryButtonClick(category: MyCategory) {
        val disposable = mDataManager.deleteCategory(category)
                .subscribe()

        mCompositeDisposable.add(disposable)
    }

    override fun onEditCategoryButtonClick(category: MyCategory) {
        mView?.showEditView(category)
    }

    override fun onViewCreate() {
        val disposable = mDataManager.getAllCategories()
                .subscribe { categories -> mView?.showCategories(categories) }

        mCompositeDisposable.add(disposable)
    }

    override fun onCategoryClick(category: MyCategory, noteId: String) {
        val disposable = mDataManager.findNote(noteId)
                .flatMapCompletable { note ->
                    note.categoryId = category.id
                    return@flatMapCompletable mDataManager.updateNote(note)
                }
                .subscribe { mView?.close() }

        mCompositeDisposable.add(disposable)
    }
}