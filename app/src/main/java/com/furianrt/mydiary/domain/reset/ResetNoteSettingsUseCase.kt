/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain.reset

import com.furianrt.mydiary.data.entity.MyNoteAppearance
import com.furianrt.mydiary.domain.update.UpdateAppearanceUseCase
import io.reactivex.Completable
import javax.inject.Inject

class ResetNoteSettingsUseCase @Inject constructor(
        private val updateAppearanceUseCase: UpdateAppearanceUseCase
) {

    fun invoke(appearanceId: String): Completable =
            updateAppearanceUseCase.invoke(MyNoteAppearance(appearanceId))
}