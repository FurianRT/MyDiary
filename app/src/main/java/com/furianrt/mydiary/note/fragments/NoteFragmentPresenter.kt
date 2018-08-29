package com.furianrt.mydiary.note.fragments

import android.util.Log
import com.furianrt.mydiary.data.DataManager

class NoteFragmentPresenter(private val mDataManager: DataManager) : NoteFragmentContract.Presenter {

    private var mView: NoteFragmentContract.View? = null

    override fun attachView(view: NoteFragmentContract.View) {
        mView = view
    }

    override fun detachView() {
        mView = null
    }

    override fun getForecast() {
        mDataManager.getForecast(50.5912227, 136.9594189)
                .subscribe { forecast ->
                    Log.e("fff", "" + forecast.main.temp)
                    mView?.showForecast(forecast)
                }
    }
}