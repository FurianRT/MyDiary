package com.furianrt.mydiary.note.fragments.notefragment

import android.location.Address
import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyImage
import com.furianrt.mydiary.data.model.MyLocation
import com.furianrt.mydiary.data.model.MyNoteWithProp
import com.furianrt.mydiary.data.model.MyTag
import com.furianrt.mydiary.note.Mode
import com.furianrt.mydiary.utils.generateUniqueId
import com.google.android.gms.location.LocationResult
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

class NoteFragmentPresenter(private val mDataManager: DataManager) : NoteFragmentContract.Presenter {

    private var mView: NoteFragmentContract.View? = null
    private val mCompositeDisposable = CompositeDisposable()

    private lateinit var mNote: MyNoteWithProp

    override fun attachView(view: NoteFragmentContract.View) {
        mView = view
    }

    override fun detachView() {
        mCompositeDisposable.clear()
        mView = null
    }

    override fun onTagsFieldClick() {
        val allTagsObservable = mDataManager.getAllTags()
                .flatMapObservable { tags -> Observable.fromIterable(tags) }

        val disposable = mDataManager.getTags(mNote.tags)
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

    private fun getForecast(location: MyLocation) {
        val disposable = mDataManager.getForecast(location.lat, location.lon)
                .subscribe({ forecast ->
                    mNote.note.forecast = forecast
                    mView?.showForecast(forecast)
                }, { error -> error.printStackTrace() })
        mCompositeDisposable.add(disposable)
    }

    override fun onNoteTagsChanged(tags: List<MyTag>) {
        val checkedTags = tags.filter { it.isChecked }
        val disposable = mDataManager.deleteAllTagsForNote(mNote.note.id)
                .andThen(mDataManager.insertTagsForNote(mNote.note.id, checkedTags))
                .subscribe {
                    mNote.tags = checkedTags.map { it.id }
                    mView?.showTagNames(checkedTags.map { it.name })
                }
        mCompositeDisposable.add(disposable)
    }

    override fun onMapReady() {
        mNote.location?.let { mView?.zoomMap(it.lat, it.lon) }
    }

    override fun setNote(note: MyNoteWithProp) {
        mNote = note
    }

    override fun getNote() = mNote

    override fun onViewCreated(mode: Mode, locationEnabled: Boolean, networkAvailable: Boolean) {
        loadNoteImages(mNote.note.id)

        val location = mNote.location
        if (location != null) {
            mView?.showLocation(location)
            val forecast = mNote.note.forecast
            if (forecast != null) {
                mView?.showForecast(forecast)
            } else if (mode == Mode.ADD) {
                getForecast(location)
            }
        } else if (mode == Mode.ADD) {
            findLocation(locationEnabled, networkAvailable)
        }

        loadTagNames(mNote.tags)
        mNote.category?.let { mView?.showCategoryName(mNote.category?.name) }
        mNote.mood?.let { mView?.showMood(mNote.mood?.name) }
    }

    private fun loadNoteImages(noteId: String) {
        val disposable = mDataManager.getImagesForNote(noteId)
                .subscribe { images ->
                    mNote.images = images
                    if (images.isEmpty()) {
                        mView?.showNoImages()
                    } else {
                        mView?.showImages(images)
                    }
                }

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
                mNote.location = location
                addLocation(location)
                mView?.showLocation(location)
                getForecast(location)
            }
        }
    }

    private fun addLocation(location: MyLocation) {
        mDataManager.addLocation(location)
                .subscribe { id -> mNote.note.locationId = id }
    }

    private fun loadTagNames(tagsIds: List<Long>) {
        if (tagsIds.isEmpty()) return
        val disposable = mDataManager.getTags(tagsIds)
                .map { tags -> tags.map { it.name } }
                .subscribe { tagNames ->
                    mView?.showTagNames(tagNames)
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
                    val name = mNote.note.id + "_" + generateUniqueId()
                    return@map MyImage(name, url, mNote.note.id)
                }
                .flatMapSingle { image -> mDataManager.saveImageToStorage(image) }
                .flatMapCompletable { savedImage -> mDataManager.insertImage(savedImage) }
                .andThen(mDataManager.getImagesForNote(mNote.note.id))
                .subscribe { images -> mNote.images = images }

        mCompositeDisposable.add(disposable)
    }

    override fun onEditButtonClick() {
        mView?.showNoteEditView()
    }

    override fun onDeleteButtonClick() {
        val disposable = Observable.fromIterable(mNote.images)
                .flatMapSingle { image -> mDataManager.deleteImageFromStorage(image.name) }
                .collectInto(mutableListOf<Boolean>()) { l, i -> l.add(i) }
                .flatMapCompletable { mDataManager.deleteNote(mNote.note) }
                .subscribe { mView?.closeView() }

        mCompositeDisposable.add(disposable)
    }
}
