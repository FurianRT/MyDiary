/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.dialogs.categories.fragments.add

import com.furianrt.mydiary.domain.save.SaveCategoryUseCase
import com.furianrt.mydiary.utils.MyRxUtils
import javax.inject.Inject

class CategoryAddPresenter @Inject constructor(
        private val saveCategory: SaveCategoryUseCase,
        private val scheduler: MyRxUtils.BaseSchedulerProvider
) : CategoryAddContract.Presenter() {

    override fun onButtonDoneClick(categoryName: String, categoryColor: Int) {
        if (categoryName.isBlank()) {
            view?.showErrorEmptyName()
        } else {
            addDisposable(saveCategory.invoke(categoryName, categoryColor)
                    .observeOn(scheduler.ui())
                    .ignoreElement()
                    .subscribe { view?.close() })
        }
    }

    override fun onButtonCancelClick() {
        view?.close()
    }
}