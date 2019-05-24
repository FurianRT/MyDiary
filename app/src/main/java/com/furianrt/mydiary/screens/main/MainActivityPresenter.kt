package com.furianrt.mydiary.screens.main

import android.os.Bundle
import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.*
import com.furianrt.mydiary.screens.main.adapter.NoteListContent
import com.furianrt.mydiary.screens.main.adapter.NoteListHeader
import com.furianrt.mydiary.screens.main.adapter.NoteListItem
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import net.danlew.android.joda.DateUtils
import org.joda.time.DateTime
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class MainActivityPresenter(
        private val dataManager: DataManager
) : MainActivityContract.Presenter() {

    companion object {
        private const val BUNDLE_SELECTED_LIST_ITEMS = "selected_list_items"
        private const val BUNDLE_FILTERED_TAG_IDS = "filtered_tag_ids"
        private const val BUNDLE_FILTERED_CATEGORY_IDS = "filtered_category_ids"
        private const val BUNDLE_FILTERED_MOOD_IDS = "filtered_mood_ids"
        private const val BUNDLE_FILTERED_LOCATION_IDS = "filtered_location_ids"
        private const val BUNDLE_SEARCH_QUERY = "search_query"
    }

    private lateinit var mNoteList: List<MyNoteWithProp>
    private var mSelectedNoteIds = HashSet<String>()
    private val mFilteredTagIds = HashSet<String>()
    private val mFilteredCategoryIds = HashSet<String>()
    private val mFilteredMoodIds = HashSet<Int>()
    private val mFilteredLocationIds = HashSet<String>()
    private var mSearchQuery = ""

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
        loadNotes()
        loadProfile()
        loadHeaderImage()
        addDisposable(checkLogOut().subscribe())
        if (mSelectedNoteIds.isNotEmpty()) {
            view.activateSelection()
        }
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
                .map {
                    mNoteList = it
                    applySearchFilter(it)
                }
                .map { formatNotes(toMap(it)) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    view?.showNotes(it, mSelectedNoteIds)
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

    private fun formatNotes(notes: Map<Long, List<MyNoteWithProp>>): ArrayList<NoteListItem> {
        val list = ArrayList<NoteListItem>()
        for (date in notes.keys) {
            val header = NoteListHeader(date)
            list.add(header)
            val values = if (dataManager.isSortDesc()) {
                notes.getValue(date).sortedByDescending { it.note.time }
            } else {
                notes.getValue(date).sortedBy { it.note.time }
            }
            for (note in values) {
                list.add(NoteListContent(note))
            }
        }
        return list
    }

    private fun applySearchFilter(notes: List<MyNoteWithProp>): List<MyNoteWithProp> {
        return notes.asSequence()
                .filter {
                    it.note.title.toLowerCase(Locale.getDefault()).contains(mSearchQuery.toLowerCase(Locale.getDefault()))
                            || it.note.content.toLowerCase(Locale.getDefault()).contains(mSearchQuery.toLowerCase(Locale.getDefault()))
                }
                .filter { note ->
                    if (mFilteredTagIds.isNotEmpty()) {
                        mFilteredTagIds.forEach { tagId ->
                            if (note.tags.find { it.tagId == tagId } == null) {
                                return@filter false
                            }
                        }
                    }
                    return@filter true
                }
                .filter { note ->
                    mFilteredCategoryIds.isEmpty()
                            || mFilteredCategoryIds.find { it == note.category?.id } != null
                }
                .filter { note ->
                    mFilteredMoodIds.isEmpty()
                            || mFilteredMoodIds.find { it == note.mood?.id } != null
                }
                .filter { note ->
                    mFilteredLocationIds.isEmpty()
                            || mFilteredLocationIds.find { it == note.note.id } != null
                }.toList()
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

    override fun onSaveInstanceState(bundle: Bundle?) {
        bundle?.let {
            it.putStringArrayList(BUNDLE_SELECTED_LIST_ITEMS, ArrayList(mSelectedNoteIds))
            it.putStringArrayList(BUNDLE_FILTERED_TAG_IDS, ArrayList(mFilteredTagIds))
            it.putStringArrayList(BUNDLE_FILTERED_CATEGORY_IDS, ArrayList(mFilteredCategoryIds))
            it.putIntegerArrayList(BUNDLE_FILTERED_MOOD_IDS, ArrayList(mFilteredMoodIds))
            it.putStringArrayList(BUNDLE_FILTERED_LOCATION_IDS, ArrayList(mFilteredLocationIds))
            it.putString(BUNDLE_SEARCH_QUERY, mSearchQuery)
        }
    }

    override fun onRestoreInstanceState(bundle: Bundle?) {
        bundle?.let {
            mSelectedNoteIds = it.getStringArrayList(BUNDLE_SELECTED_LIST_ITEMS)?.toHashSet()
                    ?: HashSet()

            mFilteredTagIds.clear()
            mFilteredTagIds.addAll(it.getStringArrayList(BUNDLE_FILTERED_TAG_IDS) ?: emptyList())

            mFilteredCategoryIds.clear()
            mFilteredCategoryIds.addAll(it.getStringArrayList(BUNDLE_FILTERED_CATEGORY_IDS)
                    ?: emptyList())

            mFilteredMoodIds.clear()
            mFilteredMoodIds.addAll(it.getIntegerArrayList(BUNDLE_FILTERED_MOOD_IDS) ?: emptyList())

            mFilteredLocationIds.clear()
            mFilteredLocationIds.addAll(it.getStringArrayList(BUNDLE_FILTERED_LOCATION_IDS)
                    ?: emptyList())

            mSearchQuery = it.getString(BUNDLE_SEARCH_QUERY, "")
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
                .subscribe { notes -> view?.showNotePager(notes.indexOf(note), note) })
    }

    override fun onButtonSettingsClick() {
        view?.showSettingsView()
    }

    override fun is24TimeFormat(): Boolean = dataManager.is24TimeFormat()


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

    private fun loadProfile() {
        addDisposable(dataManager.getDbProfile()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { profile -> view?.showProfile(profile) })

        addDisposable(dataManager.observeAuthState()
                .filter { it == DataManager.SIGN_STATE_SIGN_OUT }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    addDisposable(dataManager.clearDbProfile()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe { view?.showAnonymousProfile() })
                })
    }

    override fun onSearchQueryChange(query: String) {
        mSearchQuery = query
        view?.showNotes(formatNotes(toMap(applySearchFilter(mNoteList))), mSelectedNoteIds)
    }

    override fun onTagFilterChange(tag: MyTag, checked: Boolean) {
        if (checked) {
            mFilteredTagIds.add(tag.id)
        } else {
            mFilteredTagIds.remove(tag.id)
        }
        view?.showNotes(formatNotes(toMap(applySearchFilter(mNoteList))), mSelectedNoteIds)
    }

    override fun onCategoryFilterChange(category: MyCategory, checked: Boolean) {
        if (checked) {
            mFilteredCategoryIds.add(category.id)
        } else {
            mFilteredCategoryIds.remove(category.id)
        }
        view?.showNotes(formatNotes(toMap(applySearchFilter(mNoteList))), mSelectedNoteIds)
    }

    override fun onLocationFilterChange(location: MyLocation, checked: Boolean) {
        if (checked) {
            mFilteredLocationIds.add(location.noteId)
        } else {
            mFilteredLocationIds.remove(location.noteId)
        }
        view?.showNotes(formatNotes(toMap(applySearchFilter(mNoteList))), mSelectedNoteIds)
    }

    override fun onMoodFilterChange(mood: MyMood, checked: Boolean) {
        if (checked) {
            mFilteredMoodIds.add(mood.id)
        } else {
            mFilteredMoodIds.remove(mood.id)
        }
        view?.showNotes(formatNotes(toMap(applySearchFilter(mNoteList))), mSelectedNoteIds)
    }

    override fun onClearFilters() {
        mFilteredTagIds.clear()
        mFilteredCategoryIds.clear()
        mFilteredMoodIds.clear()
        mFilteredLocationIds.clear()
        view?.showNotes(formatNotes(toMap(applySearchFilter(mNoteList))), mSelectedNoteIds)
    }
}