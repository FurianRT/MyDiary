package com.furianrt.mydiary.dialogs.tags.fragments.delete

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyTag

class TagDeletePresenter(
        private val dataManager: DataManager
) : TagDeleteContract.Presenter() {

    override fun onButtonDeleteClick(tag: MyTag) {

    }

    override fun onButtonCancelClick() {

    }
}