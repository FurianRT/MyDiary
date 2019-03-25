package com.furianrt.mydiary.main

import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView
import com.furianrt.mydiary.data.model.MyHeaderImage
import com.furianrt.mydiary.data.model.MyNoteWithProp
import com.furianrt.mydiary.data.model.MyProfile
import com.furianrt.mydiary.main.listadapter.MainListItem

interface MainActivityContract {

    interface View : BaseView {
        fun showNotes(notes: List<MainListItem>, selectedNotes: ArrayList<MyNoteWithProp>, hasPremium: Boolean)
        fun openNotePager(position: Int, note: MyNoteWithProp)
        fun showHeaderImage(image: MyHeaderImage)
        fun requestStoragePermissions()
        fun showViewNewNote()
        fun showImageExplorer()
        fun activateSelection()
        fun deactivateSelection()
        fun updateItemSelection(selectedNotes: ArrayList<MyNoteWithProp>)
        fun showEmptyHeaderImage()
        fun showSettingsView()
        fun showPremiumView()
        fun showLoginView()
        fun networkAvailable(): Boolean
        fun showAnonymousProfile()
        fun showPremiumProfile(profile: MyProfile)
        fun showRegularProfile(profile: MyProfile)
        fun showProfileSettings()
        fun showNotesCountToday(count: Int)
        fun showImageCount(count: Int)
        fun showNotesTotal(count: Int)
        fun setSortDesc()
        fun setSortAsc()
        fun showViewImageSettings()
        fun showImageOptions()
        fun startSyncService()
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onMainListItemClick(note: MyNoteWithProp, position: Int)
        abstract fun onHeaderImagesPicked(imageUrls: List<String>)
        abstract fun onViewResume()
        abstract fun onMainImageClick()
        abstract fun onStoragePermissionsGranted()
        abstract fun onMainListItemLongClick(note: MyNoteWithProp, position: Int)
        abstract fun onSaveInstanceState(): ArrayList<MyNoteWithProp>?
        abstract fun onRestoreInstanceState(selectedNotes: ArrayList<MyNoteWithProp>?)
        abstract fun onFabMenuClick()
        abstract fun onButtonDeleteClick()
        abstract fun onMenuAllNotesClick()
        abstract fun onButtonSettingsClick()
        abstract fun is24TimeFormat(): Boolean
        abstract fun onButtonSyncClick()
        abstract fun onButtonProfileClick()
        abstract fun onSignOut()
        abstract fun onButtonSortClick()
        abstract fun onButtonImageSettingsClick()
    }
}