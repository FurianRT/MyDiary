/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.dialogs.tags.fragments.list

import com.furianrt.mydiary.presentation.base.BaseView
import com.furianrt.mydiary.presentation.base.BasePresenter
import com.furianrt.mydiary.model.entity.MyTag

interface TagListContract {

    interface View : BaseView {
        fun showItems(items: List<TagListAdapter.ViewItem>)
        fun closeView()
        fun showAddTagView(noteId: String)
        fun showEditTagView(tag: MyTag)
        fun showDeleteTagView(tag: MyTag)
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun init(noteId: String)
        abstract fun onItemCheckChange(item: TagListAdapter.ViewItem)
        abstract fun onButtonCloseClick()
        abstract fun onButtonAddClick()
        abstract fun onButtonEditTagClick(item: TagListAdapter.ViewItem)
        abstract fun onButtonDeleteTagClick(item: TagListAdapter.ViewItem)
        abstract fun onSearchQueryChange(newText: String?)
    }
}