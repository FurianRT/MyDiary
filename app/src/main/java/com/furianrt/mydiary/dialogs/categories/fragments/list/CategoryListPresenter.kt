package com.furianrt.mydiary.dialogs.categories.fragments.list

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyCategory
import io.reactivex.android.schedulers.AndroidSchedulers

class CategoryListPresenter(
        private val dataManager: DataManager
) : CategoryListContract.Presenter() {

    override fun onButtonAddCategoryClick() {
        view?.showViewAddCategory()
    }

    override fun onButtonDeleteCategoryClick(category: MyCategory) {
        view?.showDeleteCategoryView(category)
    }

    override fun onButtonEditCategoryClick(category: MyCategory) {
        view?.showEditView(category)
    }

    override fun onViewStart() {
        addDisposable(dataManager.getAllCategories()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { categories -> view?.showCategories(categories) })
    }

    override fun onCategoryClick(category: MyCategory, noteId: String) {
        addDisposable(dataManager.getNote(noteId)
                .firstOrError()
                .flatMapCompletable { note ->
                    note.categoryId = category.id
                    return@flatMapCompletable dataManager.updateNote(note)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.close() })
    }

    override fun onButtonNoCategoryClick(noteId: String) {
        addDisposable(dataManager.getNote(noteId)
                .firstOrError()
                .flatMapCompletable { dataManager.updateNote(it.apply { categoryId = "" }) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.close() })
    }

    override fun onButtonCloseClick() {
        view?.close()
    }
}