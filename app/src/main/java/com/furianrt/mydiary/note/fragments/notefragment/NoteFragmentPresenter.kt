package com.furianrt.mydiary.note.fragments.notefragment

import android.location.Address
import android.util.Log
import com.furianrt.mydiary.LOG_TAG
import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.api.Forecast
import com.furianrt.mydiary.data.model.*
import com.furianrt.mydiary.note.Mode
import com.furianrt.mydiary.utils.generateUniqueId
import com.google.android.gms.location.LocationResult
import io.reactivex.Flowable
import io.reactivex.Observable
import org.joda.time.DateTime

class NoteFragmentPresenter(private val mDataManager: DataManager) : NoteFragmentContract.Presenter() {

    private var mView: NoteFragmentContract.View? = null

    private lateinit var mNote: MyNote

    override fun attachView(view: NoteFragmentContract.View) {
        mView = view
    }

    override fun detachView() {
        super.detachView()
        mView = null
    }

    override fun onMoodFieldClick() {
        addDisposable(mDataManager.getAllMoods()
                .subscribe { moods -> mView?.showMoodsDialog(moods) })

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
                .subscribe { tags -> mView?.showTagsDialog(tags) })
    }

    override fun onCategoryFieldClick() {
        mView?.showCategoriesDialog(mNote.id)
    }

    override fun onNoteTagsChanged(tags: List<MyTag>) {
        addDisposable(mDataManager.replaceNoteTags(mNote.id, tags)
                .subscribe())
    }

    override fun onMapReady() {
        //mNote.location?.let { mView?.zoomMap(it.lat, it.lon) }
    }

    override fun loadImages(noteId: String) {
        addDisposable(mDataManager.getImagesForNote(noteId)
                .subscribe { images ->
                    if (images.isEmpty()) {
                        mView?.showNoImages()
                    } else {
                        mView?.showImages(images.sortedBy { it.order })
                    }
                })
    }

    override fun loadTags(noteId: String) {
        addDisposable(mDataManager.getTagsForNote(noteId)
                .defaultIfEmpty(emptyList())
                .subscribe { tags ->
                    if (tags.isEmpty()) {
                        mView?.showNoTagsMessage()
                    } else {
                        mView?.showTagNames(tags.map { it.name })
                    }
                })
    }

    override fun loadNote(noteId: String, mode: Mode, locationEnabled: Boolean,
                          networkAvailable: Boolean) {
        addDisposable(mDataManager.getNote(noteId)
                .subscribe { note ->
                    mNote = note
                    mView?.showNoteContent(note)
                    showNoteMood(note.moodId)
                    showNoteCategory(note.categoryId)
                    //showNoteLocation(note, mode, locationEnabled, networkAvailable)
                })
    }

    override fun loadNoteAppearance(noteId: String) {
        addDisposable(mDataManager.getNoteAppearance(noteId)
                .subscribe { appearance -> mView?.updateNoteAppearance(appearance) })
    }

    private fun showForecast(forecast: Forecast?, location: MyLocation, mode: Mode) {
        if (mDataManager.isWeatherEnabled()) {
            if (forecast != null) {
                mView?.showForecast(forecast)
            } else if (mode == Mode.ADD) {
                addForecast(location)
            }
        }
    }

    private fun showNoteLocation(note: MyNoteWithProp, mode: Mode, locationEnabled: Boolean,
                                 networkAvailable: Boolean) {
        val location = note.location
        if (location != null) {
            if (mDataManager.isLocationEnabled()) {
                mView?.showLocation(location)
            }
            showForecast(note.note.forecast, location, mode)
        } else if (mode == Mode.ADD) {
            findLocation(locationEnabled, networkAvailable)
        }
    }

    private fun showNoteMood(moodId: Int) {
        if (moodId == 0 || !mDataManager.isMoodEnabled()) {
            mView?.showNoMoodMessage()
            return
        }

        addDisposable(mDataManager.getMood(moodId)
                .subscribe { mood -> mView?.showMood(mood) })
    }

    private fun showNoteCategory(categoryId: Long) {
        if (categoryId == 0L) {
            mView?.showNoCategoryMessage()
            return
        }

        addDisposable(mDataManager.getCategory(categoryId)
                .subscribe { category -> mView?.showCategory(category) })
    }

    private fun findLocation(locationEnabled: Boolean, networkAvailable: Boolean) {
        if (locationEnabled && networkAvailable) {
            mView?.requestLocationPermissions()
        }
    }

    override fun onLocationPermissionsGranted() {
        mView?.requestLocation()
    }

    override fun onLocationReceived(result: LocationResult) {
        val lastLocation = result.lastLocation
        mView?.findAddress(lastLocation.latitude, lastLocation.longitude)
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
                    Log.e(LOG_TAG, "NoteFragmentPresenter.addForecast")
                    mNote.forecast = forecast
                    return@flatMapCompletable mDataManager.updateNote(mNote)
                }
                .subscribe({
                    val forecast = mNote.forecast
                    if (forecast != null && mDataManager.isWeatherEnabled()) {
                        mView?.showForecast(forecast)
                    }
                }, { error ->
                    Log.i(LOG_TAG, "Could't load weather: ${error.stackTrace}")
                }))
    }

    private fun addLocation(location: MyLocation) {
        Log.e(LOG_TAG, "NoteFragmentPresenter.addLocation")
        mNote.locationName = location.name
        addDisposable(mDataManager.addLocation(location)
                .andThen(mDataManager.updateNote(mNote))
                .subscribe {
                    if (mDataManager.isLocationEnabled()) {
                        mView?.showLocation(location)
                    }
                })
    }

    override fun onAddImageButtonClick() {
        mView?.requestStoragePermissions()
    }

    override fun onStoragePermissionsGranted() {
        mView?.showImageExplorer()
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

    override fun onEditButtonClick() {
        mView?.showNoteEditView(mNote)
    }

    override fun onDeleteButtonClick() {
        addDisposable(mDataManager.getImagesForNote(mNote.id)
                .first(emptyList())
                .flatMapObservable { Observable.fromIterable(it) }
                .flatMapSingle { image -> mDataManager.deleteImageFromStorage(image.name) }
                .collectInto(mutableListOf<Boolean>()) { l, i -> l.add(i) }
                .flatMapCompletable { mDataManager.deleteNote(mNote) }
                .subscribe { mView?.closeView() })
    }

    override fun onToolbarImageClick() {
        mView?.showGalleryView(mNote.id)
    }

    override fun onMoodPicked(mood: MyMood) {
        mNote.moodId = mood.id
        addDisposable(mDataManager.updateNote(mNote)
                .subscribe())
    }

    override fun onNoMoodPicked() {
        mNote.moodId = 0
        addDisposable(mDataManager.updateNote(mNote)
                .subscribe { mView?.showNoMoodMessage() })
    }

    override fun onCategoryPicked(category: MyCategory) {
        mNote.categoryId = category.id
        addDisposable(mDataManager.updateNote(mNote)
                .subscribe { mView?.showCategory(category) })
    }

    override fun onNoCategoryPicked() {
        mNote.categoryId = 0
        addDisposable(mDataManager.updateNote(mNote)
                .subscribe { mView?.showNoCategoryMessage() })
    }

    override fun onAppearanceButtonClick() {
        mView?.showNoteSettingsView(mNote.id)
    }

    override fun updateNoteText(noteId: String, noteTitle: String, noteContent: String) {
        addDisposable(mDataManager.updateNoteText(noteId, noteTitle, noteContent)
                .subscribe())
    }
}
