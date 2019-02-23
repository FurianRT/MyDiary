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
    fun updateImages(images: List<MyImage>): Completable
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
    fun deleteNotes(notes: List<MyNote>): Completable
    fun deleteImages(images: List<MyImage>): Completable
    fun deleteNoteTag(noteTag: NoteTag): Completable
    fun deleteAllTagsForNote(noteId: String): Completable
    fun deleteImageFromStorage(fileName: String): Single<Boolean>
    fun deleteCategory(category: MyCategory): Completable
    fun deleteProfile(): Completable
    fun cleanupNotes(): Completable
    fun cleanupNoteTags(): Completable
    fun cleanupAppearances(): Completable
    fun cleanupCategories(): Completable
    fun cleanupTags(): Completable
    fun isWeatherEnabled(): Boolean
    fun isLocationEnabled(): Boolean
    fun isMoodEnabled(): Boolean
    fun isProfileExists(email: String): Single<Boolean>
    fun getAllNotes(): Flowable<List<MyNote>>
    fun getDeletedNotes(): Flowable<List<MyNote>>
    fun getTagsForNote(noteId: String): Flowable<List<MyTag>>
    fun getDeletedNoteTags(): Flowable<List<NoteTag>>
    fun getNote(noteId: String): Flowable<MyNote>
    fun getAllTags(): Single<List<MyTag>>
    fun getDeletedTags(): Flowable<List<MyTag>>
    fun getForecast(lat: Double, lon: Double): Single<Forecast?>
    fun getMood(moodId: Int): Single<MyMood>
    fun getCategory(categoryId: String): Maybe<MyCategory>
    fun getAllCategories(): Flowable<List<MyCategory>>
    fun getDeletedCategories(): Flowable<List<MyCategory>>
    fun getAllMoods(): Single<List<MyMood>>
    fun getNoteAppearance(noteId: String): Flowable<MyNoteAppearance>
    fun getAllNoteAppearances(): Flowable<List<MyNoteAppearance>>
    fun getDeletedAppearances(): Flowable<List<MyNoteAppearance>>
    fun getImagesForNote(noteId: String): Flowable<List<MyImage>>
    fun getAllNotesWithProp(): Flowable<List<MyNoteWithProp>>
    fun getHeaderImages(): Flowable<List<MyHeaderImage>>
    fun getAllNoteTags(): Flowable<List<NoteTag>>
    fun getDbProfile(): Observable<MyProfile>
    fun getCloudProfile(email: String): Maybe<MyProfile>
    fun getTextColor(): Int
    fun getTextSize(): Int
    fun getNoteBackgroundColor(): Int
    fun getNoteTextBackgroundColor(): Int
    fun is24TimeFormat(): Boolean
    fun isSortDesc(): Boolean
    fun setSortDesc(desc: Boolean)
    fun replaceNoteTags(noteId: String, tags: List<MyTag>): Completable
    fun findNote(noteId: String): Maybe<MyNote>
    fun saveImageToStorage(image: MyImage): Single<MyImage>
    fun saveProfile(profile: MyProfile): Completable
    fun addLocation(location: MyLocation): Completable
    fun createProfile(profile: MyProfile): Completable
    fun loadHeaderImages(page: Int = 1, perPage: Int = 20): Single<List<MyHeaderImage>>
    fun saveNotesInCloud(notes: List<MyNote>): Completable
    fun saveCategoriesInCloud(categories: List<MyCategory>): Completable
    fun saveTagsInCloud(tags: List<MyTag>): Completable
    fun saveNoteTagsInCloud(noteTags: List<NoteTag>): Completable
    fun saveAppearancesInCloud(appearances: List<MyNoteAppearance>): Completable
    fun deleteNotesFromCloud(notes: List<MyNote>): Completable
    fun deleteCategoriesFromCloud(categories: List<MyCategory>): Completable
    fun deleteNoteTagsFromCloud(noteTags: List<NoteTag>): Completable
    fun deleteTagsFromCloud(tags: List<MyTag>): Completable
    fun deleteAppearancesFromCloud(appearances: List<MyNoteAppearance>): Completable
    fun getAllNotesFromCloud(): Single<List<MyNote>>
    fun getAllCategoriesFromCloud(): Single<List<MyCategory>>
    fun getAllTagsFromCloud(): Single<List<MyTag>>
    fun getAllAppearancesFromCloud(): Single<List<MyNoteAppearance>>
    fun getAllNoteTagsFromCloud(): Single<List<NoteTag>>
}