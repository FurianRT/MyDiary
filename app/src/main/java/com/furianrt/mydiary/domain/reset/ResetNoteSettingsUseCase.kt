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

import com.furianrt.mydiary.model.entity.MyNoteAppearance
import com.furianrt.mydiary.domain.update.UpdateAppearanceUseCase
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

class ResetNoteSettingsUseCase @Inject constructor(
        private val updateAppearanceUseCase: UpdateAppearanceUseCase
) {

    operator fun invoke(appearanceId: String): Completable =
            updateAppearanceUseCase(MyNoteAppearance(appearanceId))
}