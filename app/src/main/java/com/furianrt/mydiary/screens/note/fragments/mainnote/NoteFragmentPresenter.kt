package com.furianrt.mydiary.screens.note.fragments.mainnote

import android.location.Address
import android.util.Log
import com.furianrt.mydiary.data.model.*
import com.furianrt.mydiary.domain.check.IsForecastEnabledUseCase
import com.furianrt.mydiary.domain.check.IsLocationEnabledUseCase
import com.furianrt.mydiary.domain.check.IsMoodEnabledUseCase
import com.furianrt.mydiary.domain.get.*
import com.furianrt.mydiary.domain.save.AddForecastUseCase
import com.furianrt.mydiary.domain.save.SaveImagesUseCase
import com.furianrt.mydiary.domain.save.SaveLocationUseCase
import com.furianrt.mydiary.domain.update.UpdateNoteUseCase
import com.furianrt.mydiary.utils.generateUniqueId
import com.google.android.gms.location.LocationResult
import io.reactivex.android.schedulers.AndroidSchedulers
import org.joda.time.DateTime
import javax.inject.Inject
import kotlin.collections.ArrayList

class NoteFragmentPresenter @Inject constructor(
        private val getImages: GetImagesUseCase,
        private val getTagsWithAppearance: GetTagsWithAppearanceUseCase,
        private val getNotes: GetNotesUseCase,
        private val getTimeFormat: GetTimeFormatUseCase,
        private val getAppearance: GetAppearanceUseCase,
        private val getWeatherUnits: GetWeatherUnitsUseCase,
        private val saveImages: SaveImagesUseCase,
        private val updateNote: UpdateNoteUseCase,
        private val getCategories: GetCategoriesUseCase,
        private val saveLocation: SaveLocationUseCase,
        private val isMoodEnabled: IsMoodEnabledUseCase,
        private val getMoods: GetMoodsUseCase,
        private val getFullNotes: GetFullNotesUseCase,
        private val getLocations: GetLocationsUseCase,
        private val getForecasts: GetForecastsUseCase,
        private val addForecast: AddForecastUseCase,
        private val isLocationEnabled: IsLocationEnabledUseCase,
        private val isForecastEnabled: IsForecastEnabledUseCase
) : NoteFragmentContract.Presenter() {

    companion object {
        private const val TAG = "NoteFragmentPresenter"
        private const val UNDO_REDO_BUFFER_SIZE = 20
    }

    private var mIsNewNote = true
    private lateinit var mNoteId: String

    private var mNoteTextBuffer = ArrayList<UndoRedoEntry>(UNDO_REDO_BUFFER_SIZE)

    override fun init(noteId: String, newNote: Boolean) {
        mIsNewNote = newNote
        mNoteId = noteId
    }

    override fun attachView(view: NoteFragmentContract.MvpView) {
        super.attachView(view)
        if (!::mNoteId.isInitialized) {
            throw IllegalStateException("Need to call init before attaching view")
        }
        loadNote()
        loadNoteAppearance()
        loadTags()
        loadImages()
        loadNoteCategory()
        loadLocation()
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

    private fun loadImages() {
        addDisposable(getImages.invoke(mNoteId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { images ->
                    if (images.isEmpty()) {
                        view?.showNoImages()
                    } else {
                        view?.showImages(images)
                    }
                })
    }

    private fun loadTags() {
        addDisposable(getTagsWithAppearance.invoke(mNoteId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { tagsAndAppearance ->
                    if (tagsAndAppearance.tags.isEmpty()) {
                        view?.showNoTagsMessage(tagsAndAppearance)
                    } else {
                        view?.showTags(tagsAndAppearance)
                    }
                })
    }

    private fun loadNote() {
        addDisposable(getNotes.invoke(mNoteId)
                .map { note ->
                    if (note.isPresent) {
                        note.get()
                    } else {
                        throw IllegalStateException()
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { note ->
                    if (mNoteTextBuffer.isEmpty()) {
                        mNoteTextBuffer.add(UndoRedoEntry(note.title, note.content, true))
                    }
                    view?.showNoteText(note.title, note.content)
                    view?.showDateAndTime(
                            note.time,
                            getTimeFormat.invoke() == GetTimeFormatUseCase.TIME_FORMAT_24
                    )
                    showNoteMood(note.moodId)
                })
    }

    private fun loadNoteAppearance() {
        addDisposable(getAppearance.invoke(mNoteId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.updateNoteAppearance(it) })
    }

    private fun showForecast() {
        addDisposable(getForecasts.invoke(mNoteId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.showForecast(getWeatherTemp(it), it.icon) })
    }

    private fun showLocation(location: MyLocation) {
        if (isLocationEnabled.invoke()) {
            view?.showLocation(location)
        }
    }

    private fun loadLocation() {
        addDisposable(getLocations.invoke(mNoteId)
                .first(emptyList())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { locations ->
                    if (locations.isNotEmpty()) {
                        showLocation(locations.first())
                        showForecast()
                    } else if (mIsNewNote && view?.isLocationAvailable() == true) {
                        findLocation()
                    }
                })
    }

    private fun showNoteMood(moodId: Int) {
        if (isMoodEnabled.invoke()) {
            if (moodId == 0) {
                view?.showNoMoodMessage()
            } else {
                addDisposable(getMoods.invoke(moodId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { mood -> view?.showMood(mood) })
            }
        }
    }

    private fun loadNoteCategory() {
        addDisposable(getCategories.invoke(mNoteId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { category ->
                    if (category.isPresent) {
                        view?.showCategory(category.get())
                    } else {
                        view?.showNoCategoryMessage()
                    }
                })
    }

    private fun findLocation() {
        if (isLocationEnabled.invoke()) {
            view?.requestLocationPermissions()
        }
    }

    override fun onLocationPermissionsGranted() {
        view?.requestLocation()
    }

    override fun onLocationReceived(result: LocationResult) {
        if (isForecastEnabled.invoke()) {
            addForecastToNote(result.lastLocation.latitude, result.lastLocation.longitude)
        }
        if (isLocationEnabled.invoke()) {
            view?.findAddress(result.lastLocation.latitude, result.lastLocation.longitude)
        }
    }

    override fun onAddressFound(addresses: List<Address>, latitude: Double, longitude: Double) {
        if (addresses.isNotEmpty()) {
            val address = addresses[0].getAddressLine(0)
            if (address != null) {
                addLocation(MyLocation(generateUniqueId(), address, latitude, longitude))
            }
        }
    }

    private fun addForecastToNote(latitude: Double, longitude: Double) {
        addDisposable(addForecast.invoke(mNoteId, latitude, longitude)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ forecast ->
                    view?.showForecast(getWeatherTemp(forecast), forecast.icon)
                }, { error ->
                    error.printStackTrace()
                }))
    }

    private fun getWeatherTemp(forecast: MyForecast): String =
            when (getWeatherUnits.invoke()) {
                GetWeatherUnitsUseCase.UNITS_CELSIUS -> "${forecast.temp.toInt()} °C"
                GetWeatherUnitsUseCase.UNITS_FAHRENHEIT -> "${(forecast.temp * 1.8 + 32).toInt()} °F"
                else -> throw IllegalStateException()
            }

    private fun addLocation(location: MyLocation) {
        Log.e(TAG, "insertLocation")
        addDisposable(saveLocation.invoke(mNoteId, location)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { showLocation(location) })
    }

    override fun onButtonAddImageClick() {
        view?.requestStoragePermissions()
    }

    override fun onStoragePermissionsGranted() {
        view?.showImageExplorer()
    }

    override fun onNoteImagesPicked(imageUrls: List<String>) {
        addDisposable(saveImages.invoke(mNoteId, imageUrls)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.hideLoading() })
    }

    override fun onButtonDeleteClick() {
        view?.showDeleteConfirmationDialog(mNoteId)
    }

    override fun onToolbarImageClick(image: MyImage) {
        view?.showGalleryView(mNoteId, image)
    }

    override fun onButtonAppearanceClick() {
        view?.showNoteSettingsView(mNoteId)
    }

    override fun updateNoteText(noteTitle: String, noteContent: String) {
        addDisposable(updateNote.invoke(mNoteId, noteTitle, noteContent)
                .subscribe())
    }

    override fun onDateFieldClick() {
        addDisposable(getNotes.invoke(mNoteId)
                .map { it.get() }
                .firstOrError()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { note ->
                    val date = DateTime(note.time)
                    view?.showDatePicker(date.year, date.monthOfYear - 1, date.dayOfMonth)
                })
    }

    override fun onDateSelected(year: Int, monthOfYear: Int, dayOfMonth: Int) {
        addDisposable(getNotes.invoke(mNoteId)
                .map { it.get() }
                .firstOrError()
                .flatMapCompletable { note ->
                    val date = DateTime(note.time)
                            .withYear(year)
                            .withMonthOfYear(monthOfYear + 1)
                            .withDayOfMonth(dayOfMonth)
                    updateNote.invoke(note.apply { time = date.millis })
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe())
    }

    override fun onTimeFieldClick() {
        addDisposable(getNotes.invoke(mNoteId)
                .map { it.get() }
                .firstOrError()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { note ->
                    val date = DateTime(note.time)
                    view?.showTimePicker(
                            date.hourOfDay,
                            date.minuteOfHour,
                            getTimeFormat.invoke() == GetTimeFormatUseCase.TIME_FORMAT_24
                    )
                })
    }

    override fun onTimeSelected(hourOfDay: Int, minute: Int) {
        addDisposable(getNotes.invoke(mNoteId)
                .map { note ->
                    if (note.isPresent) {
                        note.get()
                    } else {
                        throw IllegalStateException()
                    }
                }
                .firstOrError()
                .flatMapCompletable { note ->
                    val date = DateTime(note.time)
                            .withHourOfDay(hourOfDay)
                            .withMinuteOfHour(minute)
                    updateNote.invoke(note.apply { time = date.millis })
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
            Log.e(TAG, "Undo button should be disabled")
            view?.sendUndoErrorEvent()
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
            view?.sendRedoErrorEvent()
        }
    }

    override fun onEditModeEnabled() {
        val selectedIndex = mNoteTextBuffer.indexOfFirst { it.current }
        view?.enableUndoButton(selectedIndex > 0)
        view?.enableRedoButton(selectedIndex < mNoteTextBuffer.size - 1)
    }

    override fun onButtonMicClick() {
        view?.recordSpeech()
    }

    override fun onSpeechRecorded(curTitle: String, curContent: String, recordedText: String) {
        val content = when {
            curContent.isBlank() ->
                recordedText.capitalize()
            curContent.replace(Regex("[ \n]"), "").last() == '.' ->
                "$curContent ${recordedText.capitalize()}"
            else ->
                "$curContent $recordedText"
        }
        addDisposable(updateNote.invoke(mNoteId, curTitle, content)
                .subscribe { onNoteTextChange(curTitle, content) })
    }

    override fun onButtonShareClick() {
        addDisposable(getFullNotes.invoke(mNoteId)
                .firstOrError()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { note -> view?.shareNote(note) })
    }
}
