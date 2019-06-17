package com.furianrt.mydiary.screens.main

import android.os.Bundle
import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView
import com.furianrt.mydiary.data.model.*
import com.furianrt.mydiary.screens.main.adapter.NoteListItem

interface MainActivityContract {

    interface View : BaseView {
        fun showNotes(notes: List<NoteListItem>, selectedNoteIds: Set<String>)
        fun showNotePager(position: Int, note: MyNoteWithProp)
        fun showHeaderImage(image: MyHeaderImage)
        fun showViewNewNote()
        fun activateSelection()
        fun deactivateSelection()
        fun updateItemSelection(selectedNoteIds: Set<String>)
        fun showEmptyHeaderImage(hasError: Boolean)
        fun showSettingsView()
        fun networkAvailable(): Boolean
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
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onMainListItemClick(note: MyNoteWithProp, position: Int)
        abstract fun onMainImageClick()
        abstract fun onMainListItemLongClick(note: MyNoteWithProp, position: Int)
        abstract fun onSaveInstanceState(bundle: Bundle?)
        abstract fun onRestoreInstanceState(bundle: Bundle?)
        abstract fun onFabMenuClick()
        abstract fun onMenuAllNotesClick()
        abstract fun onButtonSettingsClick()
        abstract fun is24TimeFormat(): Boolean
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
        abstract fun onButtonChangeFiltersClick()
        abstract fun onDailyImageLoadStateChange()
    }
}