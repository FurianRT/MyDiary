package com.furianrt.mydiary.dialogs.tags

import com.furianrt.mydiary.data.DataManager
import javax.inject.Inject

class TagsDialogPresenter @Inject constructor(
        private val dataManager: DataManager
) : TagsDialogContract.Presenter() {

    override fun onViewCreated(noteId: String) {
        view?.showTagListView(noteId)
    }
}