package com.furianrt.mydiary.domain.get

import com.furianrt.mydiary.data.model.MyNote
import com.furianrt.mydiary.data.repository.note.NoteRepository
import io.reactivex.Flowable
import javax.inject.Inject

class GetNotesUseCase @Inject constructor(
        private val noteRepository: NoteRepository
) {

    fun invoke(): Flowable<List<MyNote>> =
            noteRepository.getAllNotes()
                    .map { notes ->
                        if (noteRepository.isSortDesc()) {
                            notes.sortedByDescending { it.time }
                        } else {
                            notes.sortedBy { it.time }
                        }
                    }
}