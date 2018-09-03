package com.furianrt.mydiary.data

import com.furianrt.mydiary.data.api.Forecast
import com.furianrt.mydiary.data.model.MyNote
import com.furianrt.mydiary.data.model.MyTag
import com.furianrt.mydiary.data.model.NoteTag
import com.furianrt.mydiary.data.model.NoteWithTags
import io.reactivex.*

interface DataManager {

    fun insertNote(note: MyNote) : Single<Long>

    fun updateNote(note: MyNote) : Completable

    fun deleteNote(note: MyNote) : Completable

    fun getAllNotes(): Flowable<List<MyNote>>

    fun getNoteWithTags(noteId: Long): Single<NoteWithTags>

    fun getAllTags(): Single<List<MyTag>>

    fun findNote(noteId: Long): Maybe<MyNote>

    fun getForecast(lat: Double, lon: Double): Single<Forecast>

    fun deleteNoteTag(noteTag: NoteTag): Completable

    fun deleteAllTagsForNote(noteId: Long): Completable

    fun insertNoteTag(noteTag: NoteTag): Completable

    fun insertAllNoteTag(noteTags: List<NoteTag>): Completable
}