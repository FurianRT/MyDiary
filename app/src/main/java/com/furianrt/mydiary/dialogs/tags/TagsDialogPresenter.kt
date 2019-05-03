package com.furianrt.mydiary.dialogs.tags

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyTag

class TagsDialogPresenter(
        private val dataManager: DataManager
) : TagsDialogContract.Presenter() {

    private lateinit var mTags: ArrayList<MyTag>

    override fun onViewCreated(noteId: String) {
        view?.showTagListView(noteId)
    }
}