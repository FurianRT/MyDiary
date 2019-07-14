package com.furianrt.mydiary.dialogs.tags.fragments.edit

import com.furianrt.mydiary.base.mvp.BaseMvpView
import com.furianrt.mydiary.base.mvp.BaseMvpPresenter
import com.furianrt.mydiary.data.model.MyTag

interface TagEditContract {

    interface MvpView : BaseMvpView {
        fun closeView()
        fun showErrorEmptyTagName()
        fun showErrorExistingTagName()
        fun showTagName(name: String)
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun init(tag: MyTag)
        abstract fun onButtonConfirmClick(newTagName: String)
        abstract fun onButtonCloseClick()
    }
}