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

import com.furianrt.mydiary.model.entity.*
import com.furianrt.mydiary.model.source.auth.AuthSource
import com.furianrt.mydiary.model.source.cloud.CloudSource
import com.furianrt.mydiary.model.source.database.dao.NoteDao
import com.furianrt.mydiary.model.source.database.dao.NoteTagDao
import com.furianrt.mydiary.model.source.preferences.PreferencesSource
import com.furianrt.mydiary.utils.MyRxUtils
import io.reactivex.*
import javax.inject.Inject

class NoteGatewayImp @Inject constructor(
        private val noteDao: NoteDao,
        private val noteTagDao: NoteTagDao,
        private val prefs: PreferencesSource,
        private val cloud: CloudSource,
        private val auth: AuthSource,
        private val scheduler: MyRxUtils.BaseSchedulerProvider
) : NoteGateway {

    override fun insertNote(note: MyNote): Completable =
            noteDao.insert(note)
                    .subscribeOn(scheduler.io())

    override fun insertNote(notes: List<MyNote>): Completable =
            noteDao.insert(notes)
                    .subscribeOn(scheduler.io())

    override fun updateNote(note: MyNote): Completable =
            noteDao.update(note.apply { syncWith.clear() })
                    .subscribeOn(scheduler.io())

    override fun updateNoteText(noteId: String, title: String, content: String): Completable =
            noteDao.updateNoteText(noteId, title, content)
                    .subscribeOn(scheduler.io())

    override fun updateNoteTextBlocking(noteId: String, title: String, content: String) {
        noteDao.updateNoteTextBlocking(noteId, title, content)
    }

    override fun updateNotesSync(notes: List<MyNote>): Completable =
            noteDao.update(notes)
                    .subscribeOn(scheduler.io())

    override fun deleteNote(noteId: String): Completable =
            noteTagDao.deleteWithNoteId(noteId)
                    .andThen(noteDao.delete(noteId))
                    .subscribeOn(scheduler.io())

    override fun cleanupNotes(): Completable =
            noteDao.cleanup()
                    .subscribeOn(scheduler.io())

    override fun getAllNotes(): Flowable<List<MyNote>> =
            noteDao.getAllNotes()
                    .subscribeOn(scheduler.io())

    override fun getAllNotesWithImages(): Flowable<List<MyNoteWithImages>> =
            noteDao.getAllNotesWithImages()
                    .subscribeOn(scheduler.io())

    override fun getDeletedNotes(): Flowable<List<MyNote>> =
            noteDao.getDeletedNotes()
                    .subscribeOn(scheduler.io())

    override fun getNote(noteId: String): Single<MyNote> =
            noteDao.getNote(noteId)
                    .subscribeOn(scheduler.io())

    override fun getNoteAsList(noteId: String): Flowable<List<MyNote>> =
            noteDao.getNoteAsList(noteId)
                    .subscribeOn(scheduler.io())

    override fun getNoteWithImagesAsList(noteId: String): Flowable<List<MyNoteWithImages>> =
            noteDao.getNoteWithImagesAsList(noteId)
                    .subscribeOn(scheduler.io())

    override fun findNote(noteId: String): Maybe<MyNote> =
            noteDao.findNote(noteId)
                    .subscribeOn(scheduler.io())

    override fun getAllNotesWithProp(): Flowable<List<MyNoteWithProp>> =
            noteDao.getAllNotesWithProp()
                    .subscribeOn(scheduler.io())

    override fun saveNotesInCloud(notes: List<MyNote>): Completable =
            cloud.saveNotes(notes, auth.getUserId())
                    .subscribeOn(scheduler.io())

    override fun deleteNotesFromCloud(notes: List<MyNote>): Completable =
            cloud.deleteNotes(notes, auth.getUserId())
                    .subscribeOn(scheduler.io())

    override fun getAllNotesFromCloud(): Single<List<MyNote>> =
            cloud.getAllNotes(auth.getUserId())
                    .subscribeOn(scheduler.io())

    override fun isSortDesc(): Boolean = prefs.isSortDesc()

    override fun setSortDesc(desc: Boolean) {
        prefs.setSortDesc(desc)
    }
}