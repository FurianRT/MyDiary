/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

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

import com.furianrt.mydiary.BuildConfig
import com.furianrt.mydiary.data.model.MyTextSpan
import com.furianrt.mydiary.data.repository.device.DeviceRepository
import com.furianrt.mydiary.data.repository.span.SpanRepository
import com.furianrt.mydiary.di.application.component.AppScope
import com.furianrt.mydiary.utils.generateUniqueId
import io.reactivex.Completable
import javax.inject.Inject

@AppScope
class UpdateNoteSpansUseCase @Inject constructor(
        private val spanRepository: SpanRepository,
        private val deviceRepository: DeviceRepository
) {

    fun invoke(noteId: String, textSpans: List<MyTextSpan>) {
        if (deviceRepository.isItemPurchased(BuildConfig.ITEM_PREMIUM_SKU)) {
            spanRepository.deleteTextSpan(noteId)
                    .andThen(spanRepository.insertTextSpan(textSpans.map { span ->
                        span.apply {
                            span.id = generateUniqueId()
                            span.noteId = noteId
                        }
                    }))
                    .subscribe()
        } else {
            spanRepository.deleteTextSpanPermanently(noteId)
                    .andThen(spanRepository.insertTextSpan(textSpans.map { span ->
                        span.apply {
                            span.id = generateUniqueId()
                            span.noteId = noteId
                        }
                    }))
                    .subscribe()
        }
    }
}