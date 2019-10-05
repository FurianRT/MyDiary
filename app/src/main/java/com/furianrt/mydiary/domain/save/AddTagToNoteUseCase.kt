/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain.save

import com.furianrt.mydiary.data.entity.NoteTag
import com.furianrt.mydiary.data.repository.tag.TagRepository
import io.reactivex.Completable
import javax.inject.Inject

class AddTagToNoteUseCase @Inject constructor(
        private val tagRepository: TagRepository
) {

    fun invoke(noteId: String, tagId: String): Completable =
            tagRepository.insertNoteTag(NoteTag(noteId, tagId))

}