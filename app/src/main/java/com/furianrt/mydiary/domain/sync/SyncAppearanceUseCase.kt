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

import com.furianrt.mydiary.model.gateway.appearance.AppearanceGateway
import io.reactivex.Completable
import javax.inject.Inject

class SyncAppearanceUseCase @Inject constructor(
        private val appearanceGateway: AppearanceGateway
) {

    class SyncAppearanceException : Throwable()

    operator fun invoke(email: String): Completable =
            appearanceGateway.getAllNoteAppearances()
                    .firstOrError()
                    .map { appearances -> appearances.filter { !it.isSync(email) } }
                    .map { appearances -> appearances.apply { forEach { it.syncWith.add(email) } } }
                    .flatMapCompletable { appearances ->
                        Completable.concat(listOf(
                                appearanceGateway.saveAppearancesInCloud(appearances),
                                appearanceGateway.updateAppearancesSync(appearances)
                        ))
                    }
                    .andThen(appearanceGateway.getDeletedAppearances().firstOrError())
                    .flatMapCompletable { appearanceGateway.deleteAppearancesFromCloud(it).onErrorComplete() }
                    .andThen(appearanceGateway.getAllAppearancesFromCloud())
                    .flatMapCompletable { appearanceGateway.insertAppearance(it) }
                    .onErrorResumeNext { error ->
                        error.printStackTrace()
                        Completable.error(SyncAppearanceException())
                    }
}