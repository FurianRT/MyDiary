/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.data.repository.span

import com.furianrt.mydiary.data.auth.AuthHelper
import com.furianrt.mydiary.data.cloud.CloudHelper
import com.furianrt.mydiary.data.database.SpanDao
import com.furianrt.mydiary.data.model.MyTextSpan
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.Single
import javax.inject.Inject

class SpanRepositoryImp @Inject constructor(
        private val spanDao: SpanDao,
        private val cloud: CloudHelper,
        private val auth: AuthHelper,
        private val rxScheduler: Scheduler
) : SpanRepository {

    override fun insertTextSpan(textSpans: List<MyTextSpan>): Completable =
            spanDao.insert(textSpans)
                    .subscribeOn(rxScheduler)

    override fun insertTextSpan(textSpan: MyTextSpan): Completable =
            spanDao.insert(textSpan)
                    .subscribeOn(rxScheduler)

    override fun updateTextSpansSync(textSpans: List<MyTextSpan>): Completable =
            spanDao.update(textSpans)
                    .subscribeOn(rxScheduler)

    override fun deleteTextSpan(noteId: String): Completable =
            spanDao.delete(noteId)
                    .subscribeOn(rxScheduler)

    override fun deleteTextSpanPermanently(noteId: String): Completable =
            spanDao.deletePermanently(noteId)
                    .subscribeOn(rxScheduler)

    override fun deleteTextSpansFromCloud(textSpans: List<MyTextSpan>): Completable =
            cloud.deleteTextSpans(textSpans, auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun cleanupTextSpans(): Completable =
            spanDao.cleanup()
                    .subscribeOn(rxScheduler)

    override fun getDeletedTextSpans(): Flowable<List<MyTextSpan>> =
            spanDao.getDeletedTextSpans()
                    .subscribeOn(rxScheduler)

    override fun getAllTextSpansFromCloud(): Single<List<MyTextSpan>> =
            cloud.getAllTextSpans(auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun getAllTextSpans(): Flowable<List<MyTextSpan>> =
            spanDao.getAllTextSpans()
                    .subscribeOn(rxScheduler)

    override fun saveTextSpansInCloud(textSpans: List<MyTextSpan>): Completable =
            cloud.saveTextSpans(textSpans, auth.getUserId())
                    .subscribeOn(rxScheduler)
}