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

import com.furianrt.mydiary.model.repository.span.SpanRepository
import io.reactivex.Completable
import javax.inject.Inject

class SyncNoteSpansUseCase @Inject constructor(
        private val spanRepository: SpanRepository
) {

    class SyncSpanException : Throwable()

    fun invoke(email: String): Completable =
            spanRepository.getAllTextSpans()
                    .first(emptyList())
                    .map { spans -> spans.filter { !it.isSync(email) } }
                    .map { spans -> spans.apply { forEach { it.syncWith.add(email) } } }
                    .flatMapCompletable { spans ->
                        Completable.concat(listOf(
                                spanRepository.saveTextSpansInCloud(spans),
                                spanRepository.updateTextSpansSync(spans)
                        ))
                    }
                    .andThen(spanRepository.getDeletedTextSpans().first(emptyList()))
                    .flatMapCompletable { spanRepository.deleteTextSpansFromCloud(it) }
                    .andThen(spanRepository.getAllTextSpansFromCloud())
                    .flatMapCompletable { spanRepository.insertTextSpan(it) }
                    .onErrorResumeNext { error ->
                        error.printStackTrace()
                        Completable.error(SyncSpanException())
                    }
}