/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.note.fragments.mainnote

import android.util.Log
import com.furianrt.mydiary.domain.DisableLocationUseCase
import com.furianrt.mydiary.model.entity.*
import com.furianrt.mydiary.domain.FindLocationUseCase
import com.furianrt.mydiary.domain.check.IsForecastEnabledUseCase
import com.furianrt.mydiary.domain.check.IsLocationEnabledUseCase
import com.furianrt.mydiary.domain.check.IsMoodEnabledUseCase
import com.furianrt.mydiary.domain.check.IsPanoramaEnabledUseCase
import com.furianrt.mydiary.domain.get.*
import com.furianrt.mydiary.domain.save.AddForecastUseCase
import com.furianrt.mydiary.domain.save.SaveImagesUseCase
import com.furianrt.mydiary.domain.save.SaveLocationUseCase
import com.furianrt.mydiary.domain.update.UpdateNoteUseCase
import com.furianrt.mydiary.utils.MyRxUtils
import org.joda.time.DateTime
import javax.inject.Inject
import kotlin.collections.ArrayList

class NoteFragmentPresenter @Inject constructor(
        private val getImagesUseCase: GetImagesUseCase,
        private val getTagsWithAppearanceUseCase: GetTagsWithAppearanceUseCase,
        private val getNotesUseCase: GetNotesUseCase,
        private val getNotesWithSpansUseCase: GetNotesWithSpansUseCase,
        private val getTimeFormatUseCase: GetTimeFormatUseCase,
        private val getAppearanceUseCase: GetAppearanceUseCase,
        private val getWeatherUnitsUseCase: GetWeatherUnitsUseCase,
        private val saveImagesUseCase: SaveImagesUseCase,
        private val updateNoteUseCase: UpdateNoteUseCase,
        private val getCategoriesUseCase: GetCategoriesUseCase,
        private val saveLocationUseCase: SaveLocationUseCase,
        private val isMoodEnabledUseCase: IsMoodEnabledUseCase,
        private val getMoodsUseCase: GetMoodsUseCase,
        private val getFullNotesUseCase: GetFullNotesUseCase,
        private val getLocationsUseCase: GetLocationsUseCase,
        private val getForecastsUseCase: GetForecastsUseCase,
        private val addForecastUseCase: AddForecastUseCase,
        private val isLocationEnabledUseCase: IsLocationEnabledUseCase,
        private val isForecastEnabledUseCase: IsForecastEnabledUseCase,
        private val findLocationUseCase: FindLocationUseCase,
        private val isPanoramaEnabledUseCase: IsPanoramaEnabledUseCase,
        private val disableLocationUseCase: DisableLocationUseCase,
        private val scheduler: MyRxUtils.BaseSchedulerProvider
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

    override fun attachView(view: NoteFragmentContract.View) {
        super.attachView(view)
        check(::mNoteId.isInitialized) { "Need to call init before attaching view" }
        loadNote()
        loadNoteAppearance()
        loadTags()
        loadImages()
        loadNoteCategory()
        loadLocation()
    }

    override fun onMoodFieldClick() {
        view?.showMoodsView(mNoteId)
    }

    override fun onTagsFieldClick() {
        view?.showTagsView(mNoteId)
    }

    override fun onCategoryFieldClick() {
        view?.showCategoriesView(mNoteId)
    }

    override fun onReminderFieldClick() {
        view?.showReminderView(mNoteId)
    }

    private fun loadImages() {
        addDisposable(getImagesUseCase(mNoteId)
                .map { Pair(it, isPanoramaEnabledUseCase()) }
                .observeOn(scheduler.ui())
                .subscribe { imagesAndPanorama ->
                    if (imagesAndPanorama.first.isEmpty()) {
                        view?.showNoImages()
                    } else {
                        view?.showImages(imagesAndPanorama.first, imagesAndPanorama.second)
                    }
                })
    }

    private fun loadTags() {
        addDisposable(getTagsWithAppearanceUseCase(mNoteId)
                .observeOn(scheduler.ui())
                .subscribe { tagsAndAppearance ->
                    if (tagsAndAppearance.tags.isEmpty()) {
                        view?.showNoTagsMessage(tagsAndAppearance)
                    } else {
                        view?.showTags(tagsAndAppearance)
                    }
                })
    }

    private fun loadNote() {
        addDisposable(getNotesWithSpansUseCase(mNoteId)
                .observeOn(scheduler.ui())
                .subscribe { note ->
                    if (note.isPresent) {
                        if (mNoteTextBuffer.isEmpty()) {
                            mNoteTextBuffer.add(UndoRedoEntry(note.get().note.title, note.get().note.content, note.get().textSpans, true))
                        }
                        view?.showNoteText(note.get().note.title, note.get().note.content, note.get().textSpans)
                        view?.showDateAndTime(
                                note.get().note.time,
                                getTimeFormatUseCase() == GetTimeFormatUseCase.TIME_FORMAT_24
                        )
                        showNoteMood(note.get().note.moodId)
                    }
                })
    }

    private fun loadNoteAppearance() {
        addDisposable(getAppearanceUseCase(mNoteId)
                .observeOn(scheduler.ui())
                .subscribe({ appearance ->
                    view?.updateNoteAppearance(appearance)
                }, { error ->
                    error.printStackTrace()
                }))
    }

    private fun showForecast() {
        addDisposable(getForecastsUseCase(mNoteId)
                .observeOn(scheduler.ui())
                .subscribe { view?.showForecast(getWeatherTemp(it), it.icon) })
    }

    private fun showLocation(location: MyLocation) {
        if (isLocationEnabledUseCase()) {
            view?.showLocation(location)
        }
    }

    private fun loadLocation() {
        addDisposable(getLocationsUseCase(mNoteId)
                .first(emptyList())
                .observeOn(scheduler.ui())
                .subscribe { locations ->
                    if (locations.isNotEmpty()) {
                        showLocation(locations.first())
                        showForecast()
                    } else if (mIsNewNote && isLocationEnabledUseCase()) {
                        view?.requestLocationPermissions()
                    }
                })
    }

    override fun onLocationPermissionsGranted() {
        addDisposable(findLocationUseCase()
                .observeOn(scheduler.ui())
                .subscribe({ location ->
                    addLocation(location)
                    if (isForecastEnabledUseCase()) {
                        addForecastToNote(location.lat, location.lon)
                    }
                }, { error ->
                    error.printStackTrace()
                }))
    }

    override fun onLocationPermissionDenied() {
        disableLocationUseCase()
    }

    private fun showNoteMood(moodId: Int) {
        if (isMoodEnabledUseCase()) {
            if (moodId == 0) {
                view?.showNoMoodMessage()
            } else {
                addDisposable(getMoodsUseCase(moodId)
                        .firstOrError()
                        .observeOn(scheduler.ui())
                        .subscribe { mood -> view?.showMood(mood) })
            }
        }
    }

    private fun loadNoteCategory() {
        addDisposable(getCategoriesUseCase(mNoteId)
                .observeOn(scheduler.ui())
                .subscribe { category ->
                    if (category.isPresent) {
                        view?.showCategory(category.get())
                    } else {
                        view?.showNoCategoryMessage()
                    }
                })
    }

    private fun addForecastToNote(latitude: Double, longitude: Double) {
        addDisposable(addForecastUseCase(mNoteId, latitude, longitude)
                .observeOn(scheduler.ui())
                .subscribe({ forecast ->
                    view?.showForecast(getWeatherTemp(forecast), forecast.icon)
                }, { error ->
                    error.printStackTrace()
                    view?.showErrorForecast()
                }))
    }

    private fun getWeatherTemp(forecast: MyForecast): String =
            when (getWeatherUnitsUseCase()) {
                GetWeatherUnitsUseCase.UNITS_CELSIUS -> "${forecast.temp.toInt()} °C"
                GetWeatherUnitsUseCase.UNITS_FAHRENHEIT -> "${(forecast.temp * 1.8 + 32).toInt()} °F"
                else -> throw IllegalStateException()
            }

    private fun addLocation(location: MyLocation) {
        Log.e(TAG, "insertLocation")
        addDisposable(saveLocationUseCase(mNoteId, location)
                .observeOn(scheduler.ui())
                .subscribe { showLocation(location) })
    }

    override fun onButtonSelectPhotoClick() {
        view?.requestStoragePermissions()
    }

    override fun onButtonTakePhotoClick() {
        view?.requestCameraPermissions()
    }

    override fun onStoragePermissionsGranted() {
        view?.showImageExplorer()
    }

    override fun onCameraPermissionsGranted() {
        view?.showCamera()
    }

    override fun onNoteImagesPicked(imageUrls: List<String>) {
        addDisposable(saveImagesUseCase(mNoteId, imageUrls)
                .observeOn(scheduler.ui())
                .subscribe({
                    view?.hideLoading()
                }, { error ->
                    error.printStackTrace()
                    view?.showErrorSaveImage()
                    view?.hideLoading()
                }))
    }

    override fun onNewPhotoTaken(photoPath: String?) {
        if (photoPath == null) {
            view?.showErrorSaveImage()
            view?.hideLoading()
            return
        }
        addDisposable(saveImagesUseCase(mNoteId, photoPath)
                .observeOn(scheduler.ui())
                .subscribe({
                    view?.hideLoading()
                }, { error ->
                    error.printStackTrace()
                    view?.showErrorSaveImage()
                    view?.hideLoading()
                }))
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

    override fun onDateFieldClick() {
        addDisposable(getNotesUseCase(mNoteId)
                .map { it.get() }
                .firstOrError()
                .observeOn(scheduler.ui())
                .subscribe { note ->
                    val date = DateTime(note.time)
                    view?.showDatePicker(date.year, date.monthOfYear - 1, date.dayOfMonth)
                })
    }

    override fun onDateSelected(year: Int, monthOfYear: Int, dayOfMonth: Int) {
        addDisposable(getNotesUseCase(mNoteId)
                .map { it.get() }
                .firstOrError()
                .flatMapCompletable { note ->
                    val date = DateTime(note.time)
                            .withYear(year)
                            .withMonthOfYear(monthOfYear + 1)
                            .withDayOfMonth(dayOfMonth)
                    updateNoteUseCase(note.apply { time = date.millis })
                }
                .observeOn(scheduler.ui())
                .subscribe())
    }

    override fun onTimeFieldClick() {
        addDisposable(getNotesUseCase(mNoteId)
                .map { it.get() }
                .firstOrError()
                .observeOn(scheduler.ui())
                .subscribe { note ->
                    val date = DateTime(note.time)
                    view?.showTimePicker(
                            date.hourOfDay,
                            date.minuteOfHour,
                            getTimeFormatUseCase() == GetTimeFormatUseCase.TIME_FORMAT_24
                    )
                })
    }

    override fun onTimeSelected(hourOfDay: Int, minute: Int) {
        addDisposable(getNotesUseCase(mNoteId)
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
                    updateNoteUseCase(note.apply { time = date.millis })
                }
                .observeOn(scheduler.ui())
                .subscribe())
    }

    override fun onButtonEditClick() {
        view?.shoNoteEditView()
    }

    override fun onButtonUndoClick() {
        val selectedIndex = mNoteTextBuffer.indexOfFirst { it.current }
        if (selectedIndex > 0) {
            mNoteTextBuffer[selectedIndex].current = false
            val nextSelectedEntry = mNoteTextBuffer[selectedIndex - 1]
            nextSelectedEntry.current = true
            view?.enableUndoButton(selectedIndex - 1 != 0)
            view?.enableRedoButton(true)
            view?.showNoteText(nextSelectedEntry.title, nextSelectedEntry.content, nextSelectedEntry.textSpans)
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
            view?.showNoteText(nextSelectedEntry.title, nextSelectedEntry.content, nextSelectedEntry.textSpans)
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

    override fun onNoteTextChange(title: String, content: String, textSpans: List<MyTextSpan>) {
        val foundedIndex = mNoteTextBuffer.indexOfFirst { it.current }
        val selectedIndex = if (foundedIndex == -1) {
            if (mNoteTextBuffer.isEmpty()) {
                mNoteTextBuffer.add(UndoRedoEntry(title, content, textSpans, true))
            }
            mNoteTextBuffer.last().current = true
            mNoteTextBuffer.size - 1
        } else {
            foundedIndex
        }

        val selectedEntry = mNoteTextBuffer[selectedIndex]

        if (selectedEntry.title == title && selectedEntry.content == content && selectedEntry.textSpans == textSpans) {
            return
        }

        mNoteTextBuffer = ArrayList(mNoteTextBuffer.subList(0, selectedIndex + 1))

        mNoteTextBuffer[selectedIndex].current = false
        if (mNoteTextBuffer.size == UNDO_REDO_BUFFER_SIZE) {
            mNoteTextBuffer.removeAt(0)
        }
        mNoteTextBuffer.add(UndoRedoEntry(title, content, textSpans, true))

        view?.enableRedoButton(false)
        view?.enableUndoButton(true)
    }

    override fun onButtonMicClick() {
        view?.recordSpeech()
    }

    override fun onSpeechRecorded(curTitle: String, curContent: String, textSpans: List<MyTextSpan>, recordedText: String) {
        val content = when {
            curContent.isBlank() ->
                recordedText.capitalize()
            curContent.replace(Regex("[ \n]"), "").last() == '.' ->
                "$curContent ${recordedText.capitalize()}"
            else ->
                "$curContent $recordedText"
        }
        addDisposable(updateNoteUseCase(mNoteId, curTitle, content)
                .observeOn(scheduler.ui())
                .subscribe { onNoteTextChange(curTitle, content, textSpans) })
    }

    override fun onButtonShareClick() {
        addDisposable(getFullNotesUseCase(mNoteId)
                .firstOrError()
                .observeOn(scheduler.ui())
                .subscribe { note ->
                    if (note.isPresent) {
                        view?.shareNote(note.get())
                    }
                })
    }

    override fun onEditModeDisabled() {
        view?.hideRichTextOptions()
    }
}
