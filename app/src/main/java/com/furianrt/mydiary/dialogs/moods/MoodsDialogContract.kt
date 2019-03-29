package com.furianrt.mydiary.dialogs.moods

import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView
import com.furianrt.mydiary.data.model.MyMood

interface MoodsDialogContract {

    interface View : BaseView {

        fun showMoods(moods: List<MyMood>)
    }

    abstract class Presenter : BasePresenter<View>() {

        abstract fun onViewCreate()
    }
}