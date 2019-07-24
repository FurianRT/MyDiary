package com.furianrt.mydiary.data.repository.appearance

import com.furianrt.mydiary.data.auth.AuthHelper
import com.furianrt.mydiary.data.cloud.CloudHelper
import com.furianrt.mydiary.data.database.NoteDatabase
import com.furianrt.mydiary.data.model.MyNoteAppearance
import com.furianrt.mydiary.data.prefs.PreferencesHelper
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.Single
import javax.inject.Inject

class AppearanceRepositoryImp @Inject constructor(
        private val database: NoteDatabase,
        private val prefs: PreferencesHelper,
        private val cloud: CloudHelper,
        private val auth: AuthHelper,
        private val rxScheduler: Scheduler
) : AppearanceRepository {

    override fun insertAppearance(appearance: MyNoteAppearance): Completable =
            database.appearanceDao().insert(appearance)
                    .subscribeOn(rxScheduler)

    override fun insertAppearance(appearances: List<MyNoteAppearance>): Completable =
            database.appearanceDao().insert(appearances)
                    .subscribeOn(rxScheduler)

    override fun updateAppearance(appearance: MyNoteAppearance): Completable =
            database.appearanceDao().update(appearance.apply { syncWith.clear() })
                    .andThen(database.noteDao().getNote(appearance.appearanceId))
                    .map { it.apply { it.syncWith.clear() } }
                    .flatMapCompletable { database.noteDao().update(it) }
                    .subscribeOn(rxScheduler)

    override fun updateAppearancesSync(appearances: List<MyNoteAppearance>): Completable =
            database.appearanceDao().update(appearances)
                    .subscribeOn(rxScheduler)

    override fun cleanupAppearances(): Completable =
            database.appearanceDao().cleanup()
                    .subscribeOn(rxScheduler)

    override fun getNoteAppearance(noteId: String): Flowable<MyNoteAppearance> =
            database.appearanceDao()
                    .getNoteAppearance(noteId)
                    .subscribeOn(rxScheduler)

    override fun getDeletedAppearances(): Flowable<List<MyNoteAppearance>> =
            database.appearanceDao()
                    .getDeletedAppearances()
                    .subscribeOn(rxScheduler)

    override fun getAllNoteAppearances(): Flowable<List<MyNoteAppearance>> =
            database.appearanceDao()
                    .getAllNoteAppearances()
                    .subscribeOn(rxScheduler)

    override fun saveAppearancesInCloud(appearances: List<MyNoteAppearance>): Completable =
            cloud.saveAppearances(appearances, auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun deleteAppearancesFromCloud(appearances: List<MyNoteAppearance>): Completable =
            cloud.deleteAppearances(appearances, auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun getAllAppearancesFromCloud(): Single<List<MyNoteAppearance>> =
            cloud.getAllAppearances(auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun getPrimaryColor(): Int = prefs.getPrimaryColor()

    override fun getAccentColor(): Int = prefs.getAccentColor()

    override fun getTextColor(): Int = prefs.getTextColor()

    override fun getSurfaceTextColor(): Int = prefs.getSurfaceTextColor()

    override fun getTextSize(): Int = prefs.getTextSize()

    override fun getNoteBackgroundColor(): Int = prefs.getNoteBackgroundColor()

    override fun getNoteTextBackgroundColor(): Int = prefs.getNoteTextBackgroundColor()
}