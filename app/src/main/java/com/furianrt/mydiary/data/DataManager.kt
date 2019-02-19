package com.furianrt.mydiary.data

import com.furianrt.mydiary.data.api.forecast.Forecast
import com.furianrt.mydiary.data.api.images.ImageResponse
import com.furianrt.mydiary.data.model.*
import io.reactivex.*

interface DataManager {

    fun insertNote(note: MyNote): Completable
    fun insertNoteTag(noteTag: NoteTag): Completable
    fun insertTag(tag: MyTag): Completable
    fun insertImage(image: MyImage): Completable
    fun insertImages(images: List<MyImage>): Completable
    fun insertHeaderImage(headerImage: MyHeaderImage): Single<Long>
    fun insertCategory(category: MyCategory): Single<Long>
    fun insertAppearance(appearance: MyNoteAppearance): Completable
    fun updateNote(note: MyNote): Completable
    fun updateNoteText(noteId: String, title: String, content: String): Completable
    fun updateTag(tag: MyTag): Completable
    fun updateImages(images: List<MyImage>): Completable
    fun updateCategory(category: MyCategory): Completable
    fun updateAppearance(appearance: MyNoteAppearance): Completable
    fun updateDbProfile(profile: MyProfile): Completable
    fun deleteTag(tag: MyTag): Completable
    fun deleteNote(note: MyNote): Completable
    fun deleteNotes(notes: List<MyNote>): Completable
    fun deleteImages(images: List<MyImage>): Completable
    fun deleteNoteTag(noteTag: NoteTag): Completable
    fun deleteAllTagsForNote(noteId: String): Completable
    fun deleteImageFromStorage(fileName: String): Single<Boolean>
    fun deleteCategory(category: MyCategory): Completable
    fun deleteProfile(): Completable
    fun isWeatherEnabled(): Boolean
    fun isLocationEnabled(): Boolean
    fun isMoodEnabled(): Boolean
    fun isProfileExists(email: String): Single<Boolean>
    fun getAllNotes(): Flowable<List<MyNote>>
    fun getTagsForNote(noteId: String): Flowable<List<MyTag>>
    fun getNotesWithTag(tagId: Long): Flowable<List<MyNote>>
    fun getNoteWithProp(noteId: String): Flowable<MyNoteWithProp>
    fun getNote(noteId: String): Flowable<MyNote>
    fun getAllTags(): Single<List<MyTag>>
    fun getForecast(lat: Double, lon: Double): Single<Forecast?>
    fun getMood(moodId: Int): Single<MyMood>
    fun getCategory(categoryId: Long): Maybe<MyCategory>
    fun getAllCategories(): Flowable<List<MyCategory>>
    fun getAllMoods(): Single<List<MyMood>>
    fun getNoteAppearance(noteId: String): Flowable<MyNoteAppearance>
    fun getImagesForNote(noteId: String): Flowable<List<MyImage>>
    fun getAllNotesWithProp(): Flowable<List<MyNoteWithProp>>
    fun getHeaderImages(): Flowable<List<MyHeaderImage>>
    fun getDbProfile(): Observable<MyProfile>
    fun getCloudProfile(email: String): Maybe<MyProfile>
    fun getTextColor(): Int
    fun getTextSize(): Int
    fun getNoteBackgroundColor(): Int
    fun getNoteTextBackgroundColor(): Int
    fun getTimeFormat(): Int
    fun replaceNoteTags(noteId: String, tags: List<MyTag>): Completable
    fun findNote(noteId: String): Maybe<MyNote>
    fun saveImageToStorage(image: MyImage): Single<MyImage>
    fun saveProfile(profile: MyProfile): Completable
    fun addLocation(location: MyLocation): Completable
    fun createProfile(profile: MyProfile): Completable
    fun loadHeaderImages(page: Int = 1, perPage: Int = 20): Single<ImageResponse>

    companion object {
        const val TIME_FORMAT_12 = 0
        const val TIME_FORMAT_24 = 1
    }
}