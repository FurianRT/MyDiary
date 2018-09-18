package com.furianrt.mydiary.note.fragments.notefragment

import android.location.Address
import com.furianrt.mydiary.BasePresenter
import com.furianrt.mydiary.BaseView
import com.furianrt.mydiary.data.api.Forecast
import com.furianrt.mydiary.data.model.MyImage
import com.furianrt.mydiary.data.model.MyLocation
import com.furianrt.mydiary.data.model.MyNoteWithProp
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

        fun zoomMap(latitude: Double, longitude: Double)

        fun showCategoryName(name: String?)

        fun showMood(name: String?)

        fun showNoTagsMessage()

        fun showNoCategoryMessage()

        fun showNoMoodMessage()

        fun showLocation(location: MyLocation)

        fun requestStoragePermissions()

        fun showImageExplorer()

        fun showImages(images: List<MyImage>)
    }

    interface Presenter : BasePresenter<View> {

        fun onTagsFieldClick()

        fun onLocationReceived(result: LocationResult)

        fun onLocationPermissionsGranted()

        fun onAddressFound(addresses: List<Address>, latitude: Double, longitude: Double)

        fun onNoteTagsChanged(tags: List<MyTag>)

        fun onMapReady()

        fun getNote(): MyNoteWithProp

        fun setNote(note: MyNoteWithProp)

        fun onViewCreated(mode: Mode, locationEnabled: Boolean, networkAvailable: Boolean)

        fun onAddImageButtonClick()

        fun onStoragePermissionsGranted()

        fun onNoteImagesPicked(imageUrls: List<String>)
    }
}