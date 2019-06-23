package com.furianrt.mydiary.dialogs.tags.fragments.delete

import com.furianrt.mydiary.base.BaseMvpView
import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.data.model.MyTag

interface TagDeleteContract {

    interface MvpView : BaseMvpView {
        fun closeView()
    }

    abstract class Presenter : BasePresenter<MvpView>() {
        abstract fun onButtonDeleteClick(tag: MyTag)
        abstract fun onButtonCancelClick()
    }
}