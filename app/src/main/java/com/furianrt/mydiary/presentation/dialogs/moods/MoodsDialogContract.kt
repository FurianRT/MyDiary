/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.dialogs.moods

import com.furianrt.mydiary.presentation.base.BaseView
import com.furianrt.mydiary.presentation.base.BasePresenter
import com.furianrt.mydiary.model.entity.MyMood
import com.furianrt.mydiary.model.entity.MyNote

interface MoodsDialogContract {

    interface View : BaseView {
        fun showMoods(moods: List<MyMood>, notes: List<MyNote>)
        fun closeView()
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onButtonCloseClick()
        abstract fun onButtonNoMoodClick(noteId: String)
        abstract fun onMoodPicked(noteId: String, mood: MyMood)
    }
}