package com.furianrt.mydiary.domain

import com.furianrt.mydiary.data.model.NoteTag
import com.furianrt.mydiary.data.repository.tag.TagRepository
import io.reactivex.Completable
import javax.inject.Inject

class AddTagToNoteUseCase @Inject constructor(
        private val tagRepository: TagRepository
) {

    fun invoke(noteId: String, tagId: String): Completable =
            tagRepository.insertNoteTag(NoteTag(noteId, tagId))

}