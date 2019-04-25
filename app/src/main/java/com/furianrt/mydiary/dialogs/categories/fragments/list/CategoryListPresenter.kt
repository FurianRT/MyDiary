package com.furianrt.mydiary.dialogs.categories.fragments.list

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyCategory
import io.reactivex.android.schedulers.AndroidSchedulers

class CategoryListPresenter(
        private val mDataManager: DataManager
) : CategoryListContract.Presenter() {

    override fun onAddCategoryButtonClick() {
        view?.showViewAddCategory()
    }

    override fun onDeleteCategoryButtonClick(category: MyCategory) {
        addDisposable(mDataManager.deleteCategory(category)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe())
    }

    override fun onEditCategoryButtonClick(category: MyCategory) {
        view?.showEditView(category)
    }

    override fun onViewStart() {
        addDisposable(mDataManager.getAllCategories()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { categories -> view?.showCategories(categories) })
    }

    override fun onCategoryClick(category: MyCategory, noteId: String) {
        addDisposable(mDataManager.findNote(noteId)
                .flatMapCompletable { note ->
                    note.categoryId = category.id
                    return@flatMapCompletable mDataManager.updateNote(note)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.close() })
    }
}