package com.furianrt.mydiary.note.dialogs.categories

import com.furianrt.mydiary.data.DataManager
import io.reactivex.disposables.CompositeDisposable

class CategoriesDialogPresenter(
        private val mDataManager: DataManager
) : CategoriesDialogContract.Presenter {

    private var mView: CategoriesDialogContract.View? = null
    private val mCompositeDisposable = CompositeDisposable()

    override fun attachView(view: CategoriesDialogContract.View) {
        mView = view
    }

    override fun detachView() {
        mCompositeDisposable.clear()
        mView = null
    }

    override fun onViewCreate(noteId: String) {
        mView?.showViewCategoryList(noteId)
    }
}