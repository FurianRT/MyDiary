package com.furianrt.mydiary.main

import android.util.Log
import com.furianrt.mydiary.LOG_TAG
import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyHeaderImage
import com.furianrt.mydiary.data.model.MyNote
import com.furianrt.mydiary.data.model.MyNoteWithProp
import com.furianrt.mydiary.main.listadapter.MainContentItem
import com.furianrt.mydiary.main.listadapter.MainHeaderItem
import com.furianrt.mydiary.main.listadapter.MainListItem
import com.furianrt.mydiary.utils.generateUniqueId
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import org.joda.time.DateTime
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

private const val HEADER_IMAGE_NAME = "header_image"

class MainActivityPresenter(private val mDataManager: DataManager) : MainActivityContract.Presenter {

    private var mView: MainActivityContract.View? = null
    private val mCompositeDisposable = CompositeDisposable()
    private var mSelectedNotes = ArrayList<MyNote>()

    override fun attachView(view: MainActivityContract.View) {
        mView = view
    }

    override fun detachView() {
        mCompositeDisposable.clear()
        mView = null
    }

    override fun onMenuDeleteClick() {
        val disposable = mDataManager.deleteNotes(mSelectedNotes)
                .subscribe {
                    mSelectedNotes.clear()
                    mView?.deactivateSelection()
                }
        mCompositeDisposable.add(disposable)
    }

    override fun onMenuAllNotesClick() {
        val disposable = mDataManager.getAllNotes()
                .first(ArrayList())
                .subscribe { notes ->
                    mSelectedNotes = ArrayList(notes)
                    mView?.activateSelection()
                    mView?.updateItemSelection(mSelectedNotes)
                }
        mCompositeDisposable.add(disposable)
    }

    override fun onViewCreate() {
        loadNotes()
        loadHeaderImages()
    }

    override fun onStoragePermissionsGranted() {
        mView?.showImageExplorer()
    }

    private fun loadHeaderImages() {
        val disposable = mDataManager.getHeaderImages()
                .subscribe { images ->
                    if (!images.isEmpty()) {
                        mView?.showHeaderImages(images)
                    }
                }

        mCompositeDisposable.add(disposable)
    }

    private fun loadNotes() {
        val disposable = mDataManager.getNotesWithProp()
                .map { formatNotes(toMap(it)) }
                .subscribe {
                    Log.e(LOG_TAG, it.toString())
                    mView?.showNotes(it, mSelectedNotes)
                }
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

    override fun onHeaderImagesPicked(imageUrls: List<String>) {
        val disposable = Flowable.fromIterable(imageUrls)
                .map { url ->
                    val name = HEADER_IMAGE_NAME + "_" + generateUniqueId()
                    return@map MyHeaderImage(name, url)
                }
                .flatMapSingle { image -> mDataManager.saveHeaderImageToStorage(image) }
                .flatMapCompletable { savedImage -> mDataManager.insertHeaderImage(savedImage) }
                .subscribe()

        mCompositeDisposable.add(disposable)
    }

    override fun onFabMenuClick() {
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
            selectListItem(note.note)
        }
    }

    override fun onMainListItemLongClick(note: MyNoteWithProp, position: Int) {
        if (mSelectedNotes.isEmpty()) {
            mView?.activateSelection()
        }
        selectListItem(note.note)
    }

    override fun onSaveInstanceState() = mSelectedNotes

    override fun onRestoreInstanceState(selectedNotes: ArrayList<MyNote>?) {
        selectedNotes?.let { mSelectedNotes = selectedNotes }
        if (!mSelectedNotes.isEmpty()) {
            mView?.activateSelection()
        }
    }

    override fun onNotePagerViewFinished() {
        mView?.refreshTags()
    }

    private fun selectListItem(note: MyNote) {
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
        val disposable = mDataManager.getNotesWithProp()
                .first(ArrayList())
                .subscribe { notes -> mView?.openNotePager(notes.indexOf(note)) }
        mCompositeDisposable.add(disposable)
    }
}