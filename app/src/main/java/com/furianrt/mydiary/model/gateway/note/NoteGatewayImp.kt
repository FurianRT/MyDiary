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
import com.furianrt.mydiary.model.source.database.dao.*
import com.furianrt.mydiary.model.source.preferences.PreferencesSource
import com.furianrt.mydiary.utils.MyRxUtils
import com.google.common.base.Optional
import io.reactivex.rxjava3.core.*
import javax.inject.Inject

class NoteGatewayImp @Inject constructor(
        private val noteDao: NoteDao,
        private val tagDao: TagDao,
        private val imageDao: ImageDao,
        private val noteTagDao: NoteTagDao,
        private val locationDao: LocationDao,
        private val noteLocationDao: NoteLocationDao,
        private val spanDao: SpanDao,
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

    override fun getDeletedNotes(): Flowable<List<MyNote>> =
            noteDao.getDeletedNotes()
                    .subscribeOn(scheduler.io())

    override fun getNote(noteId: String): Flowable<Optional<MyNote>> =
            noteDao.getNoteAsList(noteId)
                    .map { Optional.fromNullable(it.firstOrNull()) }
                    .subscribeOn(scheduler.io())

    override fun getAllNotesWithProp(): Flowable<List<MyNoteWithProp>> =
            Flowable.combineLatest(
                    noteDao.getAllNotesWithProp(),
                    noteTagDao.getAllNoteTags(),
                    tagDao.getAllTags(),
                    imageDao.getAllImages(),
                    noteLocationDao.getAllNoteLocations(),
                    locationDao.getAllLocations(),
                    spanDao.getAllTextSpans(),
                    spanDao.getDeletedTextSpans(),
                    imageDao.getDeletedImages(),
                    { notes, noteTags, tags, images, noteLocations, locations, spans, deletedSpans, deletedImages ->
                        notes.map { note ->
                            val noteTagsForNote = noteTags.filter { it.noteId == note.note.id }
                            note.tags = tags.filter { tag ->
                                noteTagsForNote.find { it.tagId == tag.id } != null
                            }

                            note.images = images.filter { it.noteId == note.note.id }
                            note.deletedImages = deletedImages.filter { it.noteId == note.note.id }

                            val noteLocationsForNote = noteLocations.filter { it.noteId == note.note.id }
                            note.locations = locations.filter { location ->
                                noteLocationsForNote.find { it.locationId == location.id } != null
                            }

                            note.textSpans = spans.filter { it.noteId == note.note.id }
                            note.deletedTextSpans = deletedSpans.filter { it.noteId == note.note.id }

                            return@map note
                        }
                    }
            )
                    .subscribeOn(scheduler.io())

    override fun getNoteWithProp(noteId: String): Flowable<Optional<MyNoteWithProp>> =
            Flowable.combineLatest(
                    noteDao.getNoteWithProp(noteId),
                    noteTagDao.getTagsForNote(noteId),
                    imageDao.getImagesForNote(noteId),
                    noteLocationDao.getLocationsForNote(noteId),
                    spanDao.getTextSpans(noteId),
                    spanDao.getDeletedTextSpans(noteId),
                    imageDao.getDeletedImages(noteId),
                    { notes, tags, images, locations, spans, deletedSpans, deletedImages ->
                        val note = notes.firstOrNull()
                        if (note != null) {
                            note.tags = tags
                            note.images = images
                            note.deletedImages = deletedImages
                            note.locations = locations
                            note.textSpans = spans
                            note.deletedTextSpans = deletedSpans
                            Optional.of(note)
                        } else {
                            Optional.absent()
                        }
                    }
            )
                    .subscribeOn(scheduler.io())

    override fun getNotesWithSpans(): Flowable<List<MyNoteWithSpans>> =
            Flowable.combineLatest(
                    noteDao.getAllNotes(),
                    spanDao.getAllTextSpans(),
                    spanDao.getDeletedTextSpans(),
                    { notes, spans, deletedSpans ->
                        notes.map { note ->
                            MyNoteWithSpans(
                                    note,
                                    spans.filter { it.noteId == note.id },
                                    deletedSpans.filter { it.noteId == note.id }
                            )
                        }
                    }
            )
                    .subscribeOn(scheduler.io())

    override fun getNoteWithSpans(noteId: String): Flowable<Optional<MyNoteWithSpans>> =
            Flowable.combineLatest(
                    noteDao.getNoteAsList(noteId),
                    spanDao.getTextSpans(noteId),
                    spanDao.getDeletedTextSpans(noteId),
                    { notes, spans, deletedSpans ->
                        val note = notes.firstOrNull()
                        if (note != null) {
                            Optional.of(MyNoteWithSpans(note, spans, deletedSpans))
                        } else {
                            Optional.absent()
                        }
                    }
            )
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