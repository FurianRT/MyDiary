package com.furianrt.mydiary.note.dialogs

import com.furianrt.mydiary.BasePresenter
import com.furianrt.mydiary.BaseView
import com.furianrt.mydiary.data.model.MyTag

interface TagsDialogContract {

    interface View : BaseView {

        fun showTags(tags: MutableList<MyTag>)
    }

    interface Presenter : BasePresenter<View> {

        fun onTagClicked(tags: MutableList<MyTag>, tag: MyTag)

        fun onButtonAddTagClicked(tagName: String, tags: MutableList<MyTag>)

        fun onButtonDeleteTagClicked(tag: MyTag, tags: MutableList<MyTag>)

        fun onButtonEditTagClicked(tag: MyTag, tags: MutableList<MyTag>)
    }
}