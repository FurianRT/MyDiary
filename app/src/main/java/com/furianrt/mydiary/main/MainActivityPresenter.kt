package com.furianrt.mydiary.main

import android.util.Log
import com.furianrt.mydiary.LOG_TAG
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
import io.reactivex.disposables.CompositeDisposable
import org.joda.time.DateTime
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

private const val HEADER_IMAGE_NAME = "header_image"

class MainActivityPresenter(private val mDataManager: DataManager) : MainActivityContract.Presenter {

    private var mView: MainActivityContract.View? = null
    private val mCompositeDisposable = CompositeDisposable()
    private var mSelectedNotes = ArrayList<MyNoteWithProp>()

    override fun attachView(view: MainActivityContract.View) {
        mView = view
    }

    override fun detachView() {
        mCompositeDisposable.clear()
        mView = null
    }

    private fun deleteImagesAndNote(note: MyNoteWithProp): Single<Boolean> {
        return Flowable.fromIterable(note.images)
                .flatMapSingle { image -> mDataManager.deleteImageFromStorage(image.name) }
                .collectInto(mutableListOf<Boolean>()) { l, i -> l.add(i) }
                .flatMap { mDataManager.deleteNote(note.note).toSingleDefault(true) }
    }

    override fun onMenuDeleteClick() {
        val disposable = Flowable.fromIterable(mSelectedNotes)
                .flatMapSingle { note -> deleteImagesAndNote(note) }
                .collectInto(mutableListOf<Boolean>()) { l, i -> l.add(i) }
                .subscribe { _ ->
                    mSelectedNotes.clear()
                    mView?.deactivateSelection()
                }

        mCompositeDisposable.add(disposable)
    }

    override fun onMenuAllNotesClick() {
        val disposable = mDataManager.getAllNotesWithProp()
                .first(ArrayList())
                .subscribe { notes ->
                    mSelectedNotes = ArrayList(notes)
                    mView?.activateSelection()
                    mView?.updateItemSelection(mSelectedNotes)
                }
        mCompositeDisposable.add(disposable)
    }

    override fun onViewStart() {
        loadNotes()
        loadHeaderImages()
    }

    override fun onStoragePermissionsGranted() {
        mView?.showImageExplorer()
    }

    override fun onHeaderImagesPicked(imageUrls: List<String>) {
        val disposable = mDataManager.getHeaderImages()
                .first(emptyList())
                .flatMapObservable { images -> Observable.fromIterable(images) }
                .defaultIfEmpty(MyHeaderImage("oops", "oops"))
                .flatMapSingle { image -> mDataManager.deleteImageFromStorage(image.name) }
                .flatMapCompletable { mDataManager.deleteAllHeaderImages() }
                .andThen(Observable.fromIterable(imageUrls))
                .map { url -> MyHeaderImage(HEADER_IMAGE_NAME + "_" + generateUniqueId(), url) }
                .flatMapSingle { image -> mDataManager.saveHeaderImageToStorage(image) }
                .flatMapCompletable { savedImage -> mDataManager.insertHeaderImage(savedImage) }
                .subscribe()

        mCompositeDisposable.add(disposable)
    }

    private fun loadHeaderImages() {
        val disposable = mDataManager.getHeaderImages()
                .subscribe { images ->
                    if (images.isEmpty()) {
                        mView?.showEmptyHeaderImage()
                    } else {
                        mView?.showHeaderImages(images)
                    }
                }

        mCompositeDisposable.add(disposable)
    }

    private fun loadNotes() {
        val disposable = mDataManager.getAllNotesWithProp()
                .map { formatNotes(toMap(it)) }
                .subscribe { mView?.showNotes(it, mSelectedNotes) }

        mCompositeDisposable.add(disposable)
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
            for (note in notes[date]!!) {
                list.add(MainContentItem(note))
            }
        }
        return list
    }

    override fun onFabMenuClick() {
        Log.e(LOG_TAG, "onFabMenuClick")
        if (mSelectedNotes.isEmpty()) {
            mView?.showViewNewNote()
        } else {
            mSelectedNotes.clear()
            mView?.deactivateSelection()
            mView?.updateItemSelection(mSelectedNotes)
        }
    }

    override fun onButtonSetMainImageClick() {
        mView?.requestStoragePermissions()
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
            mView?.activateSelection()
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
                mView?.deactivateSelection()
            }
            mSelectedNotes.contains(note) -> mSelectedNotes.remove(note)
            else -> mSelectedNotes.add(note)
        }
        mView?.updateItemSelection(mSelectedNotes)
    }

    private fun openNotePagerView(note: MyNoteWithProp) {
        val disposable = mDataManager.getAllNotesWithProp()
                .first(ArrayList())
                .subscribe { notes -> mView?.openNotePager(notes.indexOf(note)) }
        mCompositeDisposable.add(disposable)
    }

    override fun onButtonSettingsClick() {
        mView?.showSettingsView()
    }
}