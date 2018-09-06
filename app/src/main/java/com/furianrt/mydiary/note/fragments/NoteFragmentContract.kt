package com.furianrt.mydiary.note.fragments

import android.location.Address
import com.furianrt.mydiary.BasePresenter
import com.furianrt.mydiary.BaseView
import com.furianrt.mydiary.data.api.Forecast
import com.furianrt.mydiary.data.model.MyNote
import com.furianrt.mydiary.data.model.MyTag
import com.furianrt.mydiary.note.Mode
import com.google.android.gms.location.LocationResult
import java.util.*

interface NoteFragmentContract {

    interface View : BaseView {

        fun showForecast(forecast: Forecast)

        fun showTagsDialog(tags: ArrayList<MyTag>)

        fun showTagNames(tagNames: List<String>)

        fun requestLocationPermissions()

        fun requestLocation()

        fun findAddress(latitude: Double, longitude: Double)

        fun showAddress(address: String)

        fun showAddressNotFound()

        fun showMap()

        fun zoomMap(latitude: Double, longitude: Double)
    }

    interface Presenter : BasePresenter<View> {

        fun onTagsFieldClick(note: MyNote)

        fun loadNoteProperties(note: MyNote)

        fun onLocationReceived(note: MyNote, result: LocationResult)

        fun findLocation(note: MyNote, mode: Mode, locationEnabled: Boolean, networkAvailable: Boolean)

        fun onPermissionsGranted()

        fun onAddressFound(note: MyNote, addresses: List<Address>)

        fun changeNoteTags(note: MyNote, tags: List<MyTag>)

        fun onMapReady(latitude: Double?, longitude: Double?)
    }
}