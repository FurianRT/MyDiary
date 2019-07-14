package com.furianrt.mydiary.view.services.sync

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.furianrt.mydiary.MyApp
import com.furianrt.mydiary.R
import com.furianrt.mydiary.view.base.BaseView
import com.furianrt.mydiary.data.model.SyncProgressMessage
import com.furianrt.mydiary.screens.main.MainActivity
import javax.inject.Inject

class SyncService : Service(), BaseView, SyncContract.MvpView {

    companion object {
        const val TAG = "SyncService"
        private const val FOREGROUND_ID = 1
        private const val EXTRA_PROGRESS_MESSAGE = "progress_message"

        fun getProgressMessage(intent: Intent?): SyncProgressMessage? =
                intent?.getParcelableExtra(EXTRA_PROGRESS_MESSAGE)
    }

    @Inject
    lateinit var mPresenter: SyncContract.Presenter

    override fun onCreate() {
        getPresenterComponent(this).inject(this)
        super.onCreate()
        mPresenter.attachView(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val pendingIntent =
                PendingIntent.getActivity(applicationContext, 0, newLauncherIntent(applicationContext), 0)
        val notification = NotificationCompat.Builder(applicationContext, MyApp.NOTIFICATION_SYNC_CHANNEL_ID)
                .setContentTitle(getString(R.string.notification_sync_title))
                .setContentText(getString(R.string.notification_sync_content))
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_cloud_upload)
                .build()
        startForeground(FOREGROUND_ID, notification)
        mPresenter.onStartCommand()
        return START_NOT_STICKY
    }

    private fun newLauncherIntent(context: Context): Intent =
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                action = Intent.ACTION_MAIN
                addCategory(Intent.CATEGORY_LAUNCHER)
            }

    override fun sendProgressUpdate(progressMessage: SyncProgressMessage) {
        progressMessage.message = if (progressMessage.hasError) {
            when (progressMessage.taskIndex) {
                SyncProgressMessage.PROFILE_CHECK -> getString(R.string.sync_error_profile)
                SyncProgressMessage.SYNC_NOTES -> getString(R.string.sync_error_notes)
                SyncProgressMessage.SYNC_APPEARANCE -> getString(R.string.sync_error_appearance)
                SyncProgressMessage.SYNC_CATEGORIES -> getString(R.string.sync_error_categories)
                SyncProgressMessage.SYNC_TAGS -> getString(R.string.sync_error_tags)
                SyncProgressMessage.SYNC_NOTE_TAGS -> getString(R.string.sync_error_note_tags)
                SyncProgressMessage.SYNC_LOCATION -> getString(R.string.sync_error_locations)
                SyncProgressMessage.SYNC_NOTE_LOCATIONS -> getString(R.string.sync_error_note_locations)
                SyncProgressMessage.SYNC_FORECAST -> getString(R.string.sync_error_forecasts)
                SyncProgressMessage.SYNC_IMAGES -> getString(R.string.sync_error_images)
                SyncProgressMessage.CLEANUP -> getString(R.string.sync_error_cleanup)
                else -> getString(R.string.sync_error)
            }
        } else {
            when (progressMessage.taskIndex) {
                SyncProgressMessage.PROFILE_CHECK -> getString(R.string.sync_profile_check)
                SyncProgressMessage.SYNC_NOTES -> getString(R.string.sync_notes)
                SyncProgressMessage.SYNC_APPEARANCE -> getString(R.string.sync_appearance)
                SyncProgressMessage.SYNC_CATEGORIES -> getString(R.string.sync_categories)
                SyncProgressMessage.SYNC_TAGS -> getString(R.string.sync_tags)
                SyncProgressMessage.SYNC_NOTE_TAGS -> getString(R.string.sync_note_tags)
                SyncProgressMessage.SYNC_LOCATION -> getString(R.string.sync_locations)
                SyncProgressMessage.SYNC_NOTE_LOCATIONS -> getString(R.string.sync_note_locations)
                SyncProgressMessage.SYNC_FORECAST -> getString(R.string.sync_forecasts)
                SyncProgressMessage.SYNC_IMAGES -> getString(R.string.sync_images)
                SyncProgressMessage.CLEANUP -> getString(R.string.sync_cleanup)
                SyncProgressMessage.SYNC_FINISHED -> getString(R.string.sync_done)
                else -> ""
            }
        }
        LocalBroadcastManager.getInstance(applicationContext)
                .sendBroadcast(Intent().apply {
                    putExtra(EXTRA_PROGRESS_MESSAGE, progressMessage)
                    action = Intent.ACTION_SYNC
                })
    }

    override fun close() {
        Log.e(TAG, "service closed")
        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.detachView()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
