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

import com.furianrt.mydiary.model.entity.MyCategory
import com.furianrt.mydiary.model.entity.MyNote
import com.furianrt.mydiary.domain.get.GetCategoriesUseCase
import com.furianrt.mydiary.domain.get.GetNotesUseCase
import com.furianrt.mydiary.domain.update.UpdateNoteUseCase
import com.furianrt.mydiary.utils.MyRxUtils
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.functions.BiFunction
import javax.inject.Inject

class CategoryListPresenter @Inject constructor(
        private val updateNoteUseCase: UpdateNoteUseCase,
        private val getCategoriesUseCase: GetCategoriesUseCase,
        private val getNotesUseCase: GetNotesUseCase,
        private val scheduler: MyRxUtils.BaseSchedulerProvider
) : CategoryListContract.Presenter() {

    private class NotesAndCategories(
            val notes: List<MyNote>,
            val categories: List<MyCategory>
    )

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
        addDisposable(Flowable.combineLatest(
                getNotesUseCase().firstOrError().toFlowable(),
                getCategoriesUseCase(),
                BiFunction<List<MyNote>, List<MyCategory>, NotesAndCategories> { notes, categories ->
                    NotesAndCategories(notes, categories)
                }
        )
                .observeOn(scheduler.ui())
                .subscribe { view?.showCategories(it.notes, it.categories) })
    }

    override fun onCategoryClick(category: MyCategory, noteIds: List<String>) {
        addDisposable(updateNoteUseCase(noteIds, category.id)
                .observeOn(scheduler.ui())
                .subscribe { view?.close() })
    }

    override fun onButtonNoCategoryClick(noteIds: List<String>) {
        addDisposable(updateNoteUseCase(noteIds, "")
                .observeOn(scheduler.ui())
                .subscribe { view?.close() })
    }

    override fun onButtonCloseClick() {
        view?.close()
    }
}