/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.dialogs.categories.fragments.list

import com.furianrt.mydiary.presentation.base.mvp.BaseMvpView
import com.furianrt.mydiary.presentation.base.mvp.BaseMvpPresenter
import com.furianrt.mydiary.model.entity.MyCategory
import com.furianrt.mydiary.model.entity.MyNote

interface CategoryListContract {

    interface View : BaseMvpView {
        fun showCategories(notes: List<MyNote>, categories: List<MyCategory>)
        fun showViewAddCategory()
        fun showEditView(category: MyCategory)
        fun close()
        fun showDeleteCategoryView(category: MyCategory)
    }

    abstract class Presenter : BaseMvpPresenter<View>() {
        abstract fun onViewStart()
        abstract fun onButtonAddCategoryClick()
        abstract fun onButtonDeleteCategoryClick(category: MyCategory)
        abstract fun onButtonEditCategoryClick(category: MyCategory)
        abstract fun onCategoryClick(category: MyCategory, noteIds: List<String>)
        abstract fun onButtonNoCategoryClick(noteIds: List<String>)
        abstract fun onButtonCloseClick()
    }
}