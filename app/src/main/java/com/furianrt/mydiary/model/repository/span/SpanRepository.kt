/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.model.repository.span

import com.furianrt.mydiary.model.entity.MyTextSpan
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface SpanRepository {
    fun insertTextSpan(textSpans: List<MyTextSpan>): Completable
    fun insertTextSpan(textSpan: MyTextSpan): Completable
    fun updateTextSpansSync(textSpans: List<MyTextSpan>): Completable
    fun deleteTextSpan(noteId: String): Completable
    fun deleteTextSpanPermanently(noteId: String): Completable
    fun deleteTextSpansFromCloud(textSpans: List<MyTextSpan>): Completable
    fun cleanupTextSpans(): Completable
    fun getDeletedTextSpans(): Flowable<List<MyTextSpan>>
    fun getAllTextSpansFromCloud(): Single<List<MyTextSpan>>
    fun getAllTextSpans(): Flowable<List<MyTextSpan>>
    fun getTextSpans(noteId: String): Flowable<List<MyTextSpan>>
    fun saveTextSpansInCloud(textSpans: List<MyTextSpan>): Completable
}