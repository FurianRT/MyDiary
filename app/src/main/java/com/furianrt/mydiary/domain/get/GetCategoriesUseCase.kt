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

import com.furianrt.mydiary.model.entity.MyCategory
import com.furianrt.mydiary.model.entity.MyNote
import com.furianrt.mydiary.model.repository.category.CategoryRepository
import com.furianrt.mydiary.model.repository.note.NoteRepository
import com.google.common.base.Optional
import io.reactivex.Flowable
import io.reactivex.functions.BiFunction
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
        private val categoryRepository: CategoryRepository,
        private val noteRepository: NoteRepository
) {

    fun invoke(): Flowable<List<MyCategory>> = categoryRepository.getAllCategories()

    fun invoke(noteId: String): Flowable<Optional<MyCategory>> =
            Flowable.combineLatest(noteRepository.getNoteAsList(noteId),
                    categoryRepository.getAllCategories(),
                    BiFunction<List<MyNote>, List<MyCategory>, Optional<MyCategory>> { note, categories ->
                        if (categories.isEmpty() || note.isEmpty()) {
                            Optional.absent()
                        } else {
                            Optional.fromNullable(categories.find { it.id == note.first().categoryId })
                        }
                    })
}