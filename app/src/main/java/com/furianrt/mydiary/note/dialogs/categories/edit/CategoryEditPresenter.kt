package com.furianrt.mydiary.note.dialogs.categories.edit

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyCategory

class CategoryEditPresenter(private val mDataManager: DataManager) : CategoryEditContract.Presenter {

    private var mView: CategoryEditContract.View? = null

    override fun attachView(view: CategoryEditContract.View) {
        mView = view
    }

    override fun detachView() {
        super.detachView()
        mView = null
    }

    override fun onViewCreate() {

    }

    override fun onButtonDoneClick(category: MyCategory) {
        addDisposable(if (category.id == 0L) {
            mDataManager.insertCategory(category)
                    .ignoreElement()
                    .subscribe { mView?.close() }
        } else {
            mDataManager.updateCategory(category)
                    .subscribe { mView?.close() }
        })
    }

    override fun loadCategory(categoryId: Long) {
        addDisposable(mDataManager.getCategory(categoryId)
                .defaultIfEmpty(MyCategory())
                .subscribe { category -> mView?.showCategory(category) })
    }
}