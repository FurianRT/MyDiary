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
import com.furianrt.mydiary.model.source.auth.AuthSource
import com.furianrt.mydiary.model.source.cloud.CloudSource
import com.furianrt.mydiary.model.source.database.dao.SpanDao
import com.furianrt.mydiary.utils.MyRxUtils
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class SpanGatewayImp @Inject constructor(
        private val spanDao: SpanDao,
        private val cloud: CloudSource,
        private val auth: AuthSource,
        private val scheduler: MyRxUtils.BaseSchedulerProvider
) : SpanGateway {

    override fun insertTextSpan(textSpans: List<MyTextSpan>): Completable =
            spanDao.insert(textSpans)
                    .subscribeOn(scheduler.io())

    override fun insertTextSpan(textSpan: MyTextSpan): Completable =
            spanDao.insert(textSpan)
                    .subscribeOn(scheduler.io())

    override fun updateTextSpansSync(textSpans: List<MyTextSpan>): Completable =
            spanDao.update(textSpans)
                    .subscribeOn(scheduler.io())

    override fun deleteTextSpan(noteId: String): Completable =
            spanDao.delete(noteId)
                    .subscribeOn(scheduler.io())

    override fun deleteTextSpanPermanently(noteId: String): Completable =
            spanDao.deletePermanently(noteId)
                    .subscribeOn(scheduler.io())

    override fun deleteTextSpansFromCloud(textSpans: List<MyTextSpan>): Completable =
            cloud.deleteTextSpans(textSpans, auth.getUserId())
                    .subscribeOn(scheduler.io())

    override fun cleanupTextSpans(): Completable =
            spanDao.cleanup()
                    .subscribeOn(scheduler.io())

    override fun getDeletedTextSpans(): Flowable<List<MyTextSpan>> =
            spanDao.getDeletedTextSpans()
                    .subscribeOn(scheduler.io())

    override fun getAllTextSpansFromCloud(): Single<List<MyTextSpan>> =
            cloud.getAllTextSpans(auth.getUserId())
                    .subscribeOn(scheduler.io())

    override fun getAllTextSpans(): Flowable<List<MyTextSpan>> =
            spanDao.getAllTextSpans()
                    .subscribeOn(scheduler.io())

    override fun getTextSpans(noteId: String): Flowable<List<MyTextSpan>> =
            spanDao.getTextSpans(noteId)
                    .subscribeOn(scheduler.io())

    override fun saveTextSpansInCloud(textSpans: List<MyTextSpan>): Completable =
            cloud.saveTextSpans(textSpans, auth.getUserId())
                    .subscribeOn(scheduler.io())
}