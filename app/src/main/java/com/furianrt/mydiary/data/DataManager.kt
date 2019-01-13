package com.furianrt.mydiary.data

import com.furianrt.mydiary.data.api.Forecast
import com.furianrt.mydiary.data.model.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

interface DataManager {

    fun insertNote(note: MyNote): Completable

    fun insertNoteTag(noteTag: NoteTag): Completable

    fun insertTag(tag: MyTag): Completable

    fun insertImage(image: MyImage): Completable

    fun insertImages(images: List<MyImage>): Completable

    fun insertHeaderImage(headerImage: MyHeaderImage): Completable

    fun insertHeaderImage(headerImages: List<MyHeaderImage>): Completable

    fun insertCategory(category: MyCategory): Single<Long>

    fun insertAppearance(appearance: MyNoteAppearance): Completable

    fun updateNote(note: MyNote): Completable

    fun updateNoteText(noteId: String, title: String, content: String): Completable

    fun updateTag(tag: MyTag): Completable

    fun updateImages(images: List<MyImage>): Completable

    fun updateCategory(category: MyCategory): Completable

    fun updateAppearance(appearance: MyNoteAppearance): Completable

    fun deleteTag(tag: MyTag): Completable

    fun deleteNote(note: MyNote): Completable

    fun deleteNotes(notes: List<MyNote>): Completable

    fun deleteImages(images: List<MyImage>): Completable

    fun deleteNoteTag(noteTag: NoteTag): Completable

    fun deleteAllTagsForNote(noteId: String): Completable

    fun deleteImageFromStorage(fileName: String): Single<Boolean>

    fun deleteAllHeaderImages(): Completable

    fun deleteCategory(category: MyCategory): Completable

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

    fun replaceNoteTags(noteId: String, tags: List<MyTag>): Completable

    fun findNote(noteId: String): Maybe<MyNote>

    fun saveImageToStorage(image: MyImage): Single<MyImage>

    fun saveHeaderImageToStorage(headerImage: MyHeaderImage): Single<MyHeaderImage>

    fun getImagesForNote(noteId: String): Flowable<List<MyImage>>

    fun getAllNotesWithProp(): Flowable<List<MyNoteWithProp>>

    fun getHeaderImages(): Flowable<List<MyHeaderImage>>

    fun addLocation(location: MyLocation): Completable

    fun isWeatherEnabled(): Boolean

    fun isLocationEnabled(): Boolean

    fun isMoodEnabled(): Boolean

    fun getTextColor(): Int

    fun getTextSize(): Int

    fun getNoteBackgroundColor(): Int

    fun getNoteTextBackgroundColor(): Int
}