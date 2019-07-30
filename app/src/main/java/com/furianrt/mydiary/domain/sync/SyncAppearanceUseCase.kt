/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain.sync

import com.furianrt.mydiary.data.repository.appearance.AppearanceRepository
import io.reactivex.Completable
import javax.inject.Inject

class SyncAppearanceUseCase @Inject constructor(
        private val appearanceRepository: AppearanceRepository
) {

    class SyncAppearanceException : Throwable()

    fun invoke(email: String): Completable =
            appearanceRepository.getAllNoteAppearances()
                    .first(emptyList())
                    .map { appearances -> appearances.filter { !it.isSync(email) } }
                    .map { appearances -> appearances.apply { forEach { it.syncWith.add(email) } } }
                    .flatMapCompletable { appearances ->
                        Completable.concat(listOf(
                                appearanceRepository.saveAppearancesInCloud(appearances),
                                appearanceRepository.updateAppearancesSync(appearances)
                        ))
                    }
                    .andThen(appearanceRepository.getDeletedAppearances().first(emptyList()))
                    .flatMapCompletable { appearanceRepository.deleteAppearancesFromCloud(it) }
                    .andThen(appearanceRepository.getAllAppearancesFromCloud())
                    .flatMapCompletable { appearanceRepository.insertAppearance(it) }
                    .onErrorResumeNext { Completable.error(SyncAppearanceException()) }
}