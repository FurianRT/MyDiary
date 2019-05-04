package com.furianrt.mydiary.screens.note.fragments.notefragment

import android.location.Address
import android.util.Log
import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.api.forecast.Forecast
import com.furianrt.mydiary.data.model.*
import com.furianrt.mydiary.data.model.pojo.TagsAndAppearance
import com.furianrt.mydiary.screens.note.NoteActivity
import com.furianrt.mydiary.utils.generateUniqueId
import com.google.android.gms.location.LocationResult
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import org.joda.time.DateTime
import java.util.*
import kotlin.collections.ArrayList

class NoteFragmentPresenter(private val dataManager: DataManager) : NoteFragmentContract.Presenter() {

    companion object {
        private const val TAG = "NoteFragmentPresenter"
        private const val UNDO_REDO_BUFFER_SIZE = 10
    }

    private lateinit var mMode: NoteActivity.Companion.Mode
    private lateinit var mNoteId: String

    private var mNoteTextBuffer = ArrayList<UndoRedoEntry>(UNDO_REDO_BUFFER_SIZE)

    override fun init(note: MyNote, mode: NoteActivity.Companion.Mode) {
        mMode = mode
        mNoteId = note.id
        if (mNoteTextBuffer.isEmpty()) {
            mNoteTextBuffer.add(UndoRedoEntry(note.title, note.content, true))
        }
    }

    override fun onViewStart(locationEnabled: Boolean, networkAvailable: Boolean) {
        loadNote(mMode, locationEnabled, networkAvailable)
        loadNoteAppearance()
        loadTags()
        loadImages()
        loadNoteCategory()
    }

    override fun onMoodFieldClick() {
        view?.showMoodsDialog(mNoteId)
    }

    override fun onTagsFieldClick() {
        view?.showTagsDialog(mNoteId)
    }

    override fun onCategoryFieldClick() {
        view?.showCategoriesDialog(mNoteId)
    }

    override fun onMapReady() {
        //mNote.location?.let { view?.zoomMap(it.lat, it.lon) }
    }

