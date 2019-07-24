package com.furianrt.mydiary.domain.delete

import com.furianrt.mydiary.data.repository.tag.TagRepository
import io.reactivex.Completable
import javax.inject.Inject

class RemoveTagFromNoteUseCase @Inject constructor(
        private val tagRepository: TagRepository
) {

    fun invoke(noteId: String, tagId: String): Completable =
            tagRepository.deleteNoteTag(noteId, tagId)
}