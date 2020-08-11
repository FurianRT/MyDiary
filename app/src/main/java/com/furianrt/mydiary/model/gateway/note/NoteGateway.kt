/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.model.gateway.note

import com.furianrt.mydiary.model.entity.MyNote
import com.furianrt.mydiary.model.entity.MyNoteWithProp
import com.furianrt.mydiary.model.entity.MyNoteWithSpans
import com.google.common.base.Optional
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

interface NoteGateway {
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
    fun getNote(noteId: String): Flowable<Optional<MyNote>>
    fun getAllNotesWithProp(): Flowable<List<MyNoteWithProp>>
    fun getNoteWithProp(noteId: String): Flowable<Optional<MyNoteWithProp>>
    fun getNotesWithSpans(): Flowable<List<MyNoteWithSpans>>
    fun getNoteWithSpans(noteId: String): Flowable<Optional<MyNoteWithSpans>>
    fun getAllNotesFromCloud(): Single<List<MyNote>>
    fun saveNotesInCloud(notes: List<MyNote>): Completable
    fun isSortDesc(): Boolean
    fun setSortDesc(desc: Boolean)
}