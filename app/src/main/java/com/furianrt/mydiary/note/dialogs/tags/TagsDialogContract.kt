package com.furianrt.mydiary.note.dialogs.tags

import com.furianrt.mydiary.BasePresenter
import com.furianrt.mydiary.BaseView
import com.furianrt.mydiary.data.model.MyTag

interface TagsDialogContract {

    interface View : BaseView {

        fun showTags(tags: MutableList<MyTag>)
    }

    interface Presenter : BasePresenter<View> {

        fun setTags(tags: ArrayList<MyTag>)

        fun getTags(): ArrayList<MyTag>

        fun onTagClicked(tag: MyTag)

        fun onButtonAddTagClicked(tagName: String)

        fun onButtonDeleteTagClicked(tag: MyTag)

        fun onButtonEditTagClicked(tag: MyTag)

        fun onViewCreate()
    }
}