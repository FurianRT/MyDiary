package com.furianrt.mydiary.dialogs.categories.fragments.add

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyCategory
import com.furianrt.mydiary.utils.generateUniqueId
import io.reactivex.android.schedulers.AndroidSchedulers

class CategoryAddPresenter(
        private val dataManager: DataManager
) : CategoryAddContract.Presenter() {

    override fun onButtonDoneClick(categoryName: String, categoryColor: Int) {
        if (categoryName.isBlank()) {
            view?.showErrorEmptyName()
        } else {
            val category = MyCategory(generateUniqueId(), categoryName, categoryColor)
            addDisposable(dataManager.insertCategory(category)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { view?.close() })
        }
    }

    override fun onButtonCancelClick() {
        view?.close()
    }
}