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
import com.furianrt.mydiary.presentation.base.BaseView
import com.furianrt.mydiary.presentation.base.BasePresenter
import com.furianrt.mydiary.model.entity.*

interface MainActivityContract {

    interface View : BaseView {
        fun showNotes(notes: List<MyNoteWithProp>, selectedNoteIds: Set<String>, scrollToTop: Boolean)
        fun showNotePager(position: Int, noteId: String)
        fun showHeaderImage(image: MyHeaderImage)
        fun showViewNewNote(noteId: String)
        fun showEmptyHeaderImage(hasError: Boolean)
        fun showSettingsView()
        fun setSortDesc()
        fun setSortAsc()
        fun showViewImageSettings()
        fun showImageOptions()
        fun showDeleteConfirmationDialog(noteIds: List<String>)
        fun showCategoriesView(noteIds: List<String>)
        fun showProfile(profile: MyProfile)
        fun showAnonymousProfile()
        fun showEmptyNoteList()
        fun showNoSearchResults()
        fun hideEmptyNoteList()
        fun hideNoSearchResults()
        fun showChangeFilters()
        fun showRateProposal()
        fun showStatisticsView()
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onMainListItemClick(note: MyNoteWithProp)
        abstract fun onMainImageClick()
        abstract fun onMainListItemLongClick(note: MyNoteWithProp)
        abstract fun onSaveInstanceState(bundle: Bundle?)
        abstract fun onRestoreInstanceState(bundle: Bundle?)
        abstract fun onFabMenuClick()
        abstract fun onMenuAllNotesClick()
        abstract fun onButtonSettingsClick()
        abstract fun onButtonSortClick()
        abstract fun onButtonImageSettingsClick()
        abstract fun onButtonDeleteClick()
        abstract fun onButtonDeleteConfirmClick()
        abstract fun onButtonFolderClick()
        abstract fun onCategorySelected()
        abstract fun onSearchQueryChange(query: String)
        abstract fun onTagFilterChange(tag: MyTag, checked: Boolean)
        abstract fun onCategoryFilterChange(category: MyCategory, checked: Boolean)
        abstract fun onLocationFilterChange(location: MyLocation, checked: Boolean)
        abstract fun onMoodFilterChange(mood: MyMood, checked: Boolean)
        abstract fun onClearFilters()
        abstract fun onNoTagsFilterChange(checked: Boolean)
        abstract fun onNoCategoryFilterChange(checked: Boolean)
        abstract fun onNoMoodFilterChange(checked: Boolean)
        abstract fun onNoLocationFilterChange(checked: Boolean)
        abstract fun onDateFilterChange(startDate: Long?, endDate: Long?)
        abstract fun onButtonChangeFiltersClick()
        abstract fun onDailyImageLoadStateChange()
        abstract fun onButtonStatisticsClick()
        abstract fun onUserReviewComplete()
    }
}