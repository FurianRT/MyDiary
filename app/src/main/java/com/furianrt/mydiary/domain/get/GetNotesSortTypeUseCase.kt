/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain.get

import com.furianrt.mydiary.data.repository.note.NoteRepository
import javax.inject.Inject

class GetNotesSortTypeUseCase @Inject constructor(
        private val noteRepository: NoteRepository
) {

    companion object {
        const val SORT_TYPE_DESC = 0
        const val SORT_TYPE_ASC = 1
    }

    fun invoke(): Int = if (noteRepository.isSortDesc()) {
        SORT_TYPE_DESC
    } else {
        SORT_TYPE_ASC
    }
}