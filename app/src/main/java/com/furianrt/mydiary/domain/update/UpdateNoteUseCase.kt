package com.furianrt.mydiary.domain.update

import com.furianrt.mydiary.data.model.MyNote
import com.furianrt.mydiary.data.repository.note.NoteRepository
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject

class UpdateNoteUseCase @Inject constructor(
        private val noteRepository: NoteRepository
) {

    fun invoke(note: MyNote): Completable = noteRepository.updateNote(note)

    fun invoke(noteId: String, moodId: Int): Completable =
            noteRepository.getNote(noteId)
                    .flatMapCompletable { noteRepository.updateNote(it.apply { this.moodId = moodId }) }

    fun invoke(noteIds: List<String>, categoryId: String): Completable =
            Observable.fromIterable(noteIds)
                    .flatMapSingle { noteRepository.getNote(it) }
                    .flatMapSingle { note ->
                        noteRepository.updateNote(note.apply { this.categoryId = categoryId })
                                .toSingleDefault(true)
                    }
                    .collectInto(mutableListOf<Boolean>()) { l, i -> l.add(i) }
                    .ignoreElement()

    fun invoke(noteId: String, title: String, content: String): Completable =
            noteRepository.updateNoteText(noteId, title, content)
}