    private fun loadImages() {
        addDisposable(dataManager.getImagesForNote(mNoteId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { images ->
                    if (images.isEmpty()) {
                        view?.showNoImages()
                    } else {
                        view?.showImages(images.sortedWith(compareBy(MyImage::order, MyImage::addedTime)))
                    }
                })
    }

    private fun loadTags() {
        val tagsFlowable = dataManager.getTagsForNote(mNoteId).defaultIfEmpty(emptyList())
        val appearanceFlowable = dataManager.getNoteAppearance(mNoteId)

        addDisposable(Flowable.combineLatest(tagsFlowable, appearanceFlowable,
                BiFunction<List<MyTag>, MyNoteAppearance, TagsAndAppearance> { tags, appearance ->
                    return@BiFunction TagsAndAppearance(tags, appearance)
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it.tags.isEmpty()) {
                        view?.showNoTagsMessage(it.appearance)
                    } else {
                        view?.showTags(it)
                    }
                })
    }

    private fun loadNote(mode: NoteActivity.Companion.Mode, locationEnabled: Boolean,
                         networkAvailable: Boolean) {
        addDisposable(dataManager.getNote(mNoteId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { note ->
                    view?.showNoteText(note.title, note.content)
                    view?.showDateAndTime(note.time, dataManager.is24TimeFormat())
                    showNoteMood(note.moodId)
                    showNoteCategory(note.categoryId)
                    //showNoteLocation(note, mode, locationEnabled, networkAvailable)
                })
    }

    private fun showNoteCategory(categoryId: String) {
        addDisposable(dataManager.getCategory(categoryId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ category ->
                    view?.showCategory(category)
                }, {
                    view?.showNoCategoryMessage()
                }))
    }

    private fun loadNoteAppearance() {
        addDisposable(dataManager.getNoteAppearance(mNoteId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { appearance ->
                    appearance.textSize = appearance.textSize ?: dataManager.getTextSize()
                    appearance.textColor = appearance.textColor ?: dataManager.getTextColor()
                    appearance.background =
                            appearance.background ?: dataManager.getNoteBackgroundColor()
                    appearance.textBackground =
                            appearance.textBackground ?: dataManager.getNoteTextBackgroundColor()
                    view?.updateNoteAppearance(appearance)
                })
    }

    private fun showForecast(forecast: Forecast?, location: MyLocation, mode: NoteActivity.Companion.Mode) {
        if (dataManager.isWeatherEnabled()) {
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
            if (dataManager.isLocationEnabled()) {
                view?.showLocation(location)
            }
            showForecast(note.note.forecast, location, mode)
        } else if (mode == NoteActivity.Companion.Mode.ADD) {
            findLocation(locationEnabled, networkAvailable)
        }
    }

    private fun showNoteMood(moodId: Int) {
        if (moodId == 0 || !dataManager.isMoodEnabled()) {
            view?.showNoMoodMessage()
            return
        }
        addDisposable(dataManager.getMood(moodId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { mood -> view?.showMood(mood) })
    }

    private fun loadNoteCategory() {
        addDisposable(Flowable.combineLatest(dataManager.getNote(mNoteId),
                dataManager.getAllCategories(),
                BiFunction<MyNote, List<MyCategory>, MyCategory> { note, categories ->
                    if (note.categoryId.isBlank()) {
                        MyCategory()
                    } else {
                        categories.find { it.id == note.categoryId } ?: MyCategory()
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { category ->
                    if (category.id.isBlank()) {
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
        /* addDisposable(dataManager.getForecast(location.lat, location.lon)
                 .onErrorReturn { null }
                 .flatMapCompletable { forecast ->
                     Log.e(TAG, "addForecast")
                     mNote.forecast = forecast
                     return@flatMapCompletable dataManager.updateNote(mNote)
                 }
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribe({
                     val forecast = mNote.forecast
                     if (forecast != null && dataManager.isWeatherEnabled()) {
                         view?.showForecast(forecast)
                     }
                 }, { error ->
                     Log.i(TAG, "Could't load weather: ${error.stackTrace}")
                 }))*/
    }

    private fun addLocation(location: MyLocation) {
        /* Log.e(TAG, "addLocation")
         mNote.locationName = location.name
         addDisposable(dataManager.addLocation(location)
                 .andThen(dataManager.updateNote(mNote))
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribe {
                     if (dataManager.isLocationEnabled()) {
                         view?.showLocation(location)
                     }
                 })*/
    }

    override fun onButtonAddImageClick() {
        view?.requestStoragePermissions()
    }

    override fun onStoragePermissionsGranted() {
        view?.showImageExplorer()
    }

    override fun onNoteImagesPicked(imageUrls: List<String>) {
        addDisposable(Flowable.fromIterable(imageUrls)
                .map { url ->
                    val name = mNoteId + "_" + generateUniqueId()
                    return@map MyImage(name, url, mNoteId, DateTime.now().millis)
                }
                .flatMapSingle { image -> dataManager.saveImageToStorage(image) }
                .flatMapCompletable { savedImage -> dataManager.insertImage(savedImage) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.hideLoading() })
    }

    override fun onButtonDeleteClick() {
        view?.showDeleteConfirmationDialog(mNoteId)
    }

    override fun onToolbarImageClick() {
        view?.showGalleryView(mNoteId)
    }

    override fun onButtonAppearanceClick() {
        view?.showNoteSettingsView(mNoteId)
    }

    override fun updateNoteText(noteTitle: String, noteContent: String) {
        addDisposable(dataManager.updateNoteText(mNoteId, noteTitle, noteContent)
                .subscribe())
    }

    override fun onDateFieldClick() {
        addDisposable(dataManager.getNote(mNoteId)
                .firstOrError()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { note ->
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = note.time
                    view?.showDatePicker(calendar)
                })
    }

    override fun onDateSelected(year: Int, monthOfYear: Int, dayOfMonth: Int) {
        addDisposable(dataManager.getNote(mNoteId)
                .firstOrError()
                .flatMapCompletable {
                    val date = Calendar.getInstance().apply {
                        timeInMillis = it.time
                        set(Calendar.YEAR, year)
                        set(Calendar.MONTH, monthOfYear)
                        set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    }
                    dataManager.updateNote(it.apply { time = date.timeInMillis })
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe())
    }

    override fun onTimeFieldClick() {
        addDisposable(dataManager.getNote(mNoteId)
                .firstOrError()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { note ->
                    val date = Calendar.getInstance().apply { timeInMillis = note.time }
                    view?.showTimePicker(
                            date.get(Calendar.HOUR_OF_DAY),
                            date.get(Calendar.MINUTE),
                            dataManager.is24TimeFormat()
                    )
                })
    }

    override fun onTimeSelected(hourOfDay: Int, minute: Int) {
        addDisposable(dataManager.getNote(mNoteId)
                .firstOrError()
                .flatMapCompletable {
                    val date = Calendar.getInstance().apply {
                        timeInMillis = it.time
                        set(Calendar.HOUR_OF_DAY, hourOfDay)
                        set(Calendar.MINUTE, minute)
                    }
                    dataManager.updateNote(it.apply { time = date.timeInMillis })
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe())
    }

    override fun onButtonEditClick() {
        view?.shoNoteEditView()
    }

    override fun getNoteTextBuffer(): ArrayList<UndoRedoEntry> = mNoteTextBuffer

    override fun setNoteTextBuffer(buffer: ArrayList<UndoRedoEntry>) {
        mNoteTextBuffer = buffer
    }

    override fun onNoteTextChange(title: String, content: String) {
        val selectedIndex = mNoteTextBuffer.indexOfFirst { it.current }
        val selectedEntry = mNoteTextBuffer[selectedIndex]

        if (selectedEntry.title == title && selectedEntry.content == content) {
            return
        }

        mNoteTextBuffer = ArrayList(mNoteTextBuffer.subList(0, selectedIndex + 1))

        mNoteTextBuffer[selectedIndex].current = false
        if (mNoteTextBuffer.size == UNDO_REDO_BUFFER_SIZE) {
            mNoteTextBuffer.removeAt(0)
        }
        mNoteTextBuffer.add(UndoRedoEntry(title, content, true))

        view?.enableRedoButton(false)
        view?.enableUndoButton(true)
    }

    override fun onButtonUndoClick() {
        val selectedIndex = mNoteTextBuffer.indexOfFirst { it.current }
        if (selectedIndex > 0) {
            mNoteTextBuffer[selectedIndex].current = false
            val nextSelectedEntry = mNoteTextBuffer[selectedIndex - 1]
            nextSelectedEntry.current = true
            view?.enableUndoButton(selectedIndex - 1 != 0)
            view?.enableRedoButton(true)
            view?.showNoteText(nextSelectedEntry.title, nextSelectedEntry.content)
        } else {
            //todo отправить эвент
            Log.e(TAG, "Undo button should be disabled")
        }
    }

    override fun onButtonRedoClick() {
        val selectedIndex = mNoteTextBuffer.indexOfFirst { it.current }
        if (selectedIndex < mNoteTextBuffer.size - 1) {
            mNoteTextBuffer[selectedIndex].current = false
            val nextSelectedEntry = mNoteTextBuffer[selectedIndex + 1]
            nextSelectedEntry.current = true
            view?.enableRedoButton(selectedIndex + 1 != mNoteTextBuffer.size - 1)
            view?.enableUndoButton(true)
            view?.showNoteText(nextSelectedEntry.title, nextSelectedEntry.content)
        } else {
            //todo отправить эвент
            Log.e(TAG, "Redo button should be disabled")
        }
    }

    override fun onEditModeEnabled() {
        val selectedIndex = mNoteTextBuffer.indexOfFirst { it.current }
        view?.enableUndoButton(selectedIndex > 0)
        view?.enableRedoButton(selectedIndex < mNoteTextBuffer.size - 1)
    }
}
