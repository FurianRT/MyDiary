package com.furianrt.mydiary.view.dialogs.categories.fragments.add

import com.furianrt.mydiary.usecase.save.SaveCategoryUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class CategoryAddPresenter @Inject constructor(
        private val saveCategory: SaveCategoryUseCase
) : CategoryAddContract.Presenter() {

    override fun onButtonDoneClick(categoryName: String, categoryColor: Int) {
        if (categoryName.isBlank()) {
            view?.showErrorEmptyName()
        } else {
            addDisposable(saveCategory.invoke(categoryName, categoryColor)
                    .observeOn(AndroidSchedulers.mainThread())
                    .ignoreElement()
                    .subscribe { view?.close() })
        }
    }

    override fun onButtonCancelClick() {
        view?.close()
    }
}