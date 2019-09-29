/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.screens.note.fragments.mainnote

import com.furianrt.mydiary.view.base.mvp.BaseMvpView
import com.furianrt.mydiary.view.base.mvp.BaseMvpPresenter
import com.furianrt.mydiary.data.model.*
import com.furianrt.mydiary.data.model.pojo.TagsAndAppearance
import java.util.*

interface NoteFragmentContract {

    interface MvpView : BaseMvpView {
        fun showForecast(temp: String, iconUri: String)
        fun showTagsDialog(noteId: String)
        fun showTags(tagsAndAppearance: TagsAndAppearance)
        fun requestLocationPermissions()
        fun showCategory(category: MyCategory)
        fun showMood(mood: MyMood)
        fun showNoTagsMessage(tagsAndAppearance: TagsAndAppearance)
        fun showNoCategoryMessage()
        fun showNoMoodMessage()
        fun showLocation(location: MyLocation)
        fun requestStoragePermissions()
        fun showImageExplorer()
        fun showImages(images: List<MyImage>)
        fun showNoImages()
        fun showGalleryView(noteId: String, image: MyImage)
        fun showMoodsDialog(noteId: String)
        fun showCategoriesDialog(noteId: String)
        fun showNoteSettingsView(noteId: String)
        fun updateNoteAppearance(appearance: MyNoteAppearance)
        fun showNoteText(title: String, content: String, textSpans: List<MyTextSpan>)
        fun showDatePicker(year: Int, monthOfYear: Int, dayOfMonth: Int)
        fun showTimePicker(hourOfDay: Int, minute: Int, is24HourMode: Boolean)
        fun showDateAndTime(time: Long, is24TimeFormat: Boolean)
        fun shoNoteEditView()
        fun showLoading()
        fun hideLoading()
        fun showDeleteConfirmationDialog(noteId: String)
        fun enableRedoButton(enable: Boolean)
        fun enableUndoButton(enable: Boolean)
        fun recordSpeech()
        fun sendUndoErrorEvent()
        fun sendRedoErrorEvent()
        fun shareNote(note: MyNoteWithProp)
        fun showErrorSaveImage()
        fun showErrorForecast()
        fun showRichTextOptions()
        fun hideRichTextOptions()
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun onTagsFieldClick()
        abstract fun onLocationPermissionsGranted()
        abstract fun onButtonAddImageClick()
        abstract fun onStoragePermissionsGranted()
        abstract fun onNoteImagesPicked(imageUrls: List<String>)
        abstract fun onButtonDeleteClick()
        abstract fun onToolbarImageClick(image: MyImage)
        abstract fun onMoodFieldClick()
        abstract fun onCategoryFieldClick()
        abstract fun onButtonAppearanceClick()
        abstract fun onDateFieldClick()
        abstract fun onTimeFieldClick()
        abstract fun onDateSelected(year: Int, monthOfYear: Int, dayOfMonth: Int)
        abstract fun onTimeSelected(hourOfDay: Int, minute: Int)
        abstract fun onButtonEditClick()
        abstract fun init(noteId: String, newNote: Boolean)
        abstract fun getNoteTextBuffer(): ArrayList<UndoRedoEntry>
        abstract fun setNoteTextBuffer(buffer: ArrayList<UndoRedoEntry>)
        abstract fun onNoteTextChange(title: String, content: String, textSpans: List<MyTextSpan>)
        abstract fun onButtonUndoClick()
        abstract fun onButtonRedoClick()
        abstract fun onEditModeEnabled()
        abstract fun onEditModeDisabled(noteTitle: String, noteContent: String, textSpans: List<MyTextSpan>)
        abstract fun onButtonMicClick()
        abstract fun onSpeechRecorded(curTitle: String, curContent: String, textSpans: List<MyTextSpan>, recordedText: String)
        abstract fun onButtonShareClick()
    }
}