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
import io.reactivex.Observable
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

    private lateinit var mNote: MyNote
    private lateinit var mMode: NoteActivity.Companion.Mode

    private var mNoteTextBuffer = ArrayList<UndoRedoEntry>(UNDO_REDO_BUFFER_SIZE)

    override fun init(note: MyNote, mode: NoteActivity.Companion.Mode) {
        mNote = note
        mMode = mode
        if (mNoteTextBuffer.isEmpty()) {
            mNoteTextBuffer.add(UndoRedoEntry(mNote.title, mNote.content, true))
        }
    }

    override fun onViewStart(locationEnabled: Boolean, networkAvailable: Boolean) {
        loadNote(mNote.id, mMode, locationEnabled, networkAvailable)
        loadNoteAppearance(mNote.id)
        loadTags(mNote.id)
        loadImages(mNote.id)
        loadNoteCategory()
    }

    override fun onMoodFieldClick() {
        addDisposable(dataManager.getAllMoods()
                .subscribe { moods -> view?.showMoodsDialog(moods) })
    }

    override fun onTagsFieldClick() {
        val allTagsObservable = dataManager.getAllTags()
                .flatMapObservable { tags -> Observable.fromIterable(tags) }

        addDisposable(dataManager.getTagsForNote(mNote.id)
                .first(emptyList())
                .flatMapObservable { tags -> Observable.fromIterable(tags) }
                .map { tag -> tag.apply { isChecked = true } }
                .concatWith(allTagsObservable)
                .collectInto(ArrayList<MyTag>()) { list, tag ->
                    val foundedTag = list.find { it.id == tag.id }
                    if (foundedTag == null) list.add(tag)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { tags -> view?.showTagsDialog(tags) })
    }

    override fun onCategoryFieldClick() {
        view?.showCategoriesDialog(mNote.id)
    }

    override fun onNoteTagsChanged(tags: List<MyTag>) {
        addDisposable(dataManager.replaceNoteTags(mNote.id, tags)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe())
    }

    override fun onMapReady() {
        //mNote.location?.let { view?.zoomMap(it.lat, it.lon) }
    }

    private fun loadImages(noteId: String) {
        addDisposable(dataManager.getImagesForNote(noteId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { images ->
                    if (images.isEmpty()) {
                        view?.showNoImages()
                    } else {
                        view?.showImages(images.sortedWith(compareBy(MyImage::order, MyImage::addedTime)))
                    }
                })
    }

    private fun loadTags(noteId: String) {
        val tagsFlowable = dataManager.getTagsForNote(noteId).defaultIfEmpty(emptyList())
        val appearanceFlowable = dataManager.getNoteAppearance(noteId)

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

    private fun loadNote(noteId: String, mode: NoteActivity.Companion.Mode, locationEnabled: Boolean,
                         networkAvailable: Boolean) {
        addDisposable(dataManager.getNote(noteId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { note ->
                    mNote = note
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

    private fun loadNoteAppearance(noteId: String) {
        addDisposable(dataManager.getNoteAppearance(noteId)
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
        addDisposable(dataManager.getAllCategories()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ categories ->
                    val category = categories.find { it.id == mNote.categoryId }
                    if (category == null) {
                        view?.showNoCategoryMessage()
                    } else {
                        view?.showCategory(category)
                    }
                }, {
                    view?.showNoCategoryMessage()
                }))
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
        addDisposable(dataManager.getForecast(location.lat, location.lon)
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
                }))
    }

    private fun addLocation(location: MyLocation) {
        Log.e(TAG, "addLocation")
        mNote.locationName = location.name
        addDisposable(dataManager.addLocation(location)
                .andThen(dataManager.updateNote(mNote))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (dataManager.isLocationEnabled()) {
                        view?.showLocation(location)
                    }
                })
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
                    val name = mNote.id + "_" + generateUniqueId()
                    return@map MyImage(name, url, mNote.id, DateTime.now().millis)
                }
                .flatMapSingle { image -> dataManager.saveImageToStorage(image) }
                .flatMapCompletable { savedImage -> dataManager.insertImage(savedImage) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.hideLoading() })
    }

    override fun onButtonDeleteClick() {
        view?.showDeleteConfirmationDialog(mNote)
    }

    override fun onButtonDeleteConfirmClick(note: MyNote) {
        addDisposable(dataManager.getImagesForNote(note.id)
                .first(emptyList())
                .flatMapObservable { Observable.fromIterable(it) }
                .flatMapSingle { image -> dataManager.deleteImageFromStorage(image.name) }
                .collectInto(mutableListOf<Boolean>()) { l, i -> l.add(i) }
                .flatMapCompletable { dataManager.deleteNote(note) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.closeView() })
    }

    override fun onToolbarImageClick() {
        view?.showGalleryView(mNote.id)
    }

    override fun onMoodPicked(mood: MyMood) {
        mNote.moodId = mood.id
        addDisposable(dataManager.updateNote(mNote)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe())
    }

    override fun onNoMoodPicked() {
        mNote.moodId = 0
        addDisposable(dataManager.updateNote(mNote)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.showNoMoodMessage() })
    }

    override fun onCategoryPicked(category: MyCategory) {
        mNote.categoryId = category.id
        addDisposable(dataManager.updateNote(mNote)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.showCategory(category) })
    }

    override fun onNoCategoryPicked() {
        mNote.categoryId = ""
        addDisposable(dataManager.updateNote(mNote)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.showNoCategoryMessage() })
    }

    override fun onButtonAppearanceClick() {
        view?.showNoteSettingsView(mNote.id)
    }

    override fun updateNoteText(noteTitle: String, noteContent: String) {
        //todo добавить условие
        addDisposable(dataManager.updateNoteText(mNote.id, noteTitle, noteContent)
                .observeOn(AndroidSchedulers.mainThread())
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
        addDisposable(dataManager.updateNote(mNote)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe())
    }

    override fun onTimeFieldClick() {
        val date = Calendar.getInstance()
                .apply { timeInMillis = mNote.time }
        view?.showTimePicker(
                date.get(Calendar.HOUR_OF_DAY),
                date.get(Calendar.MINUTE),
                dataManager.is24TimeFormat()
        )
    }

    override fun onTimeSelected(hourOfDay: Int, minute: Int) {
        val date = Calendar.getInstance().apply {
            timeInMillis = mNote.time
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
        }
        mNote.time = date.timeInMillis
        addDisposable(dataManager.updateNote(mNote)
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
            Log.e(TAG, "Redo button should be disabled")
        }
    }

    override fun onEditModeEnabled() {
        val selectedIndex = mNoteTextBuffer.indexOfFirst { it.current }
        view?.enableUndoButton(selectedIndex > 0)
        view?.enableRedoButton(selectedIndex < mNoteTextBuffer.size - 1)
    }
}
