package com.furianrt.mydiary.note.dialogs.categories.edit

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyCategory
import io.reactivex.disposables.CompositeDisposable

class CategoryEditPresenter(private val mDataManager: DataManager) : CategoryEditContract.Presenter {

    private var mView: CategoryEditContract.View? = null
    private val mCompositeDisposable = CompositeDisposable()

    override fun attachView(view: CategoryEditContract.View) {
        mView = view
    }

    override fun detachView() {
        mCompositeDisposable.clear()
        mView = null
    }

    override fun onViewCreate() {

    }

    override fun onButtonDoneClick(category: MyCategory) {
        val disposable = if (category.id == 0L) {
            mDataManager.insertCategory(category)
                    .subscribe { _ -> mView?.close() }
        } else {
            mDataManager.updateCategory(category)
                    .subscribe { mView?.close() }
        }

        mCompositeDisposable.add(disposable)
    }

    override fun loadCategory(categoryId: Long) {
        val disposable = mDataManager.getCategory(categoryId)
                .defaultIfEmpty(MyCategory())
                .subscribe { category -> mView?.showCategory(category) }

        mCompositeDisposable.add(disposable)
    }
}