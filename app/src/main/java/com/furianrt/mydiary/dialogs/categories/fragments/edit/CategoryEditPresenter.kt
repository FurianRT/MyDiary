package com.furianrt.mydiary.dialogs.categories.fragments.edit

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyCategory
import com.furianrt.mydiary.utils.generateUniqueId
import io.reactivex.android.schedulers.AndroidSchedulers

class CategoryEditPresenter(
        private val dataManager: DataManager
) : CategoryEditContract.Presenter() {

    override fun onButtonDoneClick(category: MyCategory) {
        addDisposable(if (category.id.isEmpty()) {
            category.id = generateUniqueId()
            dataManager.insertCategory(category)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { view?.close() }
        } else {
            dataManager.updateCategory(category)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { view?.close() }
        })
    }

    override fun loadCategory(categoryId: String) {
        addDisposable(dataManager.getCategory(categoryId)
                .onErrorReturn { MyCategory("", "") }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { category -> view?.showCategory(category) })
    }
}