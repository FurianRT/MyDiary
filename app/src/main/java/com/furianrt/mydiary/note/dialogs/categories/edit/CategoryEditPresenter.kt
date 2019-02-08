package com.furianrt.mydiary.note.dialogs.categories.edit

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyCategory

class CategoryEditPresenter(private val mDataManager: DataManager) : CategoryEditContract.Presenter() {

    override fun onViewCreate() {

    }

    override fun onButtonDoneClick(category: MyCategory) {
        addDisposable(if (category.id == 0L) {
            mDataManager.insertCategory(category)
                    .ignoreElement()
                    .subscribe { view?.close() }
        } else {
            mDataManager.updateCategory(category)
                    .subscribe { view?.close() }
        })
    }

    override fun loadCategory(categoryId: Long) {
        addDisposable(mDataManager.getCategory(categoryId)
                .defaultIfEmpty(MyCategory())
                .subscribe { category -> view?.showCategory(category) })
    }
}