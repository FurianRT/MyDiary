package com.furianrt.mydiary.note.fragments

import com.furianrt.mydiary.BasePresenter
import com.furianrt.mydiary.BaseView
import com.furianrt.mydiary.data.api.Forecast
import com.furianrt.mydiary.data.model.MyNote
import com.furianrt.mydiary.data.model.MyTag
import com.google.android.gms.maps.model.LatLng

interface NoteFragmentContract {

    interface View : BaseView {

        fun showForecast(forecast: Forecast?)

        fun showTagsDialog(tags: ArrayList<MyTag>)

        fun showTagNames(tagNames: List<String>)
    }

    interface Presenter : BasePresenter<View> {

        fun getForecast(coords: LatLng)

        fun onTagsFieldClick(note: MyNote)

        fun changeNoteTags(note: MyNote, tags: List<MyTag>)

        fun loadNoteProperties(note: MyNote)
    }
}