package com.furianrt.mydiary.view.dialogs.categories.fragments.list

import com.furianrt.mydiary.data.model.MyCategory
import com.furianrt.mydiary.usecase.get.GetCategoriesUseCase
import com.furianrt.mydiary.usecase.update.UpdateNoteUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class CategoryListPresenter @Inject constructor(
        private val updateNote: UpdateNoteUseCase,
        private val getCategories: GetCategoriesUseCase
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
        addDisposable(getCategories.invoke()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { categories -> view?.showCategories(categories) })
    }

    override fun onCategoryClick(category: MyCategory, noteIds: List<String>) {
        addDisposable(updateNote.invoke(noteIds, category.id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.close() })
    }

    override fun onButtonNoCategoryClick(noteIds: List<String>) {
        addDisposable(updateNote.invoke(noteIds, "")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.close() })
    }

    override fun onButtonCloseClick() {
        view?.close()
    }
}