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

        fun showNoteSettingsView(noteId: String)

        fun updateNoteAppearance(appearance: MyNoteAppearance)

        fun showNoteContent(note: MyNote)
    }

    abstract class Presenter : BasePresenter<View>() {

        abstract fun onTagsFieldClick()

        abstract fun onLocationReceived(result: LocationResult)

        abstract fun onLocationPermissionsGranted()

        abstract fun onAddressFound(addresses: List<Address>, latitude: Double, longitude: Double)

        abstract fun onNoteTagsChanged(tags: List<MyTag>)

        abstract fun onMapReady()

        abstract fun onAddImageButtonClick()

        abstract fun onStoragePermissionsGranted()

        abstract fun onNoteImagesPicked(imageUrls: List<String>)

        abstract fun onEditButtonClick()

        abstract fun onDeleteButtonClick()

        abstract fun onToolbarImageClick()

        abstract fun onMoodFieldClick()

        abstract fun onMoodPicked(mood: MyMood)

        abstract fun onNoMoodPicked()

        abstract fun onCategoryFieldClick()

        abstract fun onNoCategoryPicked()

        abstract fun onCategoryPicked(category: MyCategory)

        abstract fun onAppearanceButtonClick()

        abstract fun loadNote(noteId: String, mode: Mode, locationEnabled: Boolean, networkAvailable: Boolean)

        abstract fun loadNoteAppearance(noteId: String)

        abstract fun loadTags(noteId: String)

        abstract fun loadImages(noteId: String)

        abstract fun updateNoteText(noteId: String, noteTitle: String, noteContent: String)
    }
}