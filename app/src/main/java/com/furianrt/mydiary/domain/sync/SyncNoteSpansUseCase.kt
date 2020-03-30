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

import com.furianrt.mydiary.model.gateway.span.SpanGateway
import io.reactivex.Completable
import javax.inject.Inject

class SyncNoteSpansUseCase @Inject constructor(
        private val spanGateway: SpanGateway
) {

    class SyncSpanException : Throwable()

    operator fun invoke(email: String): Completable =
            spanGateway.getAllTextSpans()
                    .firstOrError()
                    .map { spans -> spans.filter { !it.isSync(email) } }
                    .map { spans -> spans.apply { forEach { it.syncWith.add(email) } } }
                    .flatMapCompletable { spans ->
                        Completable.concat(listOf(
                                spanGateway.saveTextSpansInCloud(spans),
                                spanGateway.updateTextSpansSync(spans)
                        ))
                    }
                    .andThen(spanGateway.getDeletedTextSpans().firstOrError())
                    .flatMapCompletable { spanGateway.deleteTextSpansFromCloud(it).onErrorComplete() }
                    .andThen(spanGateway.getAllTextSpansFromCloud())
                    .flatMapCompletable { spanGateway.insertTextSpan(it) }
                    .onErrorResumeNext { error ->
                        error.printStackTrace()
                        Completable.error(SyncSpanException())
                    }
}