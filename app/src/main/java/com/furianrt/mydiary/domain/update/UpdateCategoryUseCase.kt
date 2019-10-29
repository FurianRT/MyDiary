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
import com.furianrt.mydiary.model.repository.category.CategoryRepository
import com.furianrt.mydiary.model.repository.note.NoteRepository
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject

class UpdateCategoryUseCase @Inject constructor(
        private val categoryRepository: CategoryRepository,
        private val noteRepository: NoteRepository
) {

    fun invoke(category: MyCategory): Completable =
            categoryRepository.updateCategory(category.apply { syncWith.clear() })
                    .andThen(noteRepository.getAllNotes())
                    .first(emptyList())
                    .flatMapObservable { Observable.fromIterable(it) }
                    .filter { it.categoryId == category.id }
                    .map { it.apply { it.syncWith.clear() } }
                    .flatMapSingle { noteRepository.updateNote(it).toSingleDefault(true) }
                    .collectInto(mutableListOf<Boolean>()) { l, i -> l.add(i) }
                    .ignoreElement()
}