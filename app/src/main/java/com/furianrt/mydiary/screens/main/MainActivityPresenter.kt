package com.furianrt.mydiary.screens.main

import android.util.Log
import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyNote
import com.furianrt.mydiary.data.model.MyNoteWithProp
import com.furianrt.mydiary.screens.main.listadapter.MainContentItem
import com.furianrt.mydiary.screens.main.listadapter.MainHeaderItem
import com.furianrt.mydiary.screens.main.listadapter.MainListItem
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
        private val dataManager: DataManager
) : MainActivityContract.Presenter() {

    companion object {
        private const val TAG = "MainActivityPresenter"
    }

    private var mSelectedNotes = ArrayList<MyNoteWithProp>()

    private fun deleteImagesAndNote(note: MyNote): Single<Boolean> =
            dataManager.getImagesForNote(note.id)
                    .first(emptyList())
                    .flatMapObservable { Observable.fromIterable(it) }
                    .filter { !it.isDeleted }
                    .flatMapSingle { image -> dataManager.deleteImageFromStorage(image.name) }
                    .collectInto(mutableListOf<Boolean>()) { l, i -> l.add(i) }
                    .flatMap { dataManager.deleteNote(note).toSingleDefault(true) }

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
        addDisposable(dataManager.getAllNotesWithProp()
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
        /*addDisposable(dataManager.getHeaderImages()
                .first(emptyList())
                .flatMapObservable { images -> Observable.fromIterable(images) }
                .defaultIfEmpty(MyHeaderImage("oops", "oops"))
                .flatMapSingle { image -> dataManager.deleteImageFromStorage(image.name) }
                .flatMapCompletable { dataManager.deleteAllHeaderImages() }
                .andThen(Observable.fromIterable(imageUrls))
                .map { uri -> MyHeaderImage(HEADER_IMAGE_NAME + "_" + generateUniqueId(), uri) }
                .flatMapSingle { image -> dataManager.saveHeaderImageToStorage(image) }
                .flatMapCompletable { savedImage -> dataManager.insertHeaderImage(savedImage) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe())*/
    }

    private fun loadProfile() {
        addDisposable(dataManager.getDbProfile()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { profile ->
                    //if (profile.hasPremium) {
                        view?.showPremiumProfile(profile)
                    //} else {
                   //     view?.showRegularProfile(profile)
                    //}
                })
        addDisposable(dataManager.observeSignOut()
                .subscribe {
                    view?.showAnonymousProfile()
                })
    }

    private fun loadHeaderImage() {     //todo порефакторить
        addDisposable(dataManager.getHeaderImages()
                .firstOrError()
                .flatMap { dbImages ->
                    return@flatMap when {
                        dbImages.isNotEmpty() && DateUtils.isToday(DateTime(dbImages.first().addedTime)) ->
                            Single.just(dbImages.first())
                        dbImages.isNotEmpty() && view?.networkAvailable() == false ->
                            Single.just(dbImages.first())
                        dbImages.isEmpty() && view?.networkAvailable() == true ->
                            dataManager.loadHeaderImages()
                                    .map { it.first() }
                                    .flatMap { dataManager.insertHeaderImage(it) }
                                    .flatMap { dataManager.getHeaderImages().firstOrError() }
                                    .map { it.first() }
                        dbImages.isNotEmpty() && view?.networkAvailable() == true ->
                            dataManager.loadHeaderImages()
                                    .onErrorReturn { dbImages }
                                    .map { list ->
                                        list.find { apiImage ->
                                            dbImages.find { it.id == apiImage.id } == null
                                        } ?: dbImages.first()
                                    }
                                    .flatMap { dataManager.insertHeaderImage(it) }
                                    .flatMap { dataManager.getHeaderImages().firstOrError() }
                                    .map { it.first() }
                        else ->
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
        if (dataManager.isSortDesc()) {
            view?.setSortAsc()
        } else {
            view?.setSortDesc()
        }
        addDisposable(dataManager.getAllNotesWithProp()
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
            return@Comparator if (dataManager.isSortDesc()) {
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
            val values = if (dataManager.isSortDesc()) {
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
        dataManager.setSortDesc(!dataManager.isSortDesc())
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
        addDisposable(dataManager.getAllNotesWithProp()
                .firstOrError()
                .flatMapObservable { Observable.fromIterable(it) }
                .toSortedList { o1, o2 ->
                    return@toSortedList if (dataManager.isSortDesc()) {
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

    override fun is24TimeFormat(): Boolean = dataManager.is24TimeFormat()

    override fun onButtonSyncClick() {
        when {
            !dataManager.isSignedIn() -> view?.showLoginView()
            //mProfile.hasPremium -> view?.startSyncService()
            else -> view?.startSyncService()
        }
    }

    override fun onButtonProfileClick() {
        if (dataManager.isSignedIn()) {
            view?.showProfileSettings()
        } else {
            view?.showLoginView()
        }
    }
}