package com.furianrt.mydiary.note.fragments

import android.location.Address
import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyNote
import com.furianrt.mydiary.data.model.MyTag
import com.furianrt.mydiary.note.Mode
import com.google.android.gms.location.LocationResult
import io.reactivex.Completable
import io.reactivex.Observable

class NoteFragmentPresenter(private val mDataManager: DataManager) : NoteFragmentContract.Presenter {

    private var mView: NoteFragmentContract.View? = null

    override fun attachView(view: NoteFragmentContract.View) {
        mView = view
    }

    override fun detachView() {
        mView = null
    }

    override fun onPermissionsGranted() {
        mView?.requestLocation()
    }

    override fun onLocationReceived(note: MyNote, result: LocationResult) {
        val lastLocation = result.lastLocation
        note.latitude = lastLocation.latitude
        note.longitude = lastLocation.longitude
        mView?.findAddress(lastLocation.latitude, lastLocation.longitude)
    }

    override fun onAddressFound(note: MyNote, addresses: List<Address>) {
        if (addresses.isNotEmpty()) {
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

        mView?.showMap()

        getForecast(note)
    }

    override fun loadNoteProperties(note: MyNote) {
        if (note.id != 0L) {
            mDataManager.getTagsForNote(note.id)
                    .subscribe { tags -> mView?.showTagNames(tags.map { it.name }) }
        }
    }

    override fun findLocation(note: MyNote, mode: Mode) {
        val latitude = note.latitude
        val longitude = note.longitude
        if (latitude != null && longitude != null) {
            val address = note.address
            if (address != null) {
                mView?.showAddress(address)
            } else {
                mView?.findAddress(latitude, longitude)
            }

            mView?.showMap()

            val forecast = note.forecast
            if (forecast != null) {
                mView?.showForecast(forecast)
            } else {
                getForecast(note)
            }
        } else if (mode == Mode.ADD) {
            mView?.requestLocationPermissions()
        }
    }

    override fun onTagsFieldClick(note: MyNote) {
        val allTagsObservable = mDataManager.getAllTags()
                .flatMapObservable { tags -> Observable.fromIterable(tags) }

        mDataManager.getTagsForNote(note.id)
                .flatMapObservable { tags -> Observable.fromIterable(tags) }
                .map { tag -> tag.apply { isChecked = true } }
                .concatWith(allTagsObservable)
                .collectInto(ArrayList<MyTag>()) { list, tag ->
                    val foundedTag = list.find { it.id == tag.id }
                    if (foundedTag == null) list.add(tag)
                }
                .subscribe { tags -> mView?.showTagsDialog(tags) }
    }

    private fun getForecast(note: MyNote) {
        val latitude = note.latitude
        val longitude = note.longitude
        if (latitude != null && longitude != null) {
            mDataManager.getForecast(latitude, longitude)
                    .subscribe({ forecast ->
                        note.forecast = forecast
                        mView?.showForecast(forecast)
                    }, { error -> error.printStackTrace() })
        }
    }

    override fun changeNoteTags(note: MyNote, tags: List<MyTag>) {
        Completable.concatArray(mDataManager.deleteAllTagsForNote(note.id),
                mDataManager.insertTagsForNote(note.id, tags.filter { it.isChecked }))
                .andThen(mDataManager.getTagsForNote(note.id))
                .subscribe { tagList -> mView?.showTagNames(tagList.map { it.name }) }
    }

    override fun onMapReady(latitude: Double?, longitude: Double?) {
        if (latitude != null && longitude != null) {
            mView?.zoomMap(latitude, longitude)
        }
    }
}
