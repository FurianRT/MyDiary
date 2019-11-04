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
import com.furianrt.mydiary.model.gateway.category.CategoryGateway
import com.furianrt.mydiary.model.gateway.note.NoteGateway
import com.google.common.base.Optional
import io.reactivex.Flowable
import io.reactivex.functions.BiFunction
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
        private val categoryGateway: CategoryGateway,
        private val noteGateway: NoteGateway
) {

    fun invoke(): Flowable<List<MyCategory>> = categoryGateway.getAllCategories()

    fun invoke(noteId: String): Flowable<Optional<MyCategory>> =
            Flowable.combineLatest(noteGateway.getNoteAsList(noteId),
                    categoryGateway.getAllCategories(),
                    BiFunction<List<MyNote>, List<MyCategory>, Optional<MyCategory>> { note, categories ->
                        if (categories.isEmpty() || note.isEmpty()) {
                            Optional.absent()
                        } else {
                            Optional.fromNullable(categories.find { it.id == note.first().categoryId })
                        }
                    })
}