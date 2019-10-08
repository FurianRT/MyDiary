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

import com.furianrt.mydiary.data.entity.MyCategory
import com.furianrt.mydiary.domain.get.GetCategoriesUseCase
import com.furianrt.mydiary.domain.update.UpdateNoteUseCase
import com.furianrt.mydiary.utils.MyRxUtils
import javax.inject.Inject

class CategoryListPresenter @Inject constructor(
        private val updateNote: UpdateNoteUseCase,
        private val getCategories: GetCategoriesUseCase,
        private val scheduler: MyRxUtils.BaseSchedulerProvider
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
                .observeOn(scheduler.ui())
                .subscribe { categories -> view?.showCategories(categories) })
    }

    override fun onCategoryClick(category: MyCategory, noteIds: List<String>) {
        addDisposable(updateNote.invoke(noteIds, category.id)
                .observeOn(scheduler.ui())
                .subscribe { view?.close() })
    }

    override fun onButtonNoCategoryClick(noteIds: List<String>) {
        addDisposable(updateNote.invoke(noteIds, "")
                .observeOn(scheduler.ui())
                .subscribe { view?.close() })
    }

    override fun onButtonCloseClick() {
        view?.close()
    }
}