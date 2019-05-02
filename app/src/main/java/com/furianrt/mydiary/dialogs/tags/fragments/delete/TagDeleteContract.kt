package com.furianrt.mydiary.dialogs.tags.fragments.delete

import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView
import com.furianrt.mydiary.data.model.MyTag

interface TagDeleteContract {

    interface View : BaseView {
        fun closeView()
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onButtonDeleteClick(tag: MyTag)
        abstract fun onButtonCancelClick()
    }
}