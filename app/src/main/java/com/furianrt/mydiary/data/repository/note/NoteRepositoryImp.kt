package com.furianrt.mydiary.data.repository.note

import com.furianrt.mydiary.data.auth.AuthHelper
import com.furianrt.mydiary.data.cloud.CloudHelper
import com.furianrt.mydiary.data.database.NoteDatabase
import com.furianrt.mydiary.data.model.*
import com.furianrt.mydiary.data.prefs.PreferencesHelper
import com.furianrt.mydiary.data.storage.StorageHelper
import io.reactivex.*
import io.reactivex.functions.Function6
import javax.inject.Inject

class NoteRepositoryImp @Inject constructor(
        private val database: NoteDatabase,
        private val prefs: PreferencesHelper,
        private val storage: StorageHelper,
        private val cloud: CloudHelper,
        private val auth: AuthHelper,
        private val rxScheduler: Scheduler
) : NoteRepository {

    override fun insertNote(note: MyNote): Completable =
            database.noteDao().insert(note)
                    .subscribeOn(rxScheduler)

    override fun insertNote(notes: List<MyNote>): Completable =
            database.noteDao().insert(notes)
                    .subscribeOn(rxScheduler)

    override fun updateNote(note: MyNote): Completable =
            database.noteDao().update(note.apply { syncWith.clear() })
                    .subscribeOn(rxScheduler)

    override fun updateNoteText(noteId: String, title: String, content: String): Completable =
            database.noteDao().updateNoteText(noteId, title, content)
                    .subscribeOn(rxScheduler)

    override fun updateNotesSync(notes: List<MyNote>): Completable =
            database.noteDao().update(notes)
                    .subscribeOn(rxScheduler)

    override fun deleteNote(noteId: String): Completable =
            database.noteTagDao().deleteWithNoteId(noteId)
                    .andThen(database.noteLocationDao().getLocationsForNote(noteId))
                    .first(emptyList())
                    .flatMapCompletable { locations -> database.locationDao().delete(locations.map { it.id }) }
                    .andThen(database.noteLocationDao().deleteWithNoteId(noteId))
                    .andThen(database.appearanceDao().delete(noteId))
                    .andThen(database.imageDao().getImagesForNote(noteId))
                    .first(emptyList())
                    .flatMapObservable { Observable.fromIterable(it) }
                    .flatMapSingle { Single.fromCallable { storage.deleteFile(it.name) } }
                    .collectInto(mutableListOf<Boolean>()) { l, i -> l.add(i) }
                    .flatMapCompletable { database.imageDao().deleteByNoteId(noteId) }
                    .andThen(database.noteDao().delete(noteId))
                    .andThen(database.forecastDao().delete(noteId))
                    .subscribeOn(rxScheduler)

    override fun cleanupNotes(): Completable =
            database.noteDao().cleanup()
                    .subscribeOn(rxScheduler)

    override fun getAllNotes(): Flowable<List<MyNote>> =
            database.noteDao()
                    .getAllNotes()
                    .subscribeOn(rxScheduler)

    override fun getDeletedNotes(): Flowable<List<MyNote>> =
            database.noteDao()
                    .getDeletedNotes()
                    .subscribeOn(rxScheduler)

    override fun getNote(noteId: String): Single<MyNote> =
            database.noteDao()
                    .getNote(noteId)
                    .subscribeOn(rxScheduler)

    override fun getNoteAsList(noteId: String): Flowable<List<MyNote>> =
            database.noteDao()
                    .getNoteAsList(noteId)
                    .subscribeOn(rxScheduler)

    override fun findNote(noteId: String): Maybe<MyNote> =
            database.noteDao()
                    .findNote(noteId)
                    .subscribeOn(rxScheduler)

    //Следит почти за всеми таблицами. Использовать только когда дейстительно необходимо!
    override fun getAllNotesWithProp(): Flowable<List<MyNoteWithProp>> =
            Flowable.combineLatest(
                    database.noteDao().getAllNotesWithProp(),
                    database.noteTagDao().getAllNoteTags(),
                    database.tagDao().getAllTags(),
                    database.imageDao().getAllImages(),
                    database.noteLocationDao().getAllNoteLocations(),
                    database.locationDao().getAllLocations(),
                    Function6<List<MyNoteWithProp>, List<NoteTag>, List<MyTag>, List<MyImage>,
                            List<NoteLocation>, List<MyLocation>, List<MyNoteWithProp>>
                    { notes, noteTags, tags, images, noteLocatios, locations ->
                        notes.map { note ->
                            val noteTagsForNote = noteTags.filter { it.noteId == note.note.id }
                            note.tags = tags.filter { tag ->
                                noteTagsForNote.find { it.tagId == tag.id } != null
                            }

                            note.images = images.filter { it.noteId == note.note.id }

                            val noteLocatiosForNote = noteLocatios.filter { it.noteId == note.note.id }
                            note.locations = locations.filter { location ->
                                noteLocatiosForNote.find { it.locationId == location.id } != null
                            }

                            return@map note
                        }
                    }
            )

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