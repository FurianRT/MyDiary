/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.model.repository.note

import com.furianrt.mydiary.model.entity.MyNote
import com.furianrt.mydiary.model.entity.MyNoteWithProp
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

interface NoteRepository {
    fun insertNote(note: MyNote): Completable
    fun insertNote(notes: List<MyNote>): Completable
    fun updateNote(note: MyNote): Completable
    fun updateNoteText(noteId: String, title: String, content: String): Completable
    fun updateNotesSync(notes: List<MyNote>): Completable
    fun deleteNote(noteId: String): Completable
    fun deleteNotesFromCloud(notes: List<MyNote>): Completable
    fun cleanupNotes(): Completable
    fun getAllNotes(): Flowable<List<MyNote>>
    fun getDeletedNotes(): Flowable<List<MyNote>>
    fun getNote(noteId: String): Single<MyNote>
    fun getNoteAsList(noteId: String): Flowable<List<MyNote>>
    fun getAllNotesWithProp(): Flowable<List<MyNoteWithProp>>
    fun getAllNotesFromCloud(): Single<List<MyNote>>
    fun findNote(noteId: String): Maybe<MyNote>
    fun saveNotesInCloud(notes: List<MyNote>): Completable
    fun isSortDesc(): Boolean
    fun setSortDesc(desc: Boolean)
}