package com.furianrt.mydiary.usecase.update

import com.furianrt.mydiary.data.model.MyNoteAppearance
import com.furianrt.mydiary.data.repository.appearance.AppearanceRepository
import io.reactivex.Completable
import javax.inject.Inject

class UpdateAppearanceUseCase @Inject constructor(
        private val appearanceRepository: AppearanceRepository
) {

    fun invoke(appearance: MyNoteAppearance): Completable =
            appearanceRepository.updateAppearance(appearance)
}