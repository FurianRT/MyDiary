/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.main

import android.os.Bundle
import com.furianrt.mydiary.analytics.MyAnalytics
import com.furianrt.mydiary.model.entity.*
import com.furianrt.mydiary.domain.FilterNotesUseCase
import com.furianrt.mydiary.domain.SwapNoteSortTypeUseCase
import com.furianrt.mydiary.domain.check.CheckLogOutUseCase
import com.furianrt.mydiary.domain.check.IsDailyImageEnabledUseCase
import com.furianrt.mydiary.domain.check.IsNeedRateOfferUseCase
import com.furianrt.mydiary.domain.delete.DeleteProfileUseCase
import com.furianrt.mydiary.domain.get.*
import com.furianrt.mydiary.domain.save.SetNeedRateOfferUseCase
import com.furianrt.mydiary.utils.MyRxUtils
import com.furianrt.mydiary.utils.disposeIfNot
import com.furianrt.mydiary.utils.generateUniqueId
import io.reactivex.rxjava3.disposables.Disposable
import org.joda.time.LocalDate
import java.lang.IllegalArgumentException
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class MainActivityPresenter @Inject constructor(
        private val getFullNotesUseCase: GetFullNotesUseCase,
        private val getNotesSortTypeUseCase: GetNotesSortTypeUseCase,
        private val swapNoteSortTypeUseCase: SwapNoteSortTypeUseCase,
        private val getProfileUseCase: GetProfileUseCase,
        private val isNeedRateOfferUseCase: IsNeedRateOfferUseCase,
        private val isDailyImageEnabledUseCase: IsDailyImageEnabledUseCase,
        private val getDailyImageUseCase: GetDailyImageUseCase,
        private val getAuthStateUseCase: GetAuthStateUseCase,
        private val deleteProfileUseCase: DeleteProfileUseCase,
        private val checkLogOutUseCase: CheckLogOutUseCase,
        private val filterNotesUseCase: FilterNotesUseCase,
        private val setNeedRateOfferUseCase: SetNeedRateOfferUseCase,
        private val analytics: MyAnalytics,
        private val scheduler: MyRxUtils.BaseSchedulerProvider
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

    private var mNoteList = listOf<MyNoteWithProp>()
    private var mSelectedNoteIds = HashSet<String>()
    private val mFilteredTagIds = HashSet<String>()
    private val mFilteredCategoryIds = HashSet<String>()
    private val mFilteredMoodIds = HashSet<Int>()
    private val mFilteredLocationNames = HashSet<String>()
    private var mFilteredStartDate: LocalDate? = null
    private var mFilteredEndDate: LocalDate? = null
    private var mSearchQuery = ""
    private var mFullNotesDisposable: Disposable? = null

    override fun onButtonDeleteClick() {
        view?.showDeleteConfirmationDialog(mSelectedNoteIds.toList())
    }

    override fun onButtonDeleteConfirmClick() {
        mSelectedNoteIds.clear()
        showNotes(mNoteList, false)
    }

    override fun onButtonFolderClick() {
        view?.showCategoriesView(mSelectedNoteIds.toList())
    }

    override fun onCategorySelected() {
        mSelectedNoteIds.clear()
        showNotes(mNoteList, false)
    }

    override fun onMenuAllNotesClick() {
        addDisposable(getFullNotesUseCase()
                .firstOrError()
                .observeOn(scheduler.ui())
                .subscribe { notes ->
                    if (notes.isNotEmpty()) {
                        mSelectedNoteIds.clear()
                        mSelectedNoteIds.addAll(notes.map { it.note.id })
                        showNotes(mNoteList, false)
                    }
                })
    }

    override fun attachView(view: MainActivityContract.View) {
        super.attachView(view)
        loadNotes()
        loadProfile()
        loadHeaderImage()
        addDisposable(checkLogOutUseCase().subscribe())
        showRateProposal()
    }

    override fun detachView() {
        super.detachView()
        mFullNotesDisposable?.disposeIfNot()
    }

    private fun showRateProposal() {
        if (isNeedRateOfferUseCase()) {
            view?.showRateProposal()
        }
    }

    private fun loadHeaderImage() {
        if (!isDailyImageEnabledUseCase()) {
            view?.showEmptyHeaderImage(false)
            return
        }
        addDisposable(getDailyImageUseCase()
                .observeOn(scheduler.ui())
                .subscribe({ image ->
                    view?.showHeaderImage(image)
                }, { error ->
                    error.printStackTrace()
                    analytics.logExceptionEvent(error)
                    view?.showEmptyHeaderImage(true)
                }))
    }

    private fun loadNotes() {
        when (getNotesSortTypeUseCase()) {
            GetNotesSortTypeUseCase.SORT_TYPE_ASC -> view?.setSortAsc()
            GetNotesSortTypeUseCase.SORT_TYPE_DESC -> view?.setSortDesc()
            else -> throw IllegalStateException()
        }
        mFullNotesDisposable?.disposeIfNot()
        mFullNotesDisposable = getFullNotesUseCase()
                .debounce(300L, TimeUnit.MILLISECONDS, scheduler.computation())
                .observeOn(scheduler.ui())
                .subscribe { notes ->
                    mNoteList = notes
                    showNotes(mNoteList, false)
                }
    }

    override fun onDailyImageLoadStateChange() {
        loadHeaderImage()
    }

    override fun onFabMenuClick() {
        if (mSelectedNoteIds.isEmpty()) {
            mFullNotesDisposable?.disposeIfNot()
            view?.showViewNewNote(generateUniqueId())
        } else {
            mSelectedNoteIds.clear()
            showNotes(mNoteList, false)
        }
    }

    override fun onButtonSortClick() {
        swapNoteSortTypeUseCase()
        when (getNotesSortTypeUseCase()) {
            GetNotesSortTypeUseCase.SORT_TYPE_ASC -> view?.setSortAsc()
            GetNotesSortTypeUseCase.SORT_TYPE_DESC -> view?.setSortDesc()
            else -> throw IllegalStateException()
        }
        showNotes(mNoteList, true)
    }

    override fun onMainImageClick() {
        view?.showImageOptions()
    }

    override fun onButtonImageSettingsClick() {
        view?.showViewImageSettings()
    }

    override fun onMainListItemClick(note: MyNoteWithProp) {
        if (mSelectedNoteIds.isEmpty()) {
            val position = when (getNotesSortTypeUseCase()) {
                GetNotesSortTypeUseCase.SORT_TYPE_ASC -> mNoteList
                        .sortedBy { it.note.time }
                        .indexOfFirst { it.note.id == note.note.id }
                GetNotesSortTypeUseCase.SORT_TYPE_DESC -> mNoteList
                        .sortedByDescending { it.note.time }
                        .indexOfFirst { it.note.id == note.note.id }
                else -> throw IllegalArgumentException()
            }
            if (position != -1) {
                view?.showNotePager(position, note.note.id)
            }
        } else {
            selectListItem(note.note.id)
        }
    }

    override fun onMainListItemLongClick(note: MyNoteWithProp) {
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
            mSelectedNoteIds.remove(noteId)
        } else {
            mSelectedNoteIds.add(noteId)
        }
        showNotes(mNoteList, false)
    }

    override fun onButtonSettingsClick() {
        view?.showSettingsView()
    }

    override fun onButtonStatisticsClick() {
        view?.showStatisticsView()
    }

    private fun loadProfile() {
        addDisposable(getProfileUseCase()
                .observeOn(scheduler.ui())
                .subscribe { result ->
                    if (result.isPresent) {
                        view?.showProfile(result.get())
                    }
                })

        addDisposable(getAuthStateUseCase()
                .filter { it == GetAuthStateUseCase.STATE_SIGN_OUT }
                .observeOn(scheduler.ui())
                .subscribe {
                    addDisposable(deleteProfileUseCase()
                            .observeOn(scheduler.ui())
                            .subscribe { view?.showAnonymousProfile() })
                })
    }

    override fun onSearchQueryChange(query: String) {
        mSearchQuery = query.toLowerCase(Locale.getDefault())
        showNotes(mNoteList, true)
    }

    override fun onTagFilterChange(tag: MyTag, checked: Boolean) {
        if (checked) {
            mFilteredTagIds.add(tag.id)
        } else {
            mFilteredTagIds.remove(tag.id)
        }
        showNotes(mNoteList, true)
    }

    override fun onCategoryFilterChange(category: MyCategory, checked: Boolean) {
        if (checked) {
            mFilteredCategoryIds.add(category.id)
        } else {
            mFilteredCategoryIds.remove(category.id)
        }
        showNotes(mNoteList, true)
    }

    override fun onLocationFilterChange(location: MyLocation, checked: Boolean) {
        if (checked) {
            mFilteredLocationNames.add(location.name)
        } else {
            mFilteredLocationNames.remove(location.name)
        }
        showNotes(mNoteList, true)
    }

    override fun onMoodFilterChange(mood: MyMood, checked: Boolean) {
        if (checked) {
            mFilteredMoodIds.add(mood.id)
        } else {
            mFilteredMoodIds.remove(mood.id)
        }
        showNotes(mNoteList, true)
    }

    override fun onNoTagsFilterChange(checked: Boolean) {
        if (checked) {
            mFilteredTagIds.add(MyTag.TABLE_NAME)
        } else {
            mFilteredTagIds.remove(MyTag.TABLE_NAME)
        }
        showNotes(mNoteList, true)
    }

    override fun onNoCategoryFilterChange(checked: Boolean) {
        if (checked) {
            mFilteredCategoryIds.add(MyCategory.TABLE_NAME)
        } else {
            mFilteredCategoryIds.remove(MyCategory.TABLE_NAME)
        }
        showNotes(mNoteList, true)
    }

    override fun onNoMoodFilterChange(checked: Boolean) {
        if (checked) {
            mFilteredMoodIds.add(-1)
        } else {
            mFilteredMoodIds.remove(-1)
        }
        showNotes(mNoteList, true)
    }

    override fun onNoLocationFilterChange(checked: Boolean) {
        if (checked) {
            mFilteredLocationNames.add(MyLocation.TABLE_NAME)
        } else {
            mFilteredLocationNames.remove(MyLocation.TABLE_NAME)
        }
        showNotes(mNoteList, true)
    }

    override fun onDateFilterChange(startDate: Long?, endDate: Long?) {
        mFilteredStartDate = if (startDate == null) {
            null
        } else {
            LocalDate(startDate)
        }

        mFilteredEndDate = if (endDate == null) {
            null
        } else {
            LocalDate(endDate)
        }
        showNotes(mNoteList, true)
    }

    override fun onClearFilters() {
        mFilteredTagIds.clear()
        mFilteredCategoryIds.clear()
        mFilteredMoodIds.clear()
        mFilteredLocationNames.clear()
        mFilteredStartDate = null
        mFilteredEndDate = null
        showNotes(mNoteList, true)
    }

    private fun showNotes(notes: List<MyNoteWithProp>, scrollToTop: Boolean) {
        val sortedNotes = when (getNotesSortTypeUseCase()) {
            GetNotesSortTypeUseCase.SORT_TYPE_ASC -> notes.sortedBy { it.note.time }
            GetNotesSortTypeUseCase.SORT_TYPE_DESC -> notes.sortedByDescending { it.note.time }
            else -> throw IllegalArgumentException()
        }
        if (sortedNotes.isEmpty()) {
            view?.showEmptyNoteList()
            view?.showNotes(sortedNotes, mSelectedNoteIds, scrollToTop)
        } else {
            view?.hideEmptyNoteList()
            val filteredNotes = applySearchFilter(sortedNotes)
            view?.showNotes(filteredNotes, mSelectedNoteIds, scrollToTop)
            if (filteredNotes.isEmpty()) {
                view?.showNoSearchResults()
            } else {
                view?.hideNoSearchResults()
            }
        }
    }

    private fun applySearchFilter(notes: List<MyNoteWithProp>): List<MyNoteWithProp> =
            filterNotesUseCase(notes, mFilteredTagIds, mFilteredCategoryIds, mFilteredMoodIds,
                    mFilteredLocationNames, mFilteredStartDate, mFilteredEndDate, mSearchQuery)

    override fun onButtonChangeFiltersClick() {
        view?.showChangeFilters()
    }

    override fun onUserReviewComplete() {
        setNeedRateOfferUseCase(true)
    }
}