package com.furianrt.mydiary.dialogs.moods

import com.furianrt.mydiary.base.BaseMvpView
import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.data.model.MyMood

interface MoodsDialogContract {

    interface MvpView : BaseMvpView {
        fun showMoods(moods: List<MyMood>)
        fun closeView()
    }

    abstract class Presenter : BasePresenter<MvpView>() {
        abstract fun onButtonCloseClick()
        abstract fun onButtonNoMoodClick(noteId: String)
        abstract fun onMoodPicked(noteId: String, mood: MyMood)
    }
}