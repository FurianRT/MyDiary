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

import com.furianrt.mydiary.model.entity.MyNoteAppearance
import com.furianrt.mydiary.model.entity.MyTag
import com.furianrt.mydiary.model.entity.pojo.TagsAndAppearance
import com.furianrt.mydiary.model.repository.appearance.AppearanceRepository
import com.furianrt.mydiary.model.repository.tag.TagRepository
import io.reactivex.Flowable
import io.reactivex.functions.BiFunction
import javax.inject.Inject

class GetTagsWithAppearanceUseCase @Inject constructor(
        private val tagRepository: TagRepository,
        private val appearanceRepository: AppearanceRepository
) {

    fun invoke(noteId: String): Flowable<TagsAndAppearance> =
            Flowable.combineLatest(tagRepository.getTagsForNote(noteId),
                    appearanceRepository.getNoteAppearance(noteId),
                    BiFunction<List<MyTag>, MyNoteAppearance, TagsAndAppearance> { tags, appearance ->
                        TagsAndAppearance(tags, appearance)
                    })
}