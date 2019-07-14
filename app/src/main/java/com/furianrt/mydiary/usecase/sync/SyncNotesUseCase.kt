package com.furianrt.mydiary.usecase.sync

import com.furianrt.mydiary.data.repository.note.NoteRepository
import io.reactivex.Completable
import javax.inject.Inject

class SyncNotesUseCase @Inject constructor(
        private val noteRepository: NoteRepository
) {

    class SyncNotessException : Throwable()

    fun invoke(email: String): Completable =
            noteRepository.getAllNotes()
                    .first(emptyList())
                    .map { notes -> notes.filter { !it.isSync(email) } }
                    .map { notes -> notes.apply { forEach { it.syncWith.add(email) } } }
                    .flatMapCompletable { notes ->
                        Completable.concat(listOf(
                                noteRepository.saveNotesInCloud(notes),
                                noteRepository.updateNotesSync(notes)
                        ))
                    }
                    .andThen(noteRepository.getDeletedNotes().first(emptyList()))
                    .flatMapCompletable { noteRepository.deleteNotesFromCloud(it) }
                    .andThen(noteRepository.getAllNotesFromCloud())
                    .flatMapCompletable { noteRepository.insertNote(it) }
                    .onErrorResumeNext { Completable.error(SyncNotessException()) }
}