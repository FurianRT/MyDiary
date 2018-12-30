package com.furianrt.mydiary.note.dialogs.categories.list

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyCategory

class CategoryListPresenter(
        private val mDataManager: DataManager
) : CategoryListContract.Presenter {

    private var mView: CategoryListContract.View? = null

    override fun attachView(view: CategoryListContract.View) {
        mView = view
    }

    override fun detachView() {
        super.detachView()
        mView = null
    }

    override fun onAddCategoryButtonClick() {
        mView?.showViewAddCategory()
    }

    override fun onDeleteCategoryButtonClick(category: MyCategory) {
        addDisposable(mDataManager.deleteCategory(category)
                .subscribe())
    }

    override fun onEditCategoryButtonClick(category: MyCategory) {
        mView?.showEditView(category)
    }

    override fun onViewStart() {
        addDisposable(mDataManager.getAllCategories()
                .subscribe { categories -> mView?.showCategories(categories) })
    }

    override fun onCategoryClick(category: MyCategory, noteId: String) {
        addDisposable(mDataManager.findNote(noteId)
                .flatMapCompletable { note ->
                    note.categoryId = category.id
                    return@flatMapCompletable mDataManager.updateNote(note)
                }
                .subscribe { mView?.close() })
    }
}