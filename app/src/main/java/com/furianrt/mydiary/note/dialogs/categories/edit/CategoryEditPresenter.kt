package com.furianrt.mydiary.note.dialogs.categories.edit

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyCategory
import io.reactivex.android.schedulers.AndroidSchedulers

class CategoryEditPresenter(private val mDataManager: DataManager) : CategoryEditContract.Presenter() {

    override fun onViewCreate() {

    }

    override fun onButtonDoneClick(category: MyCategory) {
        addDisposable(if (category.id == 0L) {
            mDataManager.insertCategory(category)
                    .ignoreElement()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { view?.close() }
        } else {
            mDataManager.updateCategory(category)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { view?.close() }
        })
    }

    override fun loadCategory(categoryId: Long) {
        addDisposable(mDataManager.getCategory(categoryId)
                .defaultIfEmpty(MyCategory())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { category -> view?.showCategory(category) })
    }
}