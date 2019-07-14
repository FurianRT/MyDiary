package com.furianrt.mydiary.usecase.get

import com.furianrt.mydiary.data.model.MyNoteWithProp
import com.furianrt.mydiary.data.repository.note.NoteRepository
import io.reactivex.Flowable
import javax.inject.Inject

class GetFullNotesUseCase @Inject constructor(
        private val noteRepository: NoteRepository
) {

    fun invoke(): Flowable<List<MyNoteWithProp>> =
            noteRepository.getAllNotesWithProp()
                    .map { notes ->
                        if (noteRepository.isSortDesc()) {
                            notes.sortedByDescending { it.note.time }
                        } else {
                            notes.sortedBy { it.note.time }
                        }
                    }

    fun invoke(noteId: String): Flowable<MyNoteWithProp> =
            noteRepository.getAllNotesWithProp()
                    .map { notes -> notes.first { it.note.id == noteId } }

}