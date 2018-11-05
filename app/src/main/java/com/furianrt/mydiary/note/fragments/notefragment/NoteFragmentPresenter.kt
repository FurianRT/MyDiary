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
import io.reactivex.disposables.CompositeDisposable
import org.joda.time.DateTime

class NoteFragmentPresenter(private val mDataManager: DataManager) : NoteFragmentContract.Presenter {

    private var mView: NoteFragmentContract.View? = null
    private val mCompositeDisposable = CompositeDisposable()

    private lateinit var mNote: MyNote

    override fun attachView(view: NoteFragmentContract.View) {
        mView = view
    }

    override fun detachView() {
        mCompositeDisposable.clear()
        mView = null
    }

    override fun onMoodFieldClick() {
        val disposable = mDataManager.getAllMoods()
                .subscribe { moods -> mView?.showMoodsDialog(moods) }

        mCompositeDisposable.add(disposable)
    }

    override fun onTagsFieldClick() {
        val allTagsObservable = mDataManager.getAllTags()
                .flatMapObservable { tags -> Observable.fromIterable(tags) }

        val disposable = mDataManager.getTagsForNote(mNote.id)
                .first(emptyList())
                .flatMapObservable { tags -> Observable.fromIterable(tags) }
                .map { tag -> tag.apply { isChecked = true } }
                .concatWith(allTagsObservable)
                .collectInto(ArrayList<MyTag>()) { list, tag ->
                    val foundedTag = list.find { it.id == tag.id }
                    if (foundedTag == null) list.add(tag)
                }
                .subscribe { tags -> mView?.showTagsDialog(tags) }

        mCompositeDisposable.add(disposable)
    }

    override fun onCategoryFieldClick() {
        mView?.showCategoriesDialog(mNote.id)
    }

    override fun onNoteTagsChanged(tags: List<MyTag>) {
        val disposable = mDataManager.replaceNoteTags(mNote.id, tags)
                .subscribe()

        mCompositeDisposable.add(disposable)
    }

    override fun onMapReady() {
        //mNote.location?.let { mView?.zoomMap(it.lat, it.lon) }
    }

    override fun loadImages(noteId: String) {
        val disposable = mDataManager.getImagesForNote(noteId)
                .subscribe { images ->
                    if (images.isEmpty()) {
                        mView?.showNoImages()
                    } else {
                        mView?.showImages(images.sortedBy { it.order })
                    }
                }

        mCompositeDisposable.add(disposable)
    }

    override fun loadTags(noteId: String) {
        val disposable = mDataManager.getTagsForNote(noteId)
                .defaultIfEmpty(emptyList())
                .subscribe { tags ->
                    if (tags.isEmpty()) {
                        mView?.showNoTagsMessage()
                    } else {
                        mView?.showTagNames(tags.map { it.name })
                    }
                }

        mCompositeDisposable.add(disposable)
    }

    override fun loadNote(noteId: String, mode: Mode, locationEnabled: Boolean,
                          networkAvailable: Boolean) {

        val disposable = mDataManager.getNote(noteId)
                .subscribe { note ->
                    mNote = note
                    mView?.showNoteContent(note)
                    showNoteMood(note.moodId)
                    showNoteCategory(note.categoryId)
                    //showNoteLocation(note, mode, locationEnabled, networkAvailable)
                }

        mCompositeDisposable.add(disposable)
    }

    override fun loadNoteAppearance(noteId: String) {
        val disposable = mDataManager.getNoteAppearance(noteId)
                .subscribe { appearance -> mView?.updateNoteAppearance(appearance) }

        mCompositeDisposable.add(disposable)
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

        val disposable = mDataManager.getMood(moodId)
                .subscribe { mood -> mView?.showMood(mood) }

        mCompositeDisposable.add(disposable)
    }

    private fun showNoteCategory(categoryId: Long) {
        if (categoryId == 0L) {
            mView?.showNoCategoryMessage()
            return
        }

        val disposable = mDataManager.getCategory(categoryId)
                .subscribe { category -> mView?.showCategory(category) }

        mCompositeDisposable.add(disposable)
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
        val disposable = mDataManager.getForecast(location.lat, location.lon)
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
                })

        mCompositeDisposable.add(disposable)
    }

    private fun addLocation(location: MyLocation) {
        Log.e(LOG_TAG, "NoteFragmentPresenter.addLocation")
        mNote.locationName = location.name
        val disposable = mDataManager.addLocation(location)
                .andThen(mDataManager.updateNote(mNote))
                .subscribe {
                    if (mDataManager.isLocationEnabled()) {
                        mView?.showLocation(location)
                    }
                }

        mCompositeDisposable.add(disposable)
    }

    override fun onAddImageButtonClick() {
        mView?.requestStoragePermissions()
    }

    override fun onStoragePermissionsGranted() {
        mView?.showImageExplorer()
    }

    override fun onNoteImagesPicked(imageUrls: List<String>) {
        val disposable = Flowable.fromIterable(imageUrls)
                .map { url ->
                    val name = mNote.id + "_" + generateUniqueId()
                    return@map MyImage(name, url, mNote.id, DateTime.now().millis)
                }
                .flatMapSingle { image -> mDataManager.saveImageToStorage(image) }
                .flatMapCompletable { savedImage -> mDataManager.insertImage(savedImage) }
                .andThen(mDataManager.getImagesForNote(mNote.id))
                .subscribe()

        mCompositeDisposable.add(disposable)
    }

    override fun onEditButtonClick() {
        mView?.showNoteEditView(mNote)
    }

    override fun onDeleteButtonClick() {
        val disposable = mDataManager.getImagesForNote(mNote.id)
                .flatMapIterable { it }
                .flatMapSingle { image -> mDataManager.deleteImageFromStorage(image.name) }
                .collectInto(mutableListOf<Boolean>()) { l, i -> l.add(i) }
                .flatMapCompletable { mDataManager.deleteNote(mNote) }
                .subscribe { mView?.closeView() }

        mCompositeDisposable.add(disposable)
    }

    override fun onToolbarImageClick() {
        mView?.showGalleryView(mNote.id)
    }

    override fun onMoodPicked(mood: MyMood) {
        mNote.moodId = mood.id
        val disposable = mDataManager.updateNote(mNote)
                .subscribe()

        mCompositeDisposable.add(disposable)
    }

    override fun onNoMoodPicked() {
        mNote.moodId = 0
        val disposable = mDataManager.updateNote(mNote)
                .subscribe { mView?.showNoMoodMessage() }

        mCompositeDisposable.add(disposable)
    }

    override fun onCategoryPicked(category: MyCategory) {
        mNote.categoryId = category.id
        val disposable = mDataManager.updateNote(mNote)
                .subscribe { mView?.showCategory(category) }

        mCompositeDisposable.add(disposable)
    }

    override fun onNoCategoryPicked() {
        mNote.categoryId = 0
        val disposable = mDataManager.updateNote(mNote)
                .subscribe { mView?.showNoCategoryMessage() }

        mCompositeDisposable.add(disposable)
    }

    override fun onAppearanceButtonClick() {
        mView?.showNoteSettingsView(mNote)
    }

    override fun updateNoteText(noteId: String, noteTitle: String, noteContent: String) {
        val disposable = mDataManager.updateNoteText(noteId, noteTitle, noteContent)
                .subscribe()

        mCompositeDisposable.add(disposable)
    }
}
