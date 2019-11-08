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

import com.furianrt.mydiary.model.gateway.note.NoteGateway
import javax.inject.Inject

class SwapNoteSortTypeUseCase @Inject constructor(
        private val noteGateway: NoteGateway
) {
    fun invoke() {
        noteGateway.setSortDesc(!noteGateway.isSortDesc())
    }
}