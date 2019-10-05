/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.dialogs.categories.fragments.delete

import com.furianrt.mydiary.data.entity.MyCategory
import com.furianrt.mydiary.domain.delete.DeleteCategoryUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class CategoryDeletePresenter @Inject constructor(
        private val deleteCategory: DeleteCategoryUseCase
) : CategoryDeleteContract.Presenter() {

    private lateinit var mCategory: MyCategory

    override fun init(category: MyCategory) {
        mCategory = category
    }

    override fun attachView(view: CategoryDeleteContract.MvpView) {
        super.attachView(view)
        view.showDeleteMessage(mCategory.name)
    }

    override fun onButtonDeleteClick() {
        addDisposable(deleteCategory.invoke(mCategory)
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe { view?.closeView() })
    }

    override fun onButtonCancelClick() {
        view?.closeView()
    }
}