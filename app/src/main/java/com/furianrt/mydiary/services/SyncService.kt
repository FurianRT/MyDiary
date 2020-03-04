/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.services

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.furianrt.mydiary.MyApp
import com.furianrt.mydiary.R
import com.furianrt.mydiary.di.presenter.modules.presenter.PresenterContextModule
import com.furianrt.mydiary.domain.get.GetProfileUseCase
import com.furianrt.mydiary.domain.save.SetLastSyncMessageUseCase
import com.furianrt.mydiary.domain.sync.*
import com.furianrt.mydiary.domain.update.UpdateProfileUseCase
import com.furianrt.mydiary.model.entity.SyncProgressMessage
import com.furianrt.mydiary.presentation.screens.main.MainActivity
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class SyncService : Service() {

    companion object {
        private const val FOREGROUND_ID = 1
        private const val EXTRA_PROGRESS_MESSAGE = "progress_message"
        private const val PENDING_INTENT_REQUEST_CODE = 0

        private const val PROGRESS_STARTED = 0
        private const val PROGRESS_CATEGORIES = 10
        private const val PROGRESS_TAGS = 20
        private const val PROGRESS_LOCATION = 30
        private const val PROGRESS_FORECAST = 40
        private const val PROGRESS_IMAGES = 65
        private const val PROGRESS_APPEARANCE = 75
        private const val PROGRESS_SPANS = 80
        private const val PROGRESS_NOTES = 90
        private const val PROGRESS_CLEANUP = 100
        private const val PROGRESS_FINISHED = 100

        fun getProgressMessage(intent: Intent?): SyncProgressMessage? =
                intent?.getParcelableExtra(EXTRA_PROGRESS_MESSAGE)
    }

    @Inject
    lateinit var getProfileUseCase: GetProfileUseCase
    @Inject
    lateinit var updateProfileUseCase: UpdateProfileUseCase
    @Inject
    lateinit var syncNotesUseCase: SyncNotesUseCase
    @Inject
    lateinit var syncAppearanceUseCase: SyncAppearanceUseCase
    @Inject
    lateinit var syncCategoriesUseCase: SyncCategoriesUseCase
    @Inject
    lateinit var syncTagsUseCase: SyncTagsUseCase
    @Inject
    lateinit var syncLocationsUseCase: SyncLocationsUseCase
    @Inject
    lateinit var syncForecastUseCase: SyncForecastUseCase
    @Inject
    lateinit var syncImagesUseCase: SyncImagesUseCase
    @Inject
    lateinit var syncCleanupUseCase: SyncCleanupUseCase
    @Inject
    lateinit var syncNoteSpansUseCase: SyncNoteSpansUseCase
    @Inject
    lateinit var setLastSyncMessageUseCase: SetLastSyncMessageUseCase

    private val mCompositeDisposable = CompositeDisposable()

    override fun onCreate() {
        (applicationContext as MyApp)
                .component
                .newPresenterComponent(PresenterContextModule(baseContext))
                .inject(this)
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val pendingIntent = PendingIntent.getActivity(
                applicationContext,
                PENDING_INTENT_REQUEST_CODE,
                MainActivity.getLauncherIntent(applicationContext),
                PendingIntent.FLAG_CANCEL_CURRENT
        )
        startForeground(FOREGROUND_ID, createNotification(pendingIntent))
        startSync()
        return START_NOT_STICKY
    }

    private fun startSync() {
        sendProgressUpdate(SyncProgressMessage.SYNC_STARTED, PROGRESS_STARTED)
        mCompositeDisposable.add(getProfileUseCase()
                .map { it.email }
                .firstOrError()
                .flatMapPublisher { email ->
                    Single.concat(listOf(
                            syncCategoriesUseCase(email).toSingleDefault(SyncProgressMessage.SYNC_CATEGORIES),
                            syncTagsUseCase(email).toSingleDefault(SyncProgressMessage.SYNC_TAGS),
                            syncLocationsUseCase(email).toSingleDefault(SyncProgressMessage.SYNC_LOCATION),
                            syncForecastUseCase(email).toSingleDefault(SyncProgressMessage.SYNC_FORECAST),
                            syncImagesUseCase(email).toSingleDefault(SyncProgressMessage.SYNC_IMAGES),
                            syncAppearanceUseCase(email).toSingleDefault(SyncProgressMessage.SYNC_APPEARANCE),
                            syncNoteSpansUseCase(email).toSingleDefault(SyncProgressMessage.SYNC_SPANS),
                            syncNotesUseCase(email).toSingleDefault(SyncProgressMessage.SYNC_NOTES),
                            syncCleanupUseCase().toSingleDefault(SyncProgressMessage.CLEANUP)
                    ))
                }
                .subscribe({ taskIndex ->
                    handleSyncProgress(taskIndex)
                }, { error ->
                    handleSyncError(error)
                }, {
                    handleSyncComplete()
                }))
    }

    private fun handleSyncComplete() {
        mCompositeDisposable.add(updateProfileUseCase(System.currentTimeMillis())
                .subscribe {
                    sendProgressUpdate(SyncProgressMessage.SYNC_FINISHED, PROGRESS_FINISHED)
                    close()
                })
    }

    private fun handleSyncProgress(taskIndex: Int) {
        val progress = when (taskIndex) {
            SyncProgressMessage.SYNC_NOTES -> PROGRESS_NOTES
            SyncProgressMessage.SYNC_APPEARANCE -> PROGRESS_APPEARANCE
            SyncProgressMessage.SYNC_CATEGORIES -> PROGRESS_CATEGORIES
            SyncProgressMessage.SYNC_TAGS -> PROGRESS_TAGS
            SyncProgressMessage.SYNC_LOCATION -> PROGRESS_LOCATION
            SyncProgressMessage.SYNC_FORECAST -> PROGRESS_FORECAST
            SyncProgressMessage.SYNC_IMAGES -> PROGRESS_IMAGES
            SyncProgressMessage.SYNC_SPANS -> PROGRESS_SPANS
            SyncProgressMessage.CLEANUP -> PROGRESS_CLEANUP
            else -> throw IllegalStateException()
        }
        sendProgressUpdate(taskIndex, progress)
    }

    private fun handleSyncError(error: Throwable) {
        error.printStackTrace()
        val progressMessage = when (error) {
            is SyncNotesUseCase.SyncNotesException ->
                SyncProgressMessage(task = SyncProgressMessage.SYNC_NOTES, hasError = true)
            is SyncAppearanceUseCase.SyncAppearanceException ->
                SyncProgressMessage(task = SyncProgressMessage.SYNC_APPEARANCE, hasError = true)
            is SyncCategoriesUseCase.SyncCategoriesException ->
                SyncProgressMessage(task = SyncProgressMessage.SYNC_CATEGORIES, hasError = true)
            is SyncTagsUseCase.SyncTagsException ->
                SyncProgressMessage(task = SyncProgressMessage.SYNC_TAGS, hasError = true)
            is SyncTagsUseCase.SyncNoteTagsException ->
                SyncProgressMessage(task = SyncProgressMessage.SYNC_NOTE_TAGS, hasError = true)
            is SyncLocationsUseCase.SyncLocationsException ->
                SyncProgressMessage(task = SyncProgressMessage.SYNC_LOCATION, hasError = true)
            is SyncLocationsUseCase.SyncNoteLocationsException ->
                SyncProgressMessage(task = SyncProgressMessage.SYNC_NOTE_LOCATIONS, hasError = true)
            is SyncForecastUseCase.SyncForecastsException ->
                SyncProgressMessage(task = SyncProgressMessage.SYNC_FORECAST, hasError = true)
            is SyncImagesUseCase.SyncImagesException ->
                SyncProgressMessage(task = SyncProgressMessage.SYNC_IMAGES, hasError = true)
            is SyncNoteSpansUseCase.SyncSpanException ->
                SyncProgressMessage(task = SyncProgressMessage.SYNC_SPANS, hasError = true)
            is SyncCleanupUseCase.SyncCleanupException ->
                SyncProgressMessage(task = SyncProgressMessage.CLEANUP, hasError = true)
            else -> SyncProgressMessage(task = SyncProgressMessage.UNKNOWN, hasError = true)
        }
        sendProgressUpdate(progressMessage)
        close()
    }

    private fun createNotification(pendingIntent: PendingIntent): Notification =
            NotificationCompat.Builder(applicationContext, MyApp.NOTIFICATION_SYNC_CHANNEL_ID)
                    .setContentTitle(getString(R.string.notification_sync_title))
                    .setContentText(getString(R.string.notification_sync_content))
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_cloud_upload)
                    .build()

    private fun sendProgressUpdate(progressMessage: SyncProgressMessage) {
        progressMessage.message = if (progressMessage.hasError) {
            getSyncErrorTextMessage(progressMessage.task)
        } else {
            getSyncTextMessage(progressMessage.task)
        }
        sendBroadcast(progressMessage)
    }

    private fun sendProgressUpdate(taskIndex: Int, progress: Int) {
        val progressMessage = SyncProgressMessage(taskIndex, progress)
        sendProgressUpdate(progressMessage)
        setLastSyncMessageUseCase(progressMessage)
    }

    private fun getSyncErrorTextMessage(taskIndex: Int) = when (taskIndex) {
        SyncProgressMessage.SYNC_NOTES -> getString(R.string.sync_error_notes)
        SyncProgressMessage.SYNC_APPEARANCE -> getString(R.string.sync_error_appearance)
        SyncProgressMessage.SYNC_CATEGORIES -> getString(R.string.sync_error_categories)
        SyncProgressMessage.SYNC_TAGS -> getString(R.string.sync_error_tags)
        SyncProgressMessage.SYNC_NOTE_TAGS -> getString(R.string.sync_error_note_tags)
        SyncProgressMessage.SYNC_LOCATION -> getString(R.string.sync_error_locations)
        SyncProgressMessage.SYNC_NOTE_LOCATIONS -> getString(R.string.sync_error_note_locations)
        SyncProgressMessage.SYNC_FORECAST -> getString(R.string.sync_error_forecasts)
        SyncProgressMessage.SYNC_IMAGES -> getString(R.string.sync_error_images)
        SyncProgressMessage.SYNC_SPANS -> getString(R.string.sync_error_notes)
        SyncProgressMessage.CLEANUP -> getString(R.string.sync_error_cleanup)
        else -> getString(R.string.sync_error)
    }

    private fun getSyncTextMessage(taskIndex: Int) = when (taskIndex + 1) {
        SyncProgressMessage.SYNC_NOTES -> getString(R.string.sync_notes)
        SyncProgressMessage.SYNC_APPEARANCE -> getString(R.string.sync_appearance)
        SyncProgressMessage.SYNC_CATEGORIES -> getString(R.string.sync_categories)
        SyncProgressMessage.SYNC_TAGS -> getString(R.string.sync_tags)
        SyncProgressMessage.SYNC_NOTE_TAGS -> getString(R.string.sync_tags)
        SyncProgressMessage.SYNC_LOCATION -> getString(R.string.sync_locations)
        SyncProgressMessage.SYNC_NOTE_LOCATIONS -> getString(R.string.sync_locations)
        SyncProgressMessage.SYNC_FORECAST -> getString(R.string.sync_forecasts)
        SyncProgressMessage.SYNC_IMAGES -> getString(R.string.sync_images)
        SyncProgressMessage.SYNC_SPANS -> getString(R.string.sync_notes)
        SyncProgressMessage.CLEANUP -> getString(R.string.sync_cleanup)
        else -> getString(R.string.sync_done)
    }

    private fun sendBroadcast(progressMessage: SyncProgressMessage) {
        LocalBroadcastManager.getInstance(applicationContext)
                .sendBroadcast(Intent(Intent.ACTION_SYNC).apply {
                    putExtra(EXTRA_PROGRESS_MESSAGE, progressMessage)
                })
    }

    private fun close() {
        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable.clear()
        setLastSyncMessageUseCase(SyncProgressMessage(SyncProgressMessage.SYNC_FINISHED))
    }

    override fun onBind(intent: Intent?): IBinder? = null
}