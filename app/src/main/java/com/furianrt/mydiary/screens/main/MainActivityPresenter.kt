package com.furianrt.mydiary.screens.main

import android.annotation.SuppressLint
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
import org.joda.time.LocalDate
import java.util.TreeMap
import java.util.Locale
import javax.inject.Inject
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class MainActivityPresenter @Inject constructor(
        private val dataManager: DataManager
) : MainActivityContract.Presenter() {

    companion object {
        private const val BUNDLE_SELECTED_LIST_ITEMS = "selected_list_items"
        private const val BUNDLE_FILTERED_TAG_IDS = "filtered_tag_ids"
        private const val BUNDLE_FILTERED_CATEGORY_IDS = "filtered_category_ids"
        private const val BUNDLE_FILTERED_MOOD_IDS = "filtered_mood_ids"
        private const val BUNDLE_FILTERED_LOCATION_NAMES = "filtered_location_names"
        private const val BUNDLE_FILTERED_START_DATE = "filtered_start_date"
        private const val BUNDLE_FILTERED_END_DATE = "filtered_end_date"
        private const val BUNDLE_SEARCH_QUERY = "search_query"
        private const val NUMBER_OF_LAUNCHES_FOR_RATE = 4
    }

    private lateinit var mNoteList: List<MyNoteWithProp>
    private var mSelectedNoteIds = HashSet<String>()
    private val mFilteredTagIds = HashSet<String>()
    private val mFilteredCategoryIds = HashSet<String>()
    private val mFilteredMoodIds = HashSet<Int>()
    private val mFilteredLocationNames = HashSet<String>()
    private var mFilteredStartDate: LocalDate? = null
    private var mFilteredEndDate: LocalDate? = null
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
                    if (notes.isNotEmpty()) {
                        mSelectedNoteIds.clear()
                        mSelectedNoteIds.addAll(notes.map { it.note.id })
                        view?.activateSelection()
                        view?.updateItemSelection(mSelectedNoteIds)
                    }
                })
    }

    override fun attachView(view: MainActivityContract.MvpView) {
        super.attachView(view)
        loadNotes()
        loadProfile()
        loadHeaderImage()
        addDisposable(checkLogOut().subscribe())
        if (mSelectedNoteIds.isNotEmpty()) {
            view.activateSelection()
        }
        showRateProposal()
    }

    private fun showRateProposal() {
        if (dataManager.getNumberOfLaunches() == NUMBER_OF_LAUNCHES_FOR_RATE) {
            view?.showRateProposal()
        }
    }

    private fun loadHeaderImage() {
        if (!dataManager.isDailyImageEnabled()) {
            view?.showEmptyHeaderImage(false)
            return
        }

        addDisposable(dataManager.getHeaderImages()
                .first(emptyList())
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
                    view?.showEmptyHeaderImage(true)
                }))
    }

    private fun loadNotes() {
        if (dataManager.isSortDesc()) {
            view?.setSortAsc()
        } else {
            view?.setSortDesc()
        }
        addDisposable(dataManager.getAllNotesWithProp()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    mNoteList = it
                    showNotes(mNoteList)
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

    override fun onDailyImageLoadStateChange() {
        loadHeaderImage()
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
        bundle?.let { state ->
            state.putStringArrayList(BUNDLE_SELECTED_LIST_ITEMS, ArrayList(mSelectedNoteIds))
            state.putStringArrayList(BUNDLE_FILTERED_TAG_IDS, ArrayList(mFilteredTagIds))
            state.putStringArrayList(BUNDLE_FILTERED_CATEGORY_IDS, ArrayList(mFilteredCategoryIds))
            state.putIntegerArrayList(BUNDLE_FILTERED_MOOD_IDS, ArrayList(mFilteredMoodIds))
            state.putStringArrayList(BUNDLE_FILTERED_LOCATION_NAMES, ArrayList(mFilteredLocationNames))
            mFilteredStartDate?.let { state.putSerializable(BUNDLE_FILTERED_START_DATE, it) }
            mFilteredEndDate?.let { state.putSerializable(BUNDLE_FILTERED_END_DATE, it) }
            state.putString(BUNDLE_SEARCH_QUERY, mSearchQuery)
        }
    }

    override fun onRestoreInstanceState(bundle: Bundle?) {
        bundle?.let { state ->
            mSelectedNoteIds = state.getStringArrayList(BUNDLE_SELECTED_LIST_ITEMS)?.toHashSet()
                    ?: HashSet()

            mFilteredTagIds.clear()
            mFilteredTagIds.addAll(state.getStringArrayList(BUNDLE_FILTERED_TAG_IDS) ?: emptyList())

            mFilteredCategoryIds.clear()
            mFilteredCategoryIds.addAll(state.getStringArrayList(BUNDLE_FILTERED_CATEGORY_IDS)
                    ?: emptyList())

            mFilteredMoodIds.clear()
            mFilteredMoodIds.addAll(state.getIntegerArrayList(BUNDLE_FILTERED_MOOD_IDS)
                    ?: emptyList())

            mFilteredLocationNames.clear()
            mFilteredLocationNames.addAll(state.getStringArrayList(BUNDLE_FILTERED_LOCATION_NAMES)
                    ?: emptyList())

            mFilteredStartDate = state.getSerializable(BUNDLE_FILTERED_START_DATE) as LocalDate?
            mFilteredEndDate = state.getSerializable(BUNDLE_FILTERED_END_DATE) as LocalDate?

            mSearchQuery = state.getString(BUNDLE_SEARCH_QUERY, "")
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
        mSearchQuery = query.toLowerCase(Locale.getDefault())
        showNotes(mNoteList)
    }

    override fun onTagFilterChange(tag: MyTag, checked: Boolean) {
        if (checked) {
            mFilteredTagIds.add(tag.id)
        } else {
            mFilteredTagIds.remove(tag.id)
        }

        showNotes(mNoteList)
    }

    override fun onCategoryFilterChange(category: MyCategory, checked: Boolean) {
        if (checked) {
            mFilteredCategoryIds.add(category.id)
        } else {
            mFilteredCategoryIds.remove(category.id)
        }

        showNotes(mNoteList)
    }

    override fun onLocationFilterChange(location: MyLocation, checked: Boolean) {
        if (checked) {
            mFilteredLocationNames.add(location.name)
        } else {
            mFilteredLocationNames.remove(location.name)
        }

        showNotes(mNoteList)
    }

    override fun onMoodFilterChange(mood: MyMood, checked: Boolean) {
        if (checked) {
            mFilteredMoodIds.add(mood.id)
        } else {
            mFilteredMoodIds.remove(mood.id)
        }

        showNotes(mNoteList)
    }

    override fun onNoTagsFilterChange(checked: Boolean) {
        if (checked) {
            mFilteredTagIds.add(MyTag.TABLE_NAME)
        } else {
            mFilteredTagIds.remove(MyTag.TABLE_NAME)
        }

        showNotes(mNoteList)
    }

    override fun onNoCategoryFilterChange(checked: Boolean) {
        if (checked) {
            mFilteredCategoryIds.add(MyCategory.TABLE_NAME)
        } else {
            mFilteredCategoryIds.remove(MyCategory.TABLE_NAME)
        }

        showNotes(mNoteList)
    }

    override fun onNoMoodFilterChange(checked: Boolean) {
        if (checked) {
            mFilteredMoodIds.add(-1)
        } else {
            mFilteredMoodIds.remove(-1)
        }

        showNotes(mNoteList)
    }

    override fun onNoLocationFilterChange(checked: Boolean) {
        if (checked) {
            mFilteredLocationNames.add(MyLocation.TABLE_NAME)
        } else {
            mFilteredLocationNames.remove(MyLocation.TABLE_NAME)
        }

        showNotes(mNoteList)
    }

    override fun onDateFilterChange(startDate: Long?, endDate: Long?) {
        if (startDate == null) {
            mFilteredStartDate = null
        } else {
            mFilteredStartDate = LocalDate(startDate)
        }

        if (endDate == null) {
            mFilteredEndDate = null
        } else {
            mFilteredEndDate = LocalDate(endDate)
        }

        showNotes(mNoteList)
    }

    override fun onClearFilters() {
        mFilteredTagIds.clear()
        mFilteredCategoryIds.clear()
        mFilteredMoodIds.clear()
        mFilteredLocationNames.clear()
        mFilteredStartDate = null
        mFilteredEndDate = null

        showNotes(mNoteList)
    }

    private fun showNotes(notes: List<MyNoteWithProp>) {
        if (notes.isEmpty()) {
            view?.showEmptyNoteList()
            view?.showNotes(formatNotes(toMap(notes)), mSelectedNoteIds)
        } else {
            view?.hideEmptyNoteList()
            addDisposable(dataManager.getAllDbLocations()
                    .first(emptyList())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { locations ->
                        val filteredNotes = applySearchFilter(notes, locations)
                        view?.showNotes(formatNotes(toMap(filteredNotes)), mSelectedNoteIds)
                        if (filteredNotes.isEmpty()) {
                            view?.showNoSearchResults()
                        } else {
                            view?.hideNoSearchResults()
                        }
                    })
        }
    }

    @SuppressLint("DefaultLocale")
    private fun applySearchFilter(notes: List<MyNoteWithProp>, locations: List<MyLocation>): List<MyNoteWithProp> =
            notes.asSequence()
                    .filter {
                        it.note.title.toLowerCase().contains(mSearchQuery)
                                || it.note.content.toLowerCase().contains(mSearchQuery)
                    }
                    .filter { note ->
                        if (mFilteredTagIds.isEmpty()) {
                            return@filter true
                        }
                        if (note.tags.isEmpty()) {
                            return@filter mFilteredTagIds.contains(MyTag.TABLE_NAME)
                        }
                        val tagIds = mFilteredTagIds.filter { it != MyTag.TABLE_NAME }
                        if (tagIds.isEmpty()) {
                            return@filter false
                        }
                        tagIds.filter { it != MyTag.TABLE_NAME }
                                .forEach { tagId ->
                                    if (note.tags.find { it.tagId == tagId } == null) {
                                        return@filter false
                                    }
                                }
                        return@filter true
                    }
                    .filter { note ->
                        mFilteredCategoryIds.isEmpty()
                                || mFilteredCategoryIds.find { it == note.category?.id ?: MyCategory.TABLE_NAME } != null
                    }
                    .filter { note ->
                        mFilteredMoodIds.isEmpty()
                                || mFilteredMoodIds.find { it == note.mood?.id ?: -1 } != null
                    }
                    .filter { note ->
                        if (mFilteredLocationNames.isEmpty()) {
                            return@filter true
                        }
                        if (note.locations.isEmpty()) {
                            return@filter mFilteredLocationNames.contains(MyLocation.TABLE_NAME)
                        }
                        val locationNames = mFilteredLocationNames.filter { it != MyLocation.TABLE_NAME }
                        if (locationNames.isEmpty()) {
                            return@filter false
                        }
                        locationNames.filter { it != MyLocation.TABLE_NAME }
                                .forEach { locationName ->
                                    if (note.locations.find { location -> locations.find { it.id == location.locationId }?.name == locationName } == null) {
                                        return@filter false
                                    }
                                }
                        return@filter true
                    }
                    .filter { note ->
                        val startDate = mFilteredStartDate
                        val endDate = mFilteredEndDate
                        return@filter when {
                            startDate == null && endDate == null -> true
                            startDate != null && endDate == null -> LocalDate(note.note.time) == startDate
                            else -> {
                                val noteDate = LocalDate(note.note.time)
                                noteDate >= startDate && noteDate <= endDate
                            }
                        }
                    }
                    .toList()

    override fun onButtonChangeFiltersClick() {
        view?.showChangeFilters()
    }
}