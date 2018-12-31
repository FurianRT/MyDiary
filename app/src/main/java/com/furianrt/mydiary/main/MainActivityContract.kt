package com.furianrt.mydiary.main

import com.furianrt.mydiary.BasePresenter
import com.furianrt.mydiary.BaseView
import com.furianrt.mydiary.data.model.MyHeaderImage
import com.furianrt.mydiary.data.model.MyNoteWithProp
import com.furianrt.mydiary.main.listadapter.MainListItem

interface MainActivityContract {

    interface View : BaseView {

        fun showAdded()

        fun showDeleted()

        fun showNotes(notes: List<MainListItem>, selectedNotes: ArrayList<MyNoteWithProp>)

        fun openNotePager(position: Int)

        fun showHeaderImages(images: List<MyHeaderImage>)

        fun requestStoragePermissions()

        fun showViewNewNote()

        fun showImageExplorer()

        fun activateSelection()

        fun deactivateSelection()

        fun updateItemSelection(selectedNotes: ArrayList<MyNoteWithProp>)

        fun showEmptyHeaderImage()

        fun showSettingsView()
    }

    abstract class Presenter : BasePresenter<View>() {

        abstract fun onMainListItemClick(note: MyNoteWithProp, position: Int)

        abstract fun onHeaderImagesPicked(imageUrls: List<String>)

        abstract fun onViewStart()

        abstract fun onButtonSetMainImageClick()

        abstract fun onStoragePermissionsGranted()

        abstract fun onMainListItemLongClick(note: MyNoteWithProp, position: Int)

        abstract fun onSaveInstanceState(): ArrayList<MyNoteWithProp>?

        abstract fun onRestoreInstanceState(selectedNotes: ArrayList<MyNoteWithProp>?)

        abstract fun onFabMenuClick()

        abstract fun onMenuDeleteClick()

        abstract fun onMenuAllNotesClick()

        abstract fun onButtonSettingsClick()
    }
}