package com.furianrt.mydiary.note.fragments

import com.furianrt.mydiary.BasePresenter
import com.furianrt.mydiary.BaseView
import com.furianrt.mydiary.data.api.Forecast

interface NoteFragmentContract {

    interface View : BaseView {

        fun showForecast(forecast: Forecast?)
    }

    interface Presenter : BasePresenter<View> {

        fun getForecast()
    }
}