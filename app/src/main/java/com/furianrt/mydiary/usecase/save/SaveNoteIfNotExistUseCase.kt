package com.furianrt.mydiary.usecase.save

import com.furianrt.mydiary.data.model.MyNote
import com.furianrt.mydiary.data.model.MyNoteAppearance
import com.furianrt.mydiary.data.repository.appearance.AppearanceRepository
import com.furianrt.mydiary.data.repository.note.NoteRepository
import io.reactivex.Completable
import javax.inject.Inject

class SaveNoteIfNotExistUseCase @Inject constructor(
        private val noteRepository: NoteRepository,
        private val appearanceRepository: AppearanceRepository
) {
    fun invoke(noteId: String): Completable =
        noteRepository.insertNote(MyNote(noteId))
                .andThen(appearanceRepository.insertAppearance(MyNoteAppearance(noteId)))
}