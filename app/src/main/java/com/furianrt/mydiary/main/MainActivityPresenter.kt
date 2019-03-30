package com.furianrt.mydiary.main

import android.util.Log
import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyNote
import com.furianrt.mydiary.data.model.MyNoteWithProp
import com.furianrt.mydiary.data.model.MyProfile
import com.furianrt.mydiary.main.listadapter.MainContentItem
import com.furianrt.mydiary.main.listadapter.MainHeaderItem
import com.furianrt.mydiary.main.listadapter.MainListItem
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import net.danlew.android.joda.DateUtils
import org.joda.time.DateTime
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class MainActivityPresenter(
        private val mDataManager: DataManager
) : MainActivityContract.Presenter() {

    companion object {
        private const val TAG = "MainActivityPresenter"
        private const val HEADER_IMAGE_NAME = "header_image"
    }

    private var mSelectedNotes = ArrayList<MyNoteWithProp>()
    private var mProfile = MyProfile()

    private fun deleteImagesAndNote(note: MyNote): Single<Boolean> =
            mDataManager.getImagesForNote(note.id)
                    .first(emptyList())
                    .flatMapObservable { Observable.fromIterable(it) }
                    .filter { !it.isDeleted }
                    .flatMapSingle { image -> mDataManager.deleteImageFromStorage(image.name) }
                    .collectInto(mutableListOf<Boolean>()) { l, i -> l.add(i) }
                    .flatMap { mDataManager.deleteNote(note).toSingleDefault(true) }

    override fun onButtonDeleteClick() {
        view?.showDeleteConfirmationDialog(mSelectedNotes.map { it.note })
    }

    override fun onButtonDeleteConfirmClick(notes: List<MyNote>) {
        addDisposable(Flowable.fromIterable(notes)
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

    override fun onViewResume() {
        loadProfile()
        loadNotes()
        loadHeaderImage()
    }

    override fun onStoragePermissionsGranted() {
        view?.showImageExplorer()
    }

    override fun onHeaderImagesPicked(imageUrls: List<String>) {    //todo добавить настройки выбора картинки
        /*addDisposable(mDataManager.getHeaderImages()
                .first(emptyList())
                .flatMapObservable { images -> Observable.fromIterable(images) }
                .defaultIfEmpty(MyHeaderImage("oops", "oops"))
                .flatMapSingle { image -> mDataManager.deleteImageFromStorage(image.name) }
                .flatMapCompletable { mDataManager.deleteAllHeaderImages() }
                .andThen(Observable.fromIterable(imageUrls))
                .map { uri -> MyHeaderImage(HEADER_IMAGE_NAME + "_" + generateUniqueId(), uri) }
                .flatMapSingle { image -> mDataManager.saveHeaderImageToStorage(image) }
                .flatMapCompletable { savedImage -> mDataManager.insertHeaderImage(savedImage) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe())*/
    }

    private fun loadProfile() {
        addDisposable(mDataManager.getDbProfile()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { profile ->
                    mProfile = profile
                    if (profile.hasPremium) {
                        view?.showPremiumProfile(profile)
                    } else {
                        view?.showRegularProfile(profile)
                    }
                })
    }

    private fun loadHeaderImage() {     //todo порефакторить
        addDisposable(mDataManager.getHeaderImages()
                .firstOrError()
                .flatMap { dbImages ->
                    return@flatMap if (dbImages.isNotEmpty() && DateUtils.isToday(DateTime(dbImages.first().addedTime))) {
                        Single.just(dbImages.first())
                    } else if (dbImages.isNotEmpty() && view?.networkAvailable() == false) {
                        Single.just(dbImages.first())
                    } else if (dbImages.isEmpty() && view?.networkAvailable() == true) {
                        mDataManager.loadHeaderImages()
                                .map { it.first() }
                                .flatMap { mDataManager.insertHeaderImage(it) }
                                .flatMap { mDataManager.getHeaderImages().firstOrError() }
                                .map { it.first() }
                    } else if (dbImages.isNotEmpty() && view?.networkAvailable() == true) {
                        mDataManager.loadHeaderImages()
                                .onErrorReturn { dbImages }
                                .map { list ->
                                    list.find { apiImage ->
                                        dbImages.find { it.id == apiImage.id } == null
                                    } ?: dbImages.first()
                                }
                                .flatMap { mDataManager.insertHeaderImage(it) }
                                .flatMap { mDataManager.getHeaderImages().firstOrError() }
                                .map { it.first() }
                    } else {
                        throw Exception()
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view?.showHeaderImage(it)
                }, { error ->
                    error.printStackTrace()
                    view?.showEmptyHeaderImage()
                }))
    }

    private fun loadNotes() {
        if (mDataManager.isSortDesc()) {
            view?.setSortAsc()
        } else {
            view?.setSortDesc()
        }
        addDisposable(mDataManager.getAllNotesWithProp()
                .map { formatNotes(toMap(it)) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { items ->
                    val notes = items
                            .filter { it is MainContentItem }
                            .map { (it as MainContentItem).note }
                    view?.showNotesCountToday(notes
                            .filter { DateUtils.isToday(DateTime(it.note.creationTime)) }
                            .size)
                    view?.showImageCount(notes.sumBy {
                        it.images
                                .filter { image -> !image.isDeleted }
                                .size
                    })
                    view?.showNotesTotal(notes.size)
                    view?.showNotes(items, mSelectedNotes)
                })
    }

    private fun toMap(notes: List<MyNoteWithProp>): Map<Long, ArrayList<MyNoteWithProp>> {
        val map = TreeMap<Long, ArrayList<MyNoteWithProp>>(Comparator<Long> { p0, p1 ->
            return@Comparator if (mDataManager.isSortDesc()) {
                p1.compareTo(p0)
            } else {
                p0.compareTo(p1)
            }
        })
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
            val values = if (mDataManager.isSortDesc()) {
                notes.getValue(date).sortedByDescending { it.note.time }
            } else {
                notes.getValue(date).sortedBy { it.note.time }
            }
            for (note in values) {
                list.add(MainContentItem(note))
            }
        }
        return list
    }

    override fun onFabMenuClick() {
        Log.e(TAG, "onFabMenuClick " + (view == null).toString())
        if (mSelectedNotes.isEmpty()) {
            view?.showViewNewNote()
        } else {
            mSelectedNotes.clear()
            view?.deactivateSelection()
            view?.updateItemSelection(mSelectedNotes)
        }
    }

    override fun onButtonSortClick() {
        mDataManager.setSortDesc(!mDataManager.isSortDesc())
        loadNotes()
    }

    override fun onMainImageClick() {
        view?.showImageOptions()
        //view?.requestStoragePermissions()
    }

    override fun onButtonImageSettingsClick() {
        view?.showViewImageSettings()
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
                .firstOrError()
                .flatMapObservable { Observable.fromIterable(it) }
                .toSortedList { o1, o2 ->
                    return@toSortedList if (mDataManager.isSortDesc()) {
                        o2.note.time.compareTo(o1.note.time)
                    } else {
                        o1.note.time.compareTo(o2.note.time)
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { notes -> view?.openNotePager(notes.indexOf(note), note) })
    }

    override fun onButtonSettingsClick() {
        view?.showSettingsView()
    }

    override fun is24TimeFormat(): Boolean =
            mDataManager.is24TimeFormat()

    override fun onButtonSyncClick() {
        when {
            mProfile.email.isEmpty() -> view?.showLoginView()
            mProfile.hasPremium -> view?.startSyncService()
            else -> view?.showPremiumView()
        }
    }

    override fun onButtonProfileClick() {
        if (mProfile.email.isEmpty()) {
            view?.showLoginView()
        } else {
            view?.showProfileSettings()
        }
    }

    override fun onSignOut() {
        mProfile = MyProfile()
        view?.showAnonymousProfile()
    }
}