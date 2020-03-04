/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.note.fragments.mainnote

import com.furianrt.mydiary.presentation.base.BaseView
import com.furianrt.mydiary.presentation.base.BasePresenter
import com.furianrt.mydiary.model.entity.*
import com.furianrt.mydiary.model.entity.pojo.TagsAndAppearance

interface NoteFragmentContract {

    interface View : BaseView {
        fun showForecast(temp: String, iconUri: String)
        fun showTagsView(noteId: String)
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
        fun showImages(images: List<MyImage>, panorama: Boolean)
        fun showNoImages()
        fun showGalleryView(noteId: String, image: MyImage)
        fun showMoodsView(noteId: String)
        fun showCategoriesView(noteId: String)
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
        fun requestCameraPermissions()
        fun showCamera()
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onTagsFieldClick()
        abstract fun onLocationPermissionsGranted()
        abstract fun onButtonSelectPhotoClick()
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
        abstract fun onNoteTextChange(title: String, content: String, textSpans: List<MyTextSpan>)
        abstract fun onButtonUndoClick()
        abstract fun onButtonRedoClick()
        abstract fun onEditModeEnabled()
        abstract fun onEditModeDisabled()
        abstract fun onButtonMicClick()
        abstract fun onSpeechRecorded(curTitle: String, curContent: String, textSpans: List<MyTextSpan>, recordedText: String)
        abstract fun onButtonShareClick()
        abstract fun onLocationPermissionDenied()
        abstract fun onButtonTakePhotoClick()
        abstract fun onCameraPermissionsGranted()
        abstract fun onNewPhotoTaken(photoPath: String?)
    }
}