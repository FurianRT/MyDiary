package com.furianrt.mydiary.view.dialogs.tags.fragments.delete

import com.furianrt.mydiary.view.base.mvp.BaseMvpView
import com.furianrt.mydiary.view.base.mvp.BaseMvpPresenter
import com.furianrt.mydiary.data.model.MyTag

interface TagDeleteContract {

    interface MvpView : BaseMvpView {
        fun closeView()
        fun showTagName(name: String)
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun init(tag: MyTag)
        abstract fun onButtonDeleteClick()
        abstract fun onButtonCancelClick()
    }
}