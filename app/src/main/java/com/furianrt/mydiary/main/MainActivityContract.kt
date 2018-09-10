package com.furianrt.mydiary.main

import com.furianrt.mydiary.BasePresenter
import com.furianrt.mydiary.BaseView
import com.furianrt.mydiary.data.model.MyNote
import com.furianrt.mydiary.main.listadapter.MainListItem
import java.io.File

interface MainActivityContract {

    interface View : BaseView {

        fun showAdded()

        fun showDeleted()

        fun showNotes(notes: List<MainListItem>?)

        fun openNotePager(position: Int)

        fun showHeaderImage(image: File)

        fun requestStoragePermissions()

        fun showViewNewNote()

        fun showImageExplorer()

        fun showContextualActionBar()
    }

    interface Presenter : BasePresenter<View> {

        fun addNote(note: MyNote)

        fun deleteNote(note: MyNote)

        fun onMainListItemClick(note: MyNote)

        fun onHeaderImagePicked(imagePath: String?)

        fun onViewCreate()

        fun onButtonSetMainImageClick()

        fun onButtonAddNoteClick()

        fun onStoragePermissionsGranted()

        fun onMainListItemLongClick(note: MyNote)
    }
}