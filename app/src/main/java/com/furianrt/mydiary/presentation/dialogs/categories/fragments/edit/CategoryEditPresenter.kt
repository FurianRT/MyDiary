/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.dialogs.categories.fragments.edit

import com.furianrt.mydiary.model.entity.MyCategory
import com.furianrt.mydiary.domain.update.UpdateCategoryUseCase
import com.furianrt.mydiary.utils.MyRxUtils
import javax.inject.Inject

class CategoryEditPresenter @Inject constructor(
        private val updateCategory: UpdateCategoryUseCase,
        private val scheduler: MyRxUtils.BaseSchedulerProvider
) : CategoryEditContract.Presenter() {

    private lateinit var mCategory: MyCategory

    override fun init(category: MyCategory) {
        mCategory = category
    }

    override fun attachView(view: CategoryEditContract.MvpView) {
        super.attachView(view)
        view.showCategory(mCategory)
    }

    override fun onButtonDoneClick(categoryName: String, categoryColor: Int) {
        if (categoryName.isEmpty()) {
            view?.showErrorEmptyName()
        } else {
            mCategory.name = categoryName
            mCategory.color = categoryColor
            addDisposable(updateCategory.invoke(mCategory)
                    .observeOn(scheduler.ui())
                    .subscribe { view?.close() })
        }
    }

    override fun onButtonCancelClick() {
        view?.close()
    }
}