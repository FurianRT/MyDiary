/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

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