package com.furianrt.mydiary.note.dialogs.tags

import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView
import com.furianrt.mydiary.data.model.MyTag

interface TagsDialogContract {

    interface View : BaseView {

        fun showTags(tags: MutableList<MyTag>)
    }

    abstract class Presenter : BasePresenter<View>() {

        abstract fun setTags(tags: ArrayList<MyTag>)

        abstract fun getTags(): ArrayList<MyTag>

        abstract fun onTagClicked(tag: MyTag)

        abstract fun onButtonAddTagClicked(tagName: String)

        abstract fun onButtonDeleteTagClicked(tag: MyTag)

        abstract fun onButtonEditTagClicked(tag: MyTag)

        abstract fun onViewCreate()
    }
}