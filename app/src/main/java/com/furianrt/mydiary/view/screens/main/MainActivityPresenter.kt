/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.screens.main

import android.annotation.SuppressLint
import android.os.Bundle
import com.furianrt.mydiary.data.model.*
import com.furianrt.mydiary.domain.FilterNotesUseCase
import com.furianrt.mydiary.domain.SwapNoteSortTypeUseCase
import com.furianrt.mydiary.domain.check.CheckLogOutUseCase
import com.furianrt.mydiary.domain.check.IsDailyImageEnabledUseCase
import com.furianrt.mydiary.domain.check.IsNeedRateOfferUseCase
import com.furianrt.mydiary.domain.delete.DeleteProfileUseCase
import com.furianrt.mydiary.domain.get.*
import com.furianrt.mydiary.view.screens.main.adapter.NoteListContent
import com.furianrt.mydiary.view.screens.main.adapter.NoteListHeader
import com.furianrt.mydiary.view.screens.main.adapter.NoteListItem
import com.furianrt.mydiary.utils.generateUniqueId
import io.reactivex.android.schedulers.AndroidSchedulers
import org.joda.time.DateTime
import org.joda.time.LocalDate
import java.util.TreeMap
import java.util.Locale
import javax.inject.Inject
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class MainActivityPresenter @Inject constructor(
        private val getFullNotes: GetFullNotesUseCase,
        private val getNotesSortType: GetNotesSortTypeUseCase,
        private val swapNoteSortType: SwapNoteSortTypeUseCase,
        private val getProfile: GetProfileUseCase,
        private val getTimeFormat: GetTimeFormatUseCase,
        private val isNeedRateOffer: IsNeedRateOfferUseCase,
        private val isDailyImageEnabled: IsDailyImageEnabledUseCase,
        private val getDailyImage: GetDailyImageUseCase,
        private val getAuthState: GetAuthStateUseCase,
        private val deleteProfile: DeleteProfileUseCase,
        private val checkLogOut: CheckLogOutUseCase,
        private val filterNotes: FilterNotesUseCase
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
        addDisposable(getFullNotes.invoke()
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
        addDisposable(checkLogOut.invoke().subscribe())
        if (mSelectedNoteIds.isNotEmpty()) {
            view.activateSelection()
        }
        showRateProposal()
    }

    private fun showRateProposal() {
        if (isNeedRateOffer.invoke()) {
            view?.showRateProposal()
        }
    }

    private fun loadHeaderImage() {
        if (!isDailyImageEnabled.invoke()) {
            view?.showEmptyHeaderImage(false)
            return
        }

        addDisposable(getDailyImage.invoke()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ image ->
                    view?.showHeaderImage(image)
                }, { error ->
                    error.printStackTrace()
                    view?.showEmptyHeaderImage(true)
                }))
    }

    private fun loadNotes() {
        when (getNotesSortType.invoke()) {
            GetNotesSortTypeUseCase.SORT_TYPE_ASC -> view?.setSortAsc()
            GetNotesSortTypeUseCase.SORT_TYPE_DESC -> view?.setSortDesc()
            else -> throw IllegalStateException()
        }
        addDisposable(getFullNotes.invoke()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { notes ->
                    mNoteList = notes
                    showNotes(mNoteList)
                })
    }

    private fun toMap(notes: List<MyNoteWithProp>): Map<Long, ArrayList<MyNoteWithProp>> {
        val map = TreeMap<Long, ArrayList<MyNoteWithProp>>(Comparator<Long> { p0, p1 ->
            when (getNotesSortType.invoke()) {
                GetNotesSortTypeUseCase.SORT_TYPE_ASC -> p0.compareTo(p1)
                GetNotesSortTypeUseCase.SORT_TYPE_DESC -> p1.compareTo(p0)
                else -> throw IllegalStateException()
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
            val values = when (getNotesSortType.invoke()) {
                GetNotesSortTypeUseCase.SORT_TYPE_ASC ->
                    notes.getValue(date).sortedBy { it.note.time }
                GetNotesSortTypeUseCase.SORT_TYPE_DESC ->
                    notes.getValue(date).sortedByDescending { it.note.time }
                else -> throw IllegalStateException()
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
            view?.showViewNewNote(generateUniqueId())
        } else {
            mSelectedNoteIds.clear()
            view?.deactivateSelection()
            view?.updateItemSelection(mSelectedNoteIds)
        }
    }

    override fun onButtonSortClick() {
        swapNoteSortType.invoke()
        when (getNotesSortType.invoke()) {
            GetNotesSortTypeUseCase.SORT_TYPE_ASC -> view?.setSortAsc()
            GetNotesSortTypeUseCase.SORT_TYPE_DESC -> view?.setSortDesc()
            else -> throw IllegalStateException()
        }
        showNotes(mNoteList)
    }

    override fun onMainImageClick() {
        view?.showImageOptions()
    }

    override fun onButtonImageSettingsClick() {
        view?.showViewImageSettings()
    }

    override fun onMainListItemClick(note: MyNoteWithProp, position: Int) {
        if (mSelectedNoteIds.isEmpty()) {
            view?.showNotePager(position, note.note.id)
        } else {
            selectListItem(note.note.id)
        }
    }

    override fun onMainListItemLongClick(note: MyNoteWithProp) {
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
        if (mSelectedNoteIds.contains(noteId)) {
            if ( mSelectedNoteIds.size == 1) {
                view?.deactivateSelection()
            }
            mSelectedNoteIds.remove(noteId)
        } else {
            mSelectedNoteIds.add(noteId)
        }
        view?.updateItemSelection(mSelectedNoteIds)
    }

    override fun onButtonSettingsClick() {
        view?.showSettingsView()
    }

    override fun is24TimeFormat(): Boolean =
            getTimeFormat.invoke() == GetTimeFormatUseCase.TIME_FORMAT_24

    private fun loadProfile() {
        addDisposable(getProfile.invoke()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { profile -> view?.showProfile(profile) })

        addDisposable(getAuthState.invoke()
                .filter { it == GetAuthStateUseCase.STATE_SIGN_OUT }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    addDisposable(deleteProfile.invoke()
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
            val filteredNotes = applySearchFilter(notes)
            view?.showNotes(formatNotes(toMap(filteredNotes)), mSelectedNoteIds)
            if (filteredNotes.isEmpty()) {
                view?.showNoSearchResults()
            } else {
                view?.hideNoSearchResults()
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private fun applySearchFilter(notes: List<MyNoteWithProp>): List<MyNoteWithProp> =
            filterNotes.invoke(notes, mFilteredTagIds, mFilteredCategoryIds, mFilteredMoodIds,
                    mFilteredLocationNames, mFilteredStartDate, mFilteredEndDate, mSearchQuery)

    override fun onButtonChangeFiltersClick() {
        view?.showChangeFilters()
    }
}