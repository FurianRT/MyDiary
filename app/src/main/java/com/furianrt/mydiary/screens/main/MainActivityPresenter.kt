package com.furianrt.mydiary.screens.main

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyNoteWithProp
import com.furianrt.mydiary.data.model.SyncProgressMessage
import com.furianrt.mydiary.screens.main.listadapter.MainContentItem
import com.furianrt.mydiary.screens.main.listadapter.MainHeaderItem
import com.furianrt.mydiary.screens.main.listadapter.MainListItem
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import net.danlew.android.joda.DateUtils
import org.joda.time.DateTime
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.Comparator
import kotlin.collections.ArrayList

class MainActivityPresenter(
        private val dataManager: DataManager
) : MainActivityContract.Presenter() {

    private var mSelectedNoteIds = HashSet<String>()

    override fun onButtonDeleteClick() {
        view?.showDeleteConfirmationDialog(mSelectedNoteIds.toList())
    }

    override fun onButtonDeleteConfirmClick() {
        mSelectedNoteIds.clear()
        view?.deactivateSelection()
    }

    override fun onButtonFolderClick() {
        view?.showCategoriesView(mSelectedNoteIds.toList())
    }

    override fun onCategorySelected() {
        mSelectedNoteIds.clear()
        view?.updateItemSelection(mSelectedNoteIds)
        view?.deactivateSelection()
    }

    override fun onMenuAllNotesClick() {
        addDisposable(dataManager.getAllNotesWithProp()
                .first(ArrayList())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { notes ->
                    mSelectedNoteIds.clear()
                    mSelectedNoteIds.addAll(notes.map { it.note.id })
                    view?.activateSelection()
                    view?.updateItemSelection(mSelectedNoteIds)
                })
    }

    override fun attachView(view: MainActivityContract.View) {
        super.attachView(view)
        loadProfile()
        loadNotes()
        loadHeaderImage()
        updateSyncProgress()
        addDisposable(checkLogOut().subscribe())
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
        addDisposable(dataManager.observeAuthState()
                .filter { it == DataManager.SIGN_STATE_SIGN_OUT }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    addDisposable(dataManager.clearDbProfile()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe { view?.showAnonymousProfile() })
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
                                    .flatMapCompletable { dataManager.insertHeaderImage(it) }
                                    .andThen(dataManager.getHeaderImages().firstOrError())
                                    .map { it.first() }
                        dbImages.isNotEmpty() && view?.networkAvailable() == true ->
                            dataManager.loadHeaderImages()
                                    .onErrorReturn { dbImages }
                                    .map { list ->
                                        list.find { apiImage ->
                                            dbImages.find { it.id == apiImage.id } == null
                                        } ?: dbImages.first()
                                    }
                                    .flatMapCompletable { dataManager.insertHeaderImage(it) }
                                    .andThen(dataManager.getHeaderImages().firstOrError())
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
                .debounce(400, TimeUnit.MILLISECONDS)
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
                    view?.showNotes(items, mSelectedNoteIds)
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

    private fun updateSyncProgress() {
        addDisposable(Single.fromCallable { dataManager.getLastSyncMessage() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ message ->
                    if (message != null && message.taskIndex != SyncProgressMessage.SYNC_FINISHED) {
                        view?.showSyncProgress(message)
                    } else {
                        view?.clearSyncProgress()
                    }
                }, {
                    view?.clearSyncProgress()
                }))
    }

    override fun onFabMenuClick() {
        if (mSelectedNoteIds.isEmpty()) {
            view?.showViewNewNote()
        } else {
            mSelectedNoteIds.clear()
            view?.deactivateSelection()
            view?.updateItemSelection(mSelectedNoteIds)
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
        if (mSelectedNoteIds.isEmpty()) {
            openNotePagerView(note)
        } else {
            selectListItem(note.note.id)
        }
    }

    override fun onMainListItemLongClick(note: MyNoteWithProp, position: Int) {
        if (mSelectedNoteIds.isEmpty()) {
            view?.activateSelection()
        }
        selectListItem(note.note.id)
    }

    override fun onSaveInstanceState() = mSelectedNoteIds

    override fun onRestoreInstanceState(selectedNoteIds: Set<String>?) {
        selectedNoteIds?.let {
            mSelectedNoteIds.clear()
            mSelectedNoteIds.addAll(it)
        }
    }

    private fun selectListItem(noteId: String) {
        when {
            mSelectedNoteIds.contains(noteId) && mSelectedNoteIds.size == 1 -> {
                mSelectedNoteIds.remove(noteId)
                view?.deactivateSelection()
            }
            mSelectedNoteIds.contains(noteId) -> mSelectedNoteIds.remove(noteId)
            else -> mSelectedNoteIds.add(noteId)
        }
        view?.updateItemSelection(mSelectedNoteIds)
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

    private fun checkLogOut(): Completable =
            dataManager.getDbProfileCount()
                    .flatMapCompletable { count ->
                        if (dataManager.isSignedIn() && count == 0) {
                            dataManager.signOut()
                        } else if (count > 1) {
                            dataManager.signOut().andThen(dataManager.clearDbProfile())
                        } else {
                            Completable.complete()
                        }
                    }

    override fun onButtonProfileClick() {
        addDisposable(checkLogOut()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (dataManager.isSignedIn()) {
                        view?.showProfileSettings()
                    } else {
                        view?.showLoginView()
                    }
                }, {
                    it.printStackTrace()
                }))
    }
}