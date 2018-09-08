package com.furianrt.mydiary.data

import com.furianrt.mydiary.data.api.Forecast
import com.furianrt.mydiary.data.model.MyNote
import com.furianrt.mydiary.data.model.MyTag
import com.furianrt.mydiary.data.model.NoteTag
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

interface DataManager {

    fun insertNote(note: MyNote): Single<Long>

    fun insertNoteTag(noteTag: NoteTag): Completable

    fun insertTag(tag: MyTag): Single<Long>

    fun insertTagsForNote(noteId: Long, tags: List<MyTag>): Completable

    fun updateNote(note: MyNote): Completable

    fun updateTag(tag: MyTag): Completable

    fun deleteTag(tag: MyTag): Completable

    fun deleteNote(note: MyNote): Completable

    fun deleteNoteTag(noteTag: NoteTag): Completable

    fun deleteAllTagsForNote(noteId: Long): Completable

    fun getAllNotes(): Flowable<List<MyNote>>

    fun getTagsForNote(noteId: Long): Flowable<List<MyTag>>

    fun getNotesWithTag(tagId: Long): Flowable<List<MyNote>>

    fun getAllTags(): Single<List<MyTag>>

    fun getForecast(lat: Double, lon: Double): Single<Forecast>

    fun findNote(noteId: Long): Maybe<MyNote>
}