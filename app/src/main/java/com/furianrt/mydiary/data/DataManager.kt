package com.furianrt.mydiary.data

import com.furianrt.mydiary.data.api.forecast.Forecast
import com.furianrt.mydiary.data.model.*
import io.reactivex.*

interface DataManager {
    fun insertNote(note: MyNote): Completable
    fun insertNote(notes: List<MyNote>): Completable
    fun insertNoteTag(noteTag: NoteTag): Completable
    fun insertNoteTag(noteTags: List<NoteTag>): Completable
    fun insertTag(tag: MyTag): Completable
    fun insertTag(tags: List<MyTag>): Completable
    fun insertImage(image: MyImage): Completable
    fun insertImages(images: List<MyImage>): Completable
    fun insertHeaderImage(headerImage: MyHeaderImage): Single<Long>
    fun insertCategory(category: MyCategory): Completable
    fun insertCategory(categories: List<MyCategory>): Completable
    fun insertAppearance(appearance: MyNoteAppearance): Completable
    fun insertAppearance(appearances: List<MyNoteAppearance>): Completable
    fun updateNote(note: MyNote): Completable
    fun updateNoteText(noteId: String, title: String, content: String): Completable
    fun updateTag(tag: MyTag): Completable
    fun updateImage(image: MyImage): Completable
    fun updateImage(images: List<MyImage>): Completable
    fun updateImageSync(images: List<MyImage>): Completable
    fun updateCategory(category: MyCategory): Completable
    fun updateAppearance(appearance: MyNoteAppearance): Completable
    fun updateDbProfile(profile: MyProfile): Completable
    fun updateNotesSync(notes: List<MyNote>): Completable
    fun updateAppearancesSync(appearances: List<MyNoteAppearance>): Completable
    fun updateCategoriesSync(categories: List<MyCategory>): Completable
    fun updateTagsSync(tags: List<MyTag>): Completable
    fun updateNoteTagsSync(noteTags: List<NoteTag>): Completable
    fun deleteTag(tag: MyTag): Completable
    fun deleteNote(note: MyNote): Completable
    fun deleteImage(image: MyImage): Completable
    fun deleteImage(images: List<MyImage>): Completable
    fun deleteAllTagsForNote(noteId: String): Completable
    fun deleteImageFromStorage(fileName: String): Single<Boolean>
    fun deleteCategory(category: MyCategory): Completable
    fun deleteProfile(): Completable
    fun deleteNotesFromCloud(notes: List<MyNote>): Completable
    fun deleteCategoriesFromCloud(categories: List<MyCategory>): Completable
    fun deleteNoteTagsFromCloud(noteTags: List<NoteTag>): Completable
    fun deleteTagsFromCloud(tags: List<MyTag>): Completable
    fun deleteAppearancesFromCloud(appearances: List<MyNoteAppearance>): Completable
    fun deleteImagesFromCloud(images: List<MyImage>): Completable
    fun cleanupNotes(): Completable
    fun cleanupNoteTags(): Completable
    fun cleanupAppearances(): Completable
    fun cleanupCategories(): Completable
    fun cleanupTags(): Completable
    fun cleanupImages(): Completable
    fun getAllNotes(): Flowable<List<MyNote>>
    fun getDeletedNotes(): Flowable<List<MyNote>>
    fun getTagsForNote(noteId: String): Flowable<List<MyTag>>
    fun getDeletedNoteTags(): Flowable<List<NoteTag>>
    fun getNote(noteId: String): Flowable<MyNote>
    fun getAllTags(): Single<List<MyTag>>
    fun getDeletedTags(): Flowable<List<MyTag>>
    fun getForecast(lat: Double, lon: Double): Single<Forecast?>
    fun getMood(moodId: Int): Single<MyMood>
    fun getCategory(categoryId: String): Single<MyCategory>
    fun getAllCategories(): Flowable<List<MyCategory>>
    fun getDeletedCategories(): Flowable<List<MyCategory>>
    fun getAllMoods(): Single<List<MyMood>>
    fun getAllImages(): Flowable<List<MyImage>>
    fun getDeletedImages(): Flowable<List<MyImage>>
    fun getNoteAppearance(noteId: String): Flowable<MyNoteAppearance>
    fun getAllNoteAppearances(): Flowable<List<MyNoteAppearance>>
    fun getDeletedAppearances(): Flowable<List<MyNoteAppearance>>
    fun getImagesForNote(noteId: String): Flowable<List<MyImage>>
    fun getAllNotesWithProp(): Flowable<List<MyNoteWithProp>>
    fun getHeaderImages(): Flowable<List<MyHeaderImage>>
    fun getAllNoteTags(): Flowable<List<NoteTag>>
    fun getDbProfile(): Observable<MyProfile>
    fun getCloudProfile(email: String): Single<MyProfile>
    fun getTextColor(): Int
    fun getTextSize(): Int
    fun getNoteBackgroundColor(): Int
    fun getNoteTextBackgroundColor(): Int
    fun getAllNotesFromCloud(): Single<List<MyNote>>
    fun getAllCategoriesFromCloud(): Single<List<MyCategory>>
    fun getAllTagsFromCloud(): Single<List<MyTag>>
    fun getAllAppearancesFromCloud(): Single<List<MyNoteAppearance>>
    fun getAllNoteTagsFromCloud(): Single<List<NoteTag>>
    fun getAllImagesFromCloud(): Single<List<MyImage>>
    fun is24TimeFormat(): Boolean
    fun isSortDesc(): Boolean
    fun isWeatherEnabled(): Boolean
    fun isLocationEnabled(): Boolean
    fun isMoodEnabled(): Boolean
    fun isProfileExists(email: String): Single<Boolean>
    fun setSortDesc(desc: Boolean)
    fun replaceNoteTags(noteId: String, tags: List<MyTag>): Completable
    fun findNote(noteId: String): Maybe<MyNote>
    fun addLocation(location: MyLocation): Completable
    fun saveProfile(profile: MyProfile): Completable
    fun loadHeaderImages(page: Int = 1, perPage: Int = 20): Single<List<MyHeaderImage>>
    fun saveImageToStorage(image: MyImage): Single<MyImage>
    fun newProfile(profile: MyProfile): Completable
    fun saveNotesInCloud(notes: List<MyNote>): Completable
    fun saveCategoriesInCloud(categories: List<MyCategory>): Completable
    fun saveTagsInCloud(tags: List<MyTag>): Completable
    fun saveNoteTagsInCloud(noteTags: List<NoteTag>): Completable
    fun saveAppearancesInCloud(appearances: List<MyNoteAppearance>): Completable
    fun saveImagesInCloud(images: List<MyImage>): Completable
    fun saveImagesFilesInCloud(images: List<MyImage>): Completable
    fun loadImageFiles(images: List<MyImage>): Completable
}