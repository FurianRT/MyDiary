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

    fun insertTag(tag: MyTag): Single<Long>

    fun insertTagsForNote(noteId: String, tags: List<MyTag>): Completable

    fun insertImage(image: MyImage): Completable

    fun insertImages(images: List<MyImage>): Completable

    fun insertHeaderImage(headerImage: MyHeaderImage): Completable

    fun insertHeaderImage(headerImages: List<MyHeaderImage>): Completable

    fun updateNote(note: MyNote): Completable

    fun updateTag(tag: MyTag): Completable

    fun deleteTag(tag: MyTag): Completable

    fun deleteNote(note: MyNote): Completable

    fun deleteNotes(notes: List<MyNote>): Completable

    fun deleteNoteTag(noteTag: NoteTag): Completable

    fun deleteAllTagsForNote(noteId: String): Completable

    fun deleteImageFromStorage(fileName: String): Single<Boolean>

    fun deleteAllHeaderImages(): Completable

    fun getAllNotes(): Flowable<List<MyNote>>

    fun getTagsForNote(noteId: Long): Flowable<List<MyTag>>

    fun getNotesWithTag(tagId: Long): Flowable<List<MyNote>>

    fun getAllTags(): Single<List<MyTag>>

    fun getForecast(lat: Double, lon: Double): Single<Forecast>

    fun findNote(noteId: String): Maybe<MyNote>

    fun saveImageToStorage(image: MyImage): Single<MyImage>

    fun saveHeaderImageToStorage(headerImage: MyHeaderImage): Single<MyHeaderImage>

    fun getImagesForNote(noteId: String): Flowable<List<MyImage>>

    //fun getImageFromStorage(imageName: String) : Single<File>

    fun getNotesWithProp(): Flowable<List<MyNoteWithProp>>

    fun getTags(tagIds: List<Long>): Single<List<MyTag>>

    fun getHeaderImages(): Flowable<List<MyHeaderImage>>

    fun addLocation(location: MyLocation): Single<Long>
}