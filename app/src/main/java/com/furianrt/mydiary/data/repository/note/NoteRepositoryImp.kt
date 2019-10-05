/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.data.repository.note

import com.furianrt.mydiary.data.entity.*
import com.furianrt.mydiary.data.source.auth.AuthHelper
import com.furianrt.mydiary.data.source.cloud.CloudHelper
import com.furianrt.mydiary.data.source.database.NoteDao
import com.furianrt.mydiary.data.source.database.NoteTagDao
import com.furianrt.mydiary.data.source.preferences.PreferencesHelper
import io.reactivex.*
import javax.inject.Inject

class NoteRepositoryImp @Inject constructor(
        private val noteDao: NoteDao,
        private val noteTagDao: NoteTagDao,
        private val prefs: PreferencesHelper,
        private val cloud: CloudHelper,
        private val auth: AuthHelper,
        private val rxScheduler: Scheduler
) : NoteRepository {

    override fun insertNote(note: MyNote): Completable =
            noteDao.insert(note)
                    .subscribeOn(rxScheduler)

    override fun insertNote(notes: List<MyNote>): Completable =
            noteDao.insert(notes)
                    .subscribeOn(rxScheduler)

    override fun updateNote(note: MyNote): Completable =
            noteDao.update(note.apply { syncWith.clear() })
                    .subscribeOn(rxScheduler)

    override fun updateNoteText(noteId: String, title: String, content: String): Completable =
            noteDao.updateNoteText(noteId, title, content)
                    .subscribeOn(rxScheduler)

    override fun updateNotesSync(notes: List<MyNote>): Completable =
            noteDao.update(notes)
                    .subscribeOn(rxScheduler)

    override fun deleteNote(noteId: String): Completable =
            noteTagDao.deleteWithNoteId(noteId)
                    .andThen(noteDao.delete(noteId))
                    .subscribeOn(rxScheduler)

    override fun cleanupNotes(): Completable =
            noteDao.cleanup()
                    .subscribeOn(rxScheduler)

    override fun getAllNotes(): Flowable<List<MyNote>> =
            noteDao.getAllNotes()
                    .subscribeOn(rxScheduler)

    override fun getDeletedNotes(): Flowable<List<MyNote>> =
            noteDao.getDeletedNotes()
                    .subscribeOn(rxScheduler)

    override fun getNote(noteId: String): Single<MyNote> =
            noteDao.getNote(noteId)
                    .subscribeOn(rxScheduler)

    override fun getNoteAsList(noteId: String): Flowable<List<MyNote>> =
            noteDao.getNoteAsList(noteId)
                    .subscribeOn(rxScheduler)

    override fun findNote(noteId: String): Maybe<MyNote> =
            noteDao.findNote(noteId)
                    .subscribeOn(rxScheduler)

    override fun getAllNotesWithProp(): Flowable<List<MyNoteWithProp>> =
            noteDao.getAllNotesWithProp()
                    .subscribeOn(rxScheduler)

    override fun saveNotesInCloud(notes: List<MyNote>): Completable =
            cloud.saveNotes(notes, auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun deleteNotesFromCloud(notes: List<MyNote>): Completable =
            cloud.deleteNotes(notes, auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun getAllNotesFromCloud(): Single<List<MyNote>> =
            cloud.getAllNotes(auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun isSortDesc(): Boolean = prefs.isSortDesc()

    override fun setSortDesc(desc: Boolean) {
        prefs.setSortDesc(desc)
    }
}