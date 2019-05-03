package com.furianrt.mydiary.dialogs.tags.fragments.list

import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView
import com.furianrt.mydiary.data.model.MyTag

interface TagListContract {

    interface View : BaseView {
        fun showTags(tags: List<MyTag>)
        fun closeView()
        fun showAddTagView()
        fun showEditTagView(tag: MyTag)
        fun showDeleteTagView(tag: MyTag)
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onViewResume(noteId: String)
        abstract fun onItemCheckChange(noteId: String, tag: MyTag, checked: Boolean)
        abstract fun onButtonCloseClick()
        abstract fun onButtonAddClick()
        abstract fun onButtonEditTagClick(tag: MyTag)
        abstract fun onButtonDeleteTagClick(tag: MyTag)
        abstract fun onSearchQueryChange(newText: String?)
    }
}