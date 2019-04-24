package com.furianrt.mydiary.note.fragments.notefragment

import android.location.Address
import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView
import com.furianrt.mydiary.data.api.forecast.Forecast
import com.furianrt.mydiary.data.model.*
import com.furianrt.mydiary.note.NoteActivity
import com.google.android.gms.location.LocationResult
import java.util.*

interface NoteFragmentContract {

    interface View : BaseView {
        fun showForecast(forecast: Forecast)
        fun showTagsDialog(tags: ArrayList<MyTag>)
        fun showTags(tagsAndAppearance: TagsAndAppearance)
        fun requestLocationPermissions()
        fun requestLocation()
        fun findAddress(latitude: Double, longitude: Double)
        fun zoomMap(latitude: Double, longitude: Double)
        fun showCategory(category: MyCategory)
        fun showMood(mood: MyMood)
        fun showNoTagsMessage(appearance: MyNoteAppearance)
        fun showNoCategoryMessage()
        fun showNoMoodMessage()
        fun showLocation(location: MyLocation)
        fun requestStoragePermissions()
        fun showImageExplorer()
        fun showImages(images: List<MyImage>)
        fun showNoImages()
        fun closeView()
        fun showGalleryView(noteId: String)
        fun showMoodsDialog(moods: List<MyMood>)
        fun showCategoriesDialog(noteId: String)
        fun hideLocation()
        fun hideMood()
        fun showNoteSettingsView(noteId: String)
        fun updateNoteAppearance(appearance: MyNoteAppearance)
        fun showNoteText(title: String, content: String)
        fun showDatePicker(calendar: Calendar)
        fun showTimePicker(hourOfDay: Int, minute: Int, is24HourMode: Boolean)
        fun showDateAndTime(time: Long, is24TimeFormat: Boolean)
        fun shoNoteEditView()
        fun showLoading()
        fun hideLoading()
        fun showDeleteConfirmationDialog(note: MyNote)
        fun enableRedoButton(enable: Boolean)
        fun enableUndoButton(enable: Boolean)
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onTagsFieldClick()
        abstract fun onLocationReceived(result: LocationResult)
        abstract fun onLocationPermissionsGranted()
        abstract fun onAddressFound(addresses: List<Address>, latitude: Double, longitude: Double)
        abstract fun onNoteTagsChanged(tags: List<MyTag>)
        abstract fun onMapReady()
        abstract fun onButtonAddImageClick()
        abstract fun onStoragePermissionsGranted()
        abstract fun onNoteImagesPicked(imageUrls: List<String>)
        abstract fun onButtonDeleteClick()
        abstract fun onToolbarImageClick()
        abstract fun onMoodFieldClick()
        abstract fun onMoodPicked(mood: MyMood)
        abstract fun onNoMoodPicked()
        abstract fun onCategoryFieldClick()
        abstract fun onNoCategoryPicked()
        abstract fun onCategoryPicked(category: MyCategory)
        abstract fun onButtonAppearanceClick()
        abstract fun updateNoteText(noteTitle: String, noteContent: String)
        abstract fun onDateFieldClick()
        abstract fun onTimeFieldClick()
        abstract fun onDateSelected(year: Int, monthOfYear: Int, dayOfMonth: Int)
        abstract fun onTimeSelected(hourOfDay: Int, minute: Int)
        abstract fun onButtonEditClick()
        abstract fun onViewStart(locationEnabled: Boolean, networkAvailable: Boolean)
        abstract fun init(note: MyNote, mode: NoteActivity.Companion.Mode)
        abstract fun onButtonDeleteConfirmClick(note: MyNote)
        abstract fun getNoteTextBuffer(): ArrayList<NoteCacheEntry>
        abstract fun setNoteTextBuffer(buffer: ArrayList<NoteCacheEntry>)
        abstract fun onNoteTextChange(title: String, content: String)
        abstract fun onButtonUndoClick()
        abstract fun onButtonRedoClick()
        abstract fun onEditModeEnabled()
    }
}