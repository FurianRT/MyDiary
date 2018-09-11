package com.furianrt.mydiary.note.fragments

import android.location.Address
import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyNote
import com.furianrt.mydiary.data.model.MyTag
import com.furianrt.mydiary.note.Mode
import com.google.android.gms.location.LocationResult
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

class NoteFragmentPresenter(private val mDataManager: DataManager) : NoteFragmentContract.Presenter {

    private var mView: NoteFragmentContract.View? = null
    private val mCompositeDisposable = CompositeDisposable()

    override fun attachView(view: NoteFragmentContract.View) {
        mView = view
    }

    override fun detachView() {
        mCompositeDisposable.clear()
        mView = null
    }

    override fun onLocationPermissionsGranted() {
        mView?.requestLocation()
    }

    override fun onLocationReceived(note: MyNote, result: LocationResult) {
        /*val lastLocation = result.lastLocation
        note.latitude = lastLocation.latitude
        note.longitude = lastLocation.longitude
        mView?.showMap(lastLocation.latitude, lastLocation.longitude)
        mView?.findAddress(lastLocation.latitude, lastLocation.longitude)*/
    }

    override fun onAddressFound(note: MyNote, addresses: List<Address>) {
        /*if (addresses.isNotEmpty()) {
            val address = addresses[0].getAddressLine(0)
            if (address != null) {
                note.address = address
                mView?.showAddress(address)
            } else {
                mView?.showAddressNotFound()
            }
        } else {
            mView?.showAddressNotFound()
        }

        getForecast(note)*/
    }

    override fun loadNoteProperties(noteId: Long) {
        if (noteId != 0L) {
            val disposable = mDataManager.getTagsForNote(noteId)
                    .map { tags -> tags.map { it.name } }
                    .subscribe { mView?.showTagNames(it) }
            mCompositeDisposable.add(disposable)
        }
    }

    override fun findLocation(note: MyNote, mode: Mode, locationEnabled: Boolean,
                              networkAvailable: Boolean) {
        /*val latitude = note.latitude
        val longitude = note.longitude
        if (latitude != null && longitude != null) {
            val address = note.address
            if (address != null) {
                mView?.showAddress(address)
            } else if (mode == Mode.ADD && networkAvailable) {
                mView?.findAddress(latitude, longitude)
            }

            mView?.showMap(latitude, longitude)

            val forecast = note.forecast
            if (forecast != null) {
                mView?.showForecast(forecast)
            } else if (mode == Mode.ADD && networkAvailable) {
                getForecast(note)
            }
        } else if (mode == Mode.ADD && networkAvailable && locationEnabled) {
            mView?.requestLocationPermissions()
        }*/
    }

    override fun onTagsFieldClick(note: MyNote) {
        val allTagsObservable = mDataManager.getAllTags()
                .flatMapObservable { tags -> Observable.fromIterable(tags) }

        val disposable = mDataManager.findNote(note.id)
                .isEmpty
                .flatMap { empty ->
                    return@flatMap if (empty) {
                        mDataManager.insertNote(note)
                    } else {
                        Single.fromCallable { note.id }
                    }
                }
                .flatMapPublisher { id ->
                    note.id = id
                    loadNoteProperties(id)
                    return@flatMapPublisher mDataManager.getTagsForNote(id)
                }
                .firstElement()
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

    private fun getForecast(note: MyNote) {
        /*val latitude = note.latitude
        val longitude = note.longitude
        if (latitude != null && longitude != null) {
            val disposable = mDataManager.getForecast(latitude, longitude)
                    .subscribe({ forecast ->
                        note.forecast = forecast
                        mView?.showForecast(forecast)
                    }, { error -> error.printStackTrace() })
            mCompositeDisposable.add(disposable)
        }*/
    }

    override fun changeNoteTags(note: MyNote, tags: List<MyTag>) {
        val disposable = mDataManager.deleteAllTagsForNote(note.id)
                .andThen(mDataManager.insertTagsForNote(note.id, tags.filter { it.isChecked }))
                .subscribe()
        mCompositeDisposable.add(disposable)
    }

    override fun onMapReady(latitude: Double?, longitude: Double?) {
        if (latitude != null && longitude != null) {
            mView?.zoomMap(latitude, longitude)
        }
    }

    override fun onStop(note: MyNote) {
        mDataManager.findNote(note.id)
                .subscribe(
                        { mDataManager.updateNote(note).subscribe() },
                        {},
                        { mDataManager.insertNote(note).subscribe { id -> note.id = id } }
                )
    }
}
