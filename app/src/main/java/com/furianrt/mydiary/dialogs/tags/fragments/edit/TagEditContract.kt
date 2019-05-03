package com.furianrt.mydiary.dialogs.tags.fragments.edit

import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView
import com.furianrt.mydiary.data.model.MyTag

interface TagEditContract {

    interface View : BaseView {
        fun closeView()
        fun showErrorEmptyTagName()
        fun showErrorExistingTagName()
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onButtonConfirmClick(tag: MyTag, newName: String)
        abstract fun onButtonCloseClick()
    }
}