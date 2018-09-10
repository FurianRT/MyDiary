package com.furianrt.mydiary.main

import android.util.Log
import com.furianrt.mydiary.LOG_TAG
import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyNote
import com.furianrt.mydiary.main.listadapter.MainContentItem
import com.furianrt.mydiary.main.listadapter.MainHeaderItem
import com.furianrt.mydiary.main.listadapter.MainListItem
import io.reactivex.disposables.CompositeDisposable
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

private const val HEADER_IMAGE_NAME = "header_image"

class MainActivityPresenter(private val mDataManager: DataManager) : MainActivityContract.Presenter {

    private var mView: MainActivityContract.View? = null
    private val mCompositeDisposable = CompositeDisposable()

    override fun attachView(view: MainActivityContract.View) {
        mView = view
    }

    override fun detachView() {
        mCompositeDisposable.clear()
        mView = null
    }

    override fun addNote(note: MyNote) {
        val disposable = mDataManager.insertNote(note)
                .subscribe {id -> note.id = id}
        mCompositeDisposable.add(disposable)
        mView?.showAdded()
    }

    override fun deleteNote(note: MyNote) {
        val disposable = mDataManager.deleteNote(note)
                .subscribe()
        mCompositeDisposable.add(disposable)
        mView?.showDeleted()
    }

    override fun onViewCreate() {
        loadNotes()
        loadHeaderImage()
    }

    private fun loadHeaderImage() {
        val disposable = mDataManager.getImagePath(HEADER_IMAGE_NAME)
                .subscribe({ image ->
                    mView?.showHeaderImage(image)
                }, { Log.e(LOG_TAG, "Could not load header image") })
        mCompositeDisposable.add(disposable)
    }

    private fun loadNotes() {
        val disposable = mDataManager.getAllNotes()
                .map { formatNotes(toMap(it)) }
                .subscribe { mView?.showNotes(it) }
        mCompositeDisposable.add(disposable)
    }

    private fun toMap(notes: List<MyNote>): Map<Long, ArrayList<MyNote>> {
        val map = TreeMap<Long, ArrayList<MyNote>>(Comparator<Long> { p0, p1 -> p1.compareTo(p0) })
        for (note in notes) {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = note.time
            calendar.set(Calendar.DAY_OF_MONTH, 2)
            calendar.set(Calendar.HOUR, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            var value = map[calendar.timeInMillis]
            if (value == null) {
                value = ArrayList()
                map[calendar.timeInMillis] = value
            }
            value.add(note)
        }
        return map
    }

    private fun formatNotes(notes: Map<Long, List<MyNote>>): ArrayList<MainListItem> {
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

    override fun onMainListItemClick(note: MyNote) {
        val disposable = mDataManager.getAllNotes()
                .first(ArrayList())
                .subscribe { notes -> mView?.openNotePager(notes.indexOf(note)) }
        mCompositeDisposable.add(disposable)
    }

    override fun onHeaderImagePicked(imagePath: String?) {
        if (imagePath != null) {
            val disposable = mDataManager.saveImage(imagePath, HEADER_IMAGE_NAME)
                    .subscribe({ path -> mView?.showHeaderImage(path) },
                            { Log.e(LOG_TAG, "Could not save header image") })
            mCompositeDisposable.add(disposable)
        }
    }

    override fun onButtonAddNoteClick() {
        mView?.showViewNewNote()
    }

    override fun onButtonSetMainImageClick() {
        mView?.requestStoragePermissions()
    }

    override fun onMainListItemLongClick(note: MyNote) {
        mView?.showContextualActionBar()
    }

    override fun onStoragePermissionsGranted() {
        mView?.showImageExplorer()
    }
}