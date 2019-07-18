package com.furianrt.mydiary.domain.delete

import com.furianrt.mydiary.data.repository.note.NoteRepository
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject

class DeleteNotesUseCase @Inject constructor(
        private val noteRepository: NoteRepository
) {

    fun invoke(notesIds: List<String>): Completable =
            Observable.fromIterable(notesIds)
                    .flatMapCompletable { noteRepository.deleteNote(it) }
}