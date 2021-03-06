/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.model.gateway.span

import com.furianrt.mydiary.model.entity.MyTextSpan
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

interface SpanGateway {
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