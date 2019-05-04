package com.furianrt.mydiary.dialogs.tags

import com.furianrt.mydiary.data.DataManager

class TagsDialogPresenter(
        private val dataManager: DataManager
) : TagsDialogContract.Presenter() {

    override fun onViewCreated(noteId: String) {
        view?.showTagListView(noteId)
    }
}