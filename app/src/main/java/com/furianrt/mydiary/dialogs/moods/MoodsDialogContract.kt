package com.furianrt.mydiary.dialogs.moods

import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView
import com.furianrt.mydiary.data.model.MyMood

interface MoodsDialogContract {

    interface View : BaseView {
        fun showMoods(moods: List<MyMood>)
        fun closeView()
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onButtonCloseClick()
        abstract fun onButtonNoMoodClick(noteId: String)
        abstract fun onMoodPicked(noteId: String, mood: MyMood)
    }
}