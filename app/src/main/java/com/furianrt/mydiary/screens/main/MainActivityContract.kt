package com.furianrt.mydiary.screens.main

import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView
import com.furianrt.mydiary.data.model.MyHeaderImage
import com.furianrt.mydiary.data.model.MyNoteWithProp
import com.furianrt.mydiary.data.model.MyProfile
import com.furianrt.mydiary.screens.main.adapter.NoteListItem

interface MainActivityContract {

    interface View : BaseView {
        fun showNotes(notes: List<NoteListItem>, selectedNoteIds: Set<String>)
        fun showNotePager(position: Int, note: MyNoteWithProp)
        fun showHeaderImage(image: MyHeaderImage)
        fun requestStoragePermissions()
        fun showViewNewNote()
        fun showImageExplorer()
        fun activateSelection()
        fun deactivateSelection()
        fun updateItemSelection(selectedNoteIds: Set<String>)
        fun showEmptyHeaderImage()
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
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onMainListItemClick(note: MyNoteWithProp, position: Int)
        abstract fun onHeaderImagesPicked(imageUrls: List<String>)
        abstract fun onMainImageClick()
        abstract fun onStoragePermissionsGranted()
        abstract fun onMainListItemLongClick(note: MyNoteWithProp, position: Int)
        abstract fun onSaveInstanceState(): Set<String>
        abstract fun onRestoreInstanceState(selectedNoteIds: Set<String>?)
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
    }
}