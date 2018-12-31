package com.furianrt.mydiary.note.dialogs.moods

import com.furianrt.mydiary.BasePresenter
import com.furianrt.mydiary.BaseView
import com.furianrt.mydiary.data.model.MyMood

interface MoodsDialogContract {

    interface View : BaseView {

        fun showMoods(moods: List<MyMood>)
    }

    abstract class Presenter : BasePresenter<View>() {

        abstract fun onViewCreate()
    }
}