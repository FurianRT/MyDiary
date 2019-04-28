package com.furianrt.mydiary.dialogs.categories

import com.furianrt.mydiary.data.DataManager

class CategoriesDialogPresenter(
        private val dataManager: DataManager
) : CategoriesDialogContract.Presenter() {

    override fun onViewCreate(noteId: String) {
        view?.showViewCategoryList(noteId)
    }
}