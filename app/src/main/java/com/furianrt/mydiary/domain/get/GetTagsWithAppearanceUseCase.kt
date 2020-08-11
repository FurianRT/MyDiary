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
import com.furianrt.mydiary.model.gateway.tag.TagGateway
import com.google.common.base.Optional
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.functions.BiFunction
import javax.inject.Inject

class GetTagsWithAppearanceUseCase @Inject constructor(
        private val tagGateway: TagGateway,
        private val getAppearanceUseCase: GetAppearanceUseCase
) {

    operator fun invoke(noteId: String): Flowable<TagsAndAppearance> =
            Flowable.combineLatest(tagGateway.getTagsForNote(noteId),
                    getAppearanceUseCase(noteId),
                    BiFunction<List<MyTag>, Optional<MyNoteAppearance>, TagsAndAppearance> { tags, appearance ->
                        TagsAndAppearance(tags, appearance.orNull())
                    })
}