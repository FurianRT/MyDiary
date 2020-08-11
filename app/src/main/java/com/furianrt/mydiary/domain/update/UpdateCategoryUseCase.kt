/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain.update

import com.furianrt.mydiary.model.entity.MyCategory
import com.furianrt.mydiary.model.gateway.category.CategoryGateway
import com.furianrt.mydiary.model.gateway.note.NoteGateway
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class UpdateCategoryUseCase @Inject constructor(
        private val categoryGateway: CategoryGateway,
        private val noteGateway: NoteGateway
) {

    operator fun invoke(category: MyCategory): Completable =
            categoryGateway.updateCategory(category.apply { syncWith.clear() })
                    .andThen(noteGateway.getAllNotes())
                    .first(emptyList())
                    .flatMapObservable { Observable.fromIterable(it) }
                    .filter { it.categoryId == category.id }
                    .map { it.apply { it.syncWith.clear() } }
                    .flatMapSingle { noteGateway.updateNote(it).toSingleDefault(true) }
                    .collectInto(mutableListOf<Boolean>()) { l, i -> l.add(i) }
                    .ignoreElement()
}