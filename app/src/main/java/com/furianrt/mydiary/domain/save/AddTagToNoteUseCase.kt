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

import com.furianrt.mydiary.model.entity.NoteTag
import com.furianrt.mydiary.model.gateway.tag.TagGateway
import io.reactivex.Completable
import javax.inject.Inject

class AddTagToNoteUseCase @Inject constructor(
        private val tagGateway: TagGateway
) {

    fun invoke(noteId: String, tagId: String): Completable =
            tagGateway.insertNoteTag(NoteTag(noteId, tagId))

}