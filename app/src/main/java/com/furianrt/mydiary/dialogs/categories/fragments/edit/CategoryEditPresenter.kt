package com.furianrt.mydiary.dialogs.categories.fragments.edit

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyCategory
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class CategoryEditPresenter @Inject constructor(
        private val dataManager: DataManager
) : CategoryEditContract.Presenter() {

    override fun onButtonDoneClick(category: MyCategory, categoryName: String, categoryColor: Int) {
        if (categoryName.isEmpty()) {
            view?.showErrorEmptyName()
        } else {
            category.name = categoryName
            category.color = categoryColor
            addDisposable(dataManager.updateCategory(category)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { view?.close() })
        }
    }

    override fun onButtonCancelClick() {
        view?.close()
    }
}