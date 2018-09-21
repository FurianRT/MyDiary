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

        fun refreshTags()

        fun showEmptyHeaderImage()
    }

    interface Presenter : BasePresenter<View> {

        fun onMainListItemClick(note: MyNoteWithProp, position: Int)

        fun onHeaderImagesPicked(imageUrls: List<String>)

        fun onViewCreate()

        fun onButtonSetMainImageClick()

        fun onStoragePermissionsGranted()

        fun onMainListItemLongClick(note: MyNoteWithProp, position: Int)

        fun onSaveInstanceState(): ArrayList<MyNoteWithProp>?

        fun onRestoreInstanceState(selectedNotes: ArrayList<MyNoteWithProp>?)

        fun onFabMenuClick()

        fun onMenuDeleteClick()

        fun onMenuAllNotesClick()

        fun onNotePagerViewFinished()
    }
}