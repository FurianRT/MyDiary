/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.dialogs.categories.fragments.edit

import com.furianrt.mydiary.presentation.base.mvp.BaseMvpView
import com.furianrt.mydiary.presentation.base.mvp.BaseMvpPresenter
import com.furianrt.mydiary.model.entity.MyCategory

interface CategoryEditContract {

    interface View : BaseMvpView {
        fun close()
        fun showCategory(category: MyCategory)
        fun showErrorEmptyName()
    }

    abstract class Presenter : BaseMvpPresenter<View>() {
        abstract fun init(category: MyCategory)
        abstract fun onButtonDoneClick(categoryName: String, categoryColor: Int)
        abstract fun onButtonCancelClick()
    }
}