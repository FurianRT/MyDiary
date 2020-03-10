/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.model.gateway.appearance

import com.furianrt.mydiary.model.entity.MyNoteAppearance
import com.furianrt.mydiary.model.source.auth.AuthSource
import com.furianrt.mydiary.model.source.cloud.CloudSource
import com.furianrt.mydiary.model.source.database.dao.AppearanceDao
import com.furianrt.mydiary.model.source.preferences.PreferencesSource
import com.furianrt.mydiary.utils.MyRxUtils
import com.google.common.base.Optional
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

class AppearanceGatewayImp @Inject constructor(
        private val appearanceDao: AppearanceDao,
        private val prefs: PreferencesSource,
        private val cloud: CloudSource,
        private val auth: AuthSource,
        private val scheduler: MyRxUtils.BaseSchedulerProvider
) : AppearanceGateway {

    override fun insertAppearance(appearance: MyNoteAppearance): Completable =
            appearanceDao.insert(appearance)
                    .subscribeOn(scheduler.io())

    override fun insertAppearance(appearances: List<MyNoteAppearance>): Completable =
            appearanceDao.insert(appearances)
                    .subscribeOn(scheduler.io())

    override fun updateAppearance(appearance: MyNoteAppearance): Completable =
            appearanceDao.update(appearance)
                    .subscribeOn(scheduler.io())

    override fun updateAppearancesSync(appearances: List<MyNoteAppearance>): Completable =
            appearanceDao.update(appearances)
                    .subscribeOn(scheduler.io())

    override fun deleteAppearance(noteId: String): Completable =
            appearanceDao.delete(noteId)
                    .subscribeOn(scheduler.io())

    override fun cleanupAppearances(): Completable =
            appearanceDao.cleanup()
                    .subscribeOn(scheduler.io())

    override fun getNoteAppearance(noteId: String): Flowable<Optional<MyNoteAppearance>> =
            appearanceDao.getNoteAppearance(noteId)
                    .map { Optional.fromNullable(it.firstOrNull()) }
                    .subscribeOn(scheduler.io())

    override fun getDeletedAppearances(): Flowable<List<MyNoteAppearance>> =
            appearanceDao.getDeletedAppearances()
                    .subscribeOn(scheduler.io())

    override fun getAllNoteAppearances(): Flowable<List<MyNoteAppearance>> =
            appearanceDao.getAllNoteAppearances()
                    .subscribeOn(scheduler.io())

    override fun saveAppearancesInCloud(appearances: List<MyNoteAppearance>): Completable =
            cloud.saveAppearances(appearances, auth.getUserId())
                    .subscribeOn(scheduler.io())

    override fun deleteAppearancesFromCloud(appearances: List<MyNoteAppearance>): Completable =
            cloud.deleteAppearances(appearances, auth.getUserId())
                    .subscribeOn(scheduler.io())

    override fun getAllAppearancesFromCloud(): Single<List<MyNoteAppearance>> =
            cloud.getAllAppearances(auth.getUserId())
                    .subscribeOn(scheduler.io())

    override fun getPrimaryColor(): Int = prefs.getPrimaryColor()

    override fun getAccentColor(): Int = prefs.getAccentColor()

    override fun getTextColor(): Int = prefs.getTextColor()

    override fun setTextColor(color: Int) {
        prefs.setTextColor(color)
    }

    override fun getSurfaceTextColor(): Int = prefs.getSurfaceTextColor()

    override fun setSurfaceTextColor(color: Int) {
        prefs.setSurfaceTextColor(color)
    }

    override fun getTextSize(): Int = prefs.getTextSize()

    override fun setTextSize(size: Int) {
        prefs.setTextSize(size)
    }

    override fun getNoteBackgroundColor(): Int = prefs.getNoteBackgroundColor()

    override fun setNoteBackgroundColor(color: Int) {
        prefs.setNoteBackgroundColor(color)
    }

    override fun getNoteTextBackgroundColor(): Int = prefs.getNoteTextBackgroundColor()

    override fun setNoteTextBackgroundColor(color: Int) {
        prefs.setNoteTextBackgroundColor(color)
    }
}