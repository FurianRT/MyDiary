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

import com.furianrt.mydiary.model.entity.MyTextSpan
import com.furianrt.mydiary.model.gateway.span.SpanGateway
import com.furianrt.mydiary.di.application.component.AppScope
import com.furianrt.mydiary.domain.check.IsPremiumPurchasedUseCase
import com.furianrt.mydiary.utils.generateUniqueId
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

@AppScope
class UpdateNoteSpansUseCase @Inject constructor(
        private val spanGateway: SpanGateway,
        private val isPremiumPurchasedUseCase: IsPremiumPurchasedUseCase
) {

    private fun List<MyTextSpan>.isEqualTo(spans: List<MyTextSpan>): Boolean {
        var isSpansEqual = size == spans.size
        spans.forEach { span ->
            if (find {
                        it.type == span.type
                                && it.startIndex == span.startIndex
                                && it.endIndex == span.endIndex
                                && it.size == span.size
                                && it.color == span.color
                    } == null) {
                isSpansEqual = false
            }
        }
        return isSpansEqual
    }

    operator fun invoke(noteId: String, textSpans: List<MyTextSpan>): Completable =
            spanGateway.getTextSpans(noteId)
                    .firstOrError()
                    .flatMapCompletable { existingSpans ->
                        if (existingSpans.isEqualTo(textSpans)) {
                            Completable.complete()
                        } else {
                            if (isPremiumPurchasedUseCase()) {
                                spanGateway.deleteTextSpan(noteId)
                                        .andThen(spanGateway.insertTextSpan(textSpans.map { span ->
                                            span.apply {
                                                span.id = generateUniqueId()
                                                span.noteId = noteId
                                            }
                                        }))
                            } else {
                                spanGateway.deleteTextSpanPermanently(noteId)
                                        .andThen(spanGateway.insertTextSpan(textSpans.map { span ->
                                            span.apply {
                                                span.id = generateUniqueId()
                                                span.noteId = noteId
                                            }
                                        }))
                            }
                        }
                    }
}