package com.furianrt.mydiary.dialogs.tags.fragments.list

import com.furianrt.mydiary.base.mvp.BaseMvpView
import com.furianrt.mydiary.base.mvp.BaseMvpPresenter
import com.furianrt.mydiary.data.model.MyTag

interface TagListContract {

    interface MvpView : BaseMvpView {
        fun showItems(items: List<TagListAdapter.ViewItem>)
        fun closeView()
        fun showAddTagView(noteId: String)
        fun showEditTagView(tag: MyTag)
        fun showDeleteTagView(tag: MyTag)
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun init(noteId: String)
        abstract fun onItemCheckChange(item: TagListAdapter.ViewItem)
        abstract fun onButtonCloseClick()
        abstract fun onButtonAddClick()
        abstract fun onButtonEditTagClick(item: TagListAdapter.ViewItem)
        abstract fun onButtonDeleteTagClick(item: TagListAdapter.ViewItem)
        abstract fun onSearchQueryChange(newText: String?)
    }
}