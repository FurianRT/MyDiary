package com.furianrt.mydiary.dialogs.categories.fragments.list

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyCategory
import io.reactivex.android.schedulers.AndroidSchedulers

class CategoryListPresenter(
        private val dataManager: DataManager
) : CategoryListContract.Presenter() {

    override fun onAddCategoryButtonClick() {
        view?.showViewAddCategory()
    }

    override fun onDeleteCategoryButtonClick(category: MyCategory) {
        addDisposable(dataManager.deleteCategory(category)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe())
    }

    override fun onEditCategoryButtonClick(category: MyCategory) {
        view?.showEditView(category)
    }

    override fun onViewStart() {
        addDisposable(dataManager.getAllCategories()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { categories -> view?.showCategories(categories) })
    }

    override fun onCategoryClick(category: MyCategory, noteId: String) {
        addDisposable(dataManager.findNote(noteId)
                .flatMapCompletable { note ->
                    note.categoryId = category.id
                    return@flatMapCompletable dataManager.updateNote(note)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.close() })
    }
}