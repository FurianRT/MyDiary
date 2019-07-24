package com.furianrt.mydiary.domain

import com.furianrt.mydiary.data.repository.note.NoteRepository
import javax.inject.Inject

class SwapNoteSortTypeUseCase @Inject constructor(
        private val noteRepository: NoteRepository
) {
    fun invoke() {
        noteRepository.setSortDesc(!noteRepository.isSortDesc())
    }
}