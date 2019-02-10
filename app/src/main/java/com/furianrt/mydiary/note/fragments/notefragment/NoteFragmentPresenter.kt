package com.furianrt.mydiary.note.fragments.notefragment

import android.location.Address
import android.util.Log
import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.api.Forecast
import com.furianrt.mydiary.data.model.*
import com.furianrt.mydiary.note.NoteActivity
import com.furianrt.mydiary.utils.generateUniqueId
import com.google.android.gms.location.LocationResult
import io.reactivex.Flowable
import io.reactivex.Observable
import org.joda.time.DateTime
import java.util.*

class NoteFragmentPresenter(private val mDataManager: DataManager) : NoteFragmentContract.Presenter() {

    private lateinit var mNote: MyNote

    companion object {
        private const val TAG = "NoteFragmentPresenter"
    }

    override fun onMoodFieldClick() {
        addDisposable(mDataManager.getAllMoods()
                .subscribe { moods -> view?.showMoodsDialog(moods) })

    }

    override fun onTagsFieldClick() {
        val allTagsObservable = mDataManager.getAllTags()
                .flatMapObservable { tags -> Observable.fromIterable(tags) }

        addDisposable(mDataManager.getTagsForNote(mNote.id)
                .first(emptyList())
                .flatMapObservable { tags -> Observable.fromIterable(tags) }
                .map { tag -> tag.apply { isChecked = true } }
                .concatWith(allTagsObservable)
                .collectInto(ArrayList<MyTag>()) { list, tag ->
                    val foundedTag = list.find { it.id == tag.id }
                    if (foundedTag == null) list.add(tag)
                }
                .subscribe { tags -> view?.showTagsDialog(tags) })
    }

    override fun onCategoryFieldClick() {
        view?.showCategoriesDialog(mNote.id)
    }

    override fun onNoteTagsChanged(tags: List<MyTag>) {
        addDisposable(mDataManager.replaceNoteTags(mNote.id, tags)
                .subscribe())
    }

    override fun onMapReady() {
        //mNote.location?.let { view?.zoomMap(it.lat, it.lon) }
    }

    override fun loadImages(noteId: String) {
        addDisposable(mDataManager.getImagesForNote(noteId)
                .subscribe { images ->
                    if (images.isEmpty()) {
                        view?.showNoImages()
                    } else {
                        view?.showImages(images.sortedBy { it.order })
                    }
                })
    }

    override fun loadTags(noteId: String) {
        addDisposable(mDataManager.getTagsForNote(noteId)
                .defaultIfEmpty(emptyList())
                .subscribe { tags ->
                    if (tags.isEmpty()) {
                        view?.showNoTagsMessage()
                    } else {
                        view?.showTagNames(tags.map { it.name })
                    }
                })
    }

    override fun loadNote(noteId: String, mode: NoteActivity.Companion.Mode, locationEnabled: Boolean,
                          networkAvailable: Boolean) {
        addDisposable(mDataManager.getNote(noteId)
                .subscribe { note ->
                    mNote = note
                    view?.showNoteText(note.title, note.content)
                    val is24TimeFormat = mDataManager.getTimeFormat() == DataManager.TIME_FORMAT_24
                    view?.showDateAndTime(note.time, is24TimeFormat)
                    showNoteMood(note.moodId)
                    showNoteCategory(note.categoryId)
                    //showNoteLocation(note, mode, locationEnabled, networkAvailable)
                })
    }

    override fun loadNoteAppearance(noteId: String) {
        addDisposable(mDataManager.getNoteAppearance(noteId)
                .subscribe { appearance ->
                    appearance.textSize = appearance.textSize ?: mDataManager.getTextSize()
                    appearance.textColor = appearance.textColor ?: mDataManager.getTextColor()
                    appearance.background =
                            appearance.background ?: mDataManager.getNoteBackgroundColor()
                    appearance.textBackground =
                            appearance.textBackground ?: mDataManager.getNoteTextBackgroundColor()
                    view?.updateNoteAppearance(appearance)
                })
    }

    private fun showForecast(forecast: Forecast?, location: MyLocation, mode: NoteActivity.Companion.Mode) {
        if (mDataManager.isWeatherEnabled()) {
            if (forecast != null) {
                view?.showForecast(forecast)
            } else if (mode == NoteActivity.Companion.Mode.ADD) {
                addForecast(location)
            }
        }
    }

    private fun showNoteLocation(note: MyNoteWithProp, mode: NoteActivity.Companion.Mode, locationEnabled: Boolean,
                                 networkAvailable: Boolean) {
        val location = note.location
        if (location != null) {
            if (mDataManager.isLocationEnabled()) {
                view?.showLocation(location)
            }
            showForecast(note.note.forecast, location, mode)
        } else if (mode == NoteActivity.Companion.Mode.ADD) {
            findLocation(locationEnabled, networkAvailable)
        }
    }

    private fun showNoteMood(moodId: Int) {
        if (moodId == 0 || !mDataManager.isMoodEnabled()) {
            view?.showNoMoodMessage()
            return
        }

        addDisposable(mDataManager.getMood(moodId)
                .subscribe { mood -> view?.showMood(mood) })
    }

    private fun showNoteCategory(categoryId: Long) {
        addDisposable(mDataManager.getAllCategories()
                .subscribe { categories ->
                    val category = categories.find { it.id == categoryId }
                    if (category == null) {
                        view?.showNoCategoryMessage()
                    } else {
                        view?.showCategory(category)
                    }
                })
    }

    private fun findLocation(locationEnabled: Boolean, networkAvailable: Boolean) {
        if (locationEnabled && networkAvailable) {
            view?.requestLocationPermissions()
        }
    }

    override fun onLocationPermissionsGranted() {
        view?.requestLocation()
    }

    override fun onLocationReceived(result: LocationResult) {
        val lastLocation = result.lastLocation
        view?.findAddress(lastLocation.latitude, lastLocation.longitude)
    }

    override fun onAddressFound(addresses: List<Address>, latitude: Double, longitude: Double) {
        if (addresses.isNotEmpty()) {
            val address = addresses[0].getAddressLine(0)
            if (address != null) {
                val location = MyLocation(address, latitude, longitude)
                addLocation(location)
                addForecast(location)
            }
        }
    }

    private fun addForecast(location: MyLocation) {
        addDisposable(mDataManager.getForecast(location.lat, location.lon)
                .onErrorReturn { null }
                .flatMapCompletable { forecast ->
                    Log.e(TAG, "addForecast")
                    mNote.forecast = forecast
                    return@flatMapCompletable mDataManager.updateNote(mNote)
                }
                .subscribe({
                    val forecast = mNote.forecast
                    if (forecast != null && mDataManager.isWeatherEnabled()) {
                        view?.showForecast(forecast)
                    }
                }, { error ->
                    Log.i(TAG, "Could't load weather: ${error.stackTrace}")
                }))
    }

    private fun addLocation(location: MyLocation) {
        Log.e(TAG, "addLocation")
        mNote.locationName = location.name
        addDisposable(mDataManager.addLocation(location)
                .andThen(mDataManager.updateNote(mNote))
                .subscribe {
                    if (mDataManager.isLocationEnabled()) {
                        view?.showLocation(location)
                    }
                })
    }

    override fun onAddImageButtonClick() {
        view?.requestStoragePermissions()
    }

    override fun onStoragePermissionsGranted() {
        view?.showImageExplorer()
    }

    override fun onNoteImagesPicked(imageUrls: List<String>) {
        addDisposable(Flowable.fromIterable(imageUrls)
                .map { url ->
                    val name = mNote.id + "_" + generateUniqueId()
                    return@map MyImage(name, url, mNote.id, DateTime.now().millis)
                }
                .flatMapSingle { image -> mDataManager.saveImageToStorage(image) }
                .flatMapCompletable { savedImage -> mDataManager.insertImage(savedImage) }
                .subscribe())
    }

    override fun onDeleteButtonClick() {
        addDisposable(mDataManager.getImagesForNote(mNote.id)
                .first(emptyList())
                .flatMapObservable { Observable.fromIterable(it) }
                .flatMapSingle { image -> mDataManager.deleteImageFromStorage(image.name) }
                .collectInto(mutableListOf<Boolean>()) { l, i -> l.add(i) }
                .flatMapCompletable { mDataManager.deleteNote(mNote) }
                .subscribe { view?.closeView() })
    }

    override fun onToolbarImageClick() {
        view?.showGalleryView(mNote.id)
    }

    override fun onMoodPicked(mood: MyMood) {
        mNote.moodId = mood.id
        addDisposable(mDataManager.updateNote(mNote)
                .subscribe())
    }

    override fun onNoMoodPicked() {
        mNote.moodId = 0
        addDisposable(mDataManager.updateNote(mNote)
                .subscribe { view?.showNoMoodMessage() })
    }

    override fun onCategoryPicked(category: MyCategory) {
        mNote.categoryId = category.id
        addDisposable(mDataManager.updateNote(mNote)
                .subscribe { view?.showCategory(category) })
    }

    override fun onNoCategoryPicked() {
        mNote.categoryId = 0
        addDisposable(mDataManager.updateNote(mNote)
                .subscribe { view?.showNoCategoryMessage() })
    }

    override fun onAppearanceButtonClick() {
        view?.showNoteSettingsView(mNote.id)
    }

    override fun updateNoteText(noteId: String, noteTitle: String, noteContent: String) {
        //todo добавить условие
        addDisposable(mDataManager.updateNoteText(noteId, noteTitle, noteContent)
                .subscribe())
    }

    override fun onDateFieldClick() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = mNote.time
        view?.showDatePicker(calendar)
    }

    override fun onDateSelected(year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val date = Calendar.getInstance().apply {
            timeInMillis = mNote.time
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, monthOfYear)
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }
        mNote.time = date.timeInMillis
        addDisposable(mDataManager.updateNote(mNote)
                .subscribe())
    }

    override fun onTimeFieldClick() {
        val date = Calendar.getInstance()
                .apply { timeInMillis = mNote.time }
        val is24HourMode = mDataManager.getTimeFormat() == DataManager.TIME_FORMAT_24
        view?.showTimePicker(date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), is24HourMode)
    }

    override fun onTimeSelected(hourOfDay: Int, minute: Int) {
        val date = Calendar.getInstance().apply {
            timeInMillis = mNote.time
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
        }
        mNote.time = date.timeInMillis
        addDisposable(mDataManager.updateNote(mNote)
                .subscribe())
    }
}
