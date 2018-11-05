package com.furianrt.mydiary.note.fragments.notefragment

import android.location.Address
import com.furianrt.mydiary.BasePresenter
import com.furianrt.mydiary.BaseView
import com.furianrt.mydiary.data.api.Forecast
import com.furianrt.mydiary.data.model.*
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

        fun showCategory(category: MyCategory)

        fun showMood(mood: MyMood)

        fun showNoTagsMessage()

        fun showNoCategoryMessage()

        fun showNoMoodMessage()

        fun showLocation(location: MyLocation)

        fun requestStoragePermissions()

        fun showImageExplorer()

        fun showImages(images: List<MyImage>)

        fun showNoImages()

        fun showNoteEditView(note: MyNote)

        fun closeView()

        fun showGalleryView(noteId: String)

        fun showMoodsDialog(moods: List<MyMood>)

        fun showCategoriesDialog(noteId: String)

        fun hideLocation()

        fun hideMood()

        fun showNoteSettingsView(note: MyNote)

        fun updateNoteAppearance(appearance: MyNoteAppearance)

        fun showNoteContent(note: MyNote)
    }

    interface Presenter : BasePresenter<View> {

        fun onTagsFieldClick()

        fun onLocationReceived(result: LocationResult)

        fun onLocationPermissionsGranted()

        fun onAddressFound(addresses: List<Address>, latitude: Double, longitude: Double)

        fun onNoteTagsChanged(tags: List<MyTag>)

        fun onMapReady()

        fun onAddImageButtonClick()

        fun onStoragePermissionsGranted()

        fun onNoteImagesPicked(imageUrls: List<String>)

        fun onEditButtonClick()

        fun onDeleteButtonClick()

        fun onToolbarImageClick()

        fun onMoodFieldClick()

        fun onMoodPicked(mood: MyMood)

        fun onNoMoodPicked()

        fun onCategoryFieldClick()

        fun onNoCategoryPicked()

        fun onCategoryPicked(category: MyCategory)

        fun onAppearanceButtonClick()

        fun loadNote(noteId: String, mode: Mode, locationEnabled: Boolean, networkAvailable: Boolean)

        fun loadNoteAppearance(noteId: String)

        fun loadTags(noteId: String)

        fun loadImages(noteId: String)

        fun updateNoteText(noteId: String, noteTitle: String, noteContent: String)
    }
}