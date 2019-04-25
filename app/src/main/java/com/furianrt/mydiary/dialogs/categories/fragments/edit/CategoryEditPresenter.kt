package com.furianrt.mydiary.dialogs.categories.fragments.edit

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyCategory
import com.furianrt.mydiary.utils.generateUniqueId
import io.reactivex.android.schedulers.AndroidSchedulers

class CategoryEditPresenter(private val mDataManager: DataManager) : CategoryEditContract.Presenter() {

    override fun onViewCreate() {

    }

    override fun onButtonDoneClick(category: MyCategory) {
        addDisposable(if (category.id.isEmpty()) {
            category.id = generateUniqueId()
            mDataManager.insertCategory(category)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { view?.close() }
        } else {
            mDataManager.updateCategory(category)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { view?.close() }
        })
    }

    override fun loadCategory(categoryId: String) {
        addDisposable(mDataManager.getCategory(categoryId)
                .onErrorReturn { MyCategory("", "") }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { category -> view?.showCategory(category) })
    }
}