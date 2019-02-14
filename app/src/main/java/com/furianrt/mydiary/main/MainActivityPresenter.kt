package com.furianrt.mydiary.main

import android.util.Log
import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyHeaderImage
import com.furianrt.mydiary.data.model.MyNoteWithProp
import com.furianrt.mydiary.main.listadapter.MainContentItem
import com.furianrt.mydiary.main.listadapter.MainHeaderItem
import com.furianrt.mydiary.main.listadapter.MainListItem
import com.furianrt.mydiary.utils.generateUniqueId
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import org.joda.time.DateTime
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class MainActivityPresenter(private val mDataManager: DataManager) : MainActivityContract.Presenter() {

    companion object {
        private const val TAG = "MainActivityPresenter"
        private const val HEADER_IMAGE_NAME = "header_image"
    }

    private var mSelectedNotes = ArrayList<MyNoteWithProp>()

    private fun deleteImagesAndNote(note: MyNoteWithProp): Single<Boolean> =
            Flowable.fromIterable(note.images)
                    .flatMapSingle { image -> mDataManager.deleteImageFromStorage(image.name) }
                    .collectInto(mutableListOf<Boolean>()) { l, i -> l.add(i) }
                    .flatMap { mDataManager.deleteNote(note.note).toSingleDefault(true) }

    override fun onMenuDeleteClick() {
        addDisposable(Flowable.fromIterable(mSelectedNotes)
                .flatMapSingle { note -> deleteImagesAndNote(note) }
                .collectInto(mutableListOf<Boolean>()) { l, i -> l.add(i) }
                .ignoreElement()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    mSelectedNotes.clear()
                    view?.deactivateSelection()
                })
    }

    override fun onMenuAllNotesClick() {
        addDisposable(mDataManager.getAllNotesWithProp()
                .first(ArrayList())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { notes ->
                    mSelectedNotes = ArrayList(notes)
                    view?.activateSelection()
                    view?.updateItemSelection(mSelectedNotes)
                })
    }

    override fun onViewStart() {
        loadNotes()
        loadHeaderImages()
    }

    override fun onStoragePermissionsGranted() {
        view?.showImageExplorer()
    }

    override fun onHeaderImagesPicked(imageUrls: List<String>) {
        addDisposable(mDataManager.getHeaderImages()
                .first(emptyList())
                .flatMapObservable { images -> Observable.fromIterable(images) }
                .defaultIfEmpty(MyHeaderImage("oops", "oops"))
                .flatMapSingle { image -> mDataManager.deleteImageFromStorage(image.name) }
                .flatMapCompletable { mDataManager.deleteAllHeaderImages() }
                .andThen(Observable.fromIterable(imageUrls))
                .map { url -> MyHeaderImage(HEADER_IMAGE_NAME + "_" + generateUniqueId(), url) }
                .flatMapSingle { image -> mDataManager.saveHeaderImageToStorage(image) }
                .flatMapCompletable { savedImage -> mDataManager.insertHeaderImage(savedImage) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe())
    }

    private fun loadHeaderImages() {
        addDisposable(mDataManager.getHeaderImages()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { images ->
                    if (images.isEmpty()) {
                        view?.showEmptyHeaderImage()
                    } else {
                        view?.showHeaderImages(images)
                    }
                })
    }

    private fun loadNotes() {
        addDisposable(mDataManager.getAllNotesWithProp()
                .map { formatNotes(toMap(it)) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.showNotes(it, mSelectedNotes) })
    }

    private fun toMap(notes: List<MyNoteWithProp>): Map<Long, ArrayList<MyNoteWithProp>> {
        val map = TreeMap<Long, ArrayList<MyNoteWithProp>>(Comparator<Long> { p0, p1 -> p1.compareTo(p0) })
        for (note in notes) {
            val dateTime = DateTime(note.note.time).dayOfMonth()
                    .withMinimumValue()
                    .withTimeAtStartOfDay()
                    .withMillisOfDay(0)

            var value = map[dateTime.millis]
            if (value == null) {
                value = ArrayList()
                map[dateTime.millis] = value
            }
            value.add(note)
        }
        return map
    }

    private fun formatNotes(notes: Map<Long, List<MyNoteWithProp>>): ArrayList<MainListItem> {
        val list = ArrayList<MainListItem>()
        for (date in notes.keys) {
            val header = MainHeaderItem(date)
            list.add(header)
            for (note in notes.getValue(date)) {
                list.add(MainContentItem(note))
            }
        }
        return list
    }

    override fun onFabMenuClick() {
        Log.e(TAG, "onFabMenuClick")
        if (mSelectedNotes.isEmpty()) {
            view?.showViewNewNote()
        } else {
            mSelectedNotes.clear()
            view?.deactivateSelection()
            view?.updateItemSelection(mSelectedNotes)
        }
    }

    override fun onButtonSetMainImageClick() {
        view?.requestStoragePermissions()
    }

    override fun onMainListItemClick(note: MyNoteWithProp, position: Int) {
        if (mSelectedNotes.isEmpty()) {
            openNotePagerView(note)
        } else {
            selectListItem(note)
        }
    }

    override fun onMainListItemLongClick(note: MyNoteWithProp, position: Int) {
        if (mSelectedNotes.isEmpty()) {
            view?.activateSelection()
        }
        selectListItem(note)
    }

    override fun onSaveInstanceState() = mSelectedNotes

    override fun onRestoreInstanceState(selectedNotes: ArrayList<MyNoteWithProp>?) {
        selectedNotes?.let { mSelectedNotes = selectedNotes }
    }

    private fun selectListItem(note: MyNoteWithProp) {
        when {
            mSelectedNotes.contains(note) && mSelectedNotes.size == 1 -> {
                mSelectedNotes.remove(note)
                view?.deactivateSelection()
            }
            mSelectedNotes.contains(note) -> mSelectedNotes.remove(note)
            else -> mSelectedNotes.add(note)
        }
        view?.updateItemSelection(mSelectedNotes)
    }

    private fun openNotePagerView(note: MyNoteWithProp) {
        addDisposable(mDataManager.getAllNotesWithProp()
                .first(ArrayList())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { notes -> view?.openNotePager(notes.indexOf(note)) })
    }

    override fun onButtonSettingsClick() {
        view?.showSettingsView()
    }

    override fun is24TimeFormat(): Boolean =
            mDataManager.getTimeFormat() == DataManager.TIME_FORMAT_24

    override fun onButtonSyncClick() {
        //todo проверка на наличие премиума
        view?.showPremiumView()
    }

    override fun onButtonProfileClick() {
        //todo различные проверки
        view?.showLoginView()
    }
}