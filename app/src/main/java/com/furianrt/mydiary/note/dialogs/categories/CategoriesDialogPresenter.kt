package com.furianrt.mydiary.note.dialogs.categories

import com.furianrt.mydiary.data.DataManager

class CategoriesDialogPresenter(
        private val mDataManager: DataManager
) : CategoriesDialogContract.Presenter {

    private var mView: CategoriesDialogContract.View? = null

    override fun attachView(view: CategoriesDialogContract.View) {
        mView = view
    }

    override fun detachView() {
        super.detachView()
        mView = null
    }

    override fun onViewCreate(noteId: String) {
        mView?.showViewCategoryList(noteId)
    }
}