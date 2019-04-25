package com.furianrt.mydiary.services.sync

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.furianrt.mydiary.MyApp
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.ProgressMessage
import com.furianrt.mydiary.main.MainActivity
import javax.inject.Inject

class SyncService : Service(), SyncContract.View {

    companion object {
        const val TAG = "SyncService"
        private const val FOREGROUND_ID = 1
        const val EXTRA_PROGRESS_MESSAGE = "progress_message"
    }

    @Inject
    lateinit var mPresenter: SyncContract.Presenter

    override fun onCreate() {
        getPresenterComponent(this).inject(this)
        super.onCreate()
        mPresenter.attachView(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        val notification = NotificationCompat.Builder(this, MyApp.NOTIFICATION_SYNC_CHANNEL_ID)
                .setContentTitle(getString(R.string.notification_sync_title))
                .setContentText(getString(R.string.notification_sync_content))
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_cloud_upload)
                .build()
        startForeground(FOREGROUND_ID, notification)
        mPresenter.onStartCommand()
        return START_NOT_STICKY
    }

    override fun sendProgressUpdate(progressMessage: ProgressMessage) {
        progressMessage.message = if (progressMessage.hasError) {
            when (progressMessage.taskIndex) {
                ProgressMessage.PROFILE_CHECK -> getString(R.string.sync_error_profile)
                ProgressMessage.SYNC_NOTES -> getString(R.string.sync_error_notes)
                ProgressMessage.SYNC_APPEARANCE -> getString(R.string.sync_error_appearance)
                ProgressMessage.SYNC_CATEGORIES -> getString(R.string.sync_error_categories)
                ProgressMessage.SYNC_TAGS -> getString(R.string.sync_error_tags)
                ProgressMessage.SYNC_NOTE_TAGS -> getString(R.string.sync_error_note_tags)
                ProgressMessage.SYNC_IMAGES -> getString(R.string.sync_error_images)
                ProgressMessage.CLEANUP -> getString(R.string.sync_error_cleanup)
                else -> getString(R.string.sync_error)
            }
        } else {
            when (progressMessage.taskIndex) {
                ProgressMessage.PROFILE_CHECK -> getString(R.string.sync_profile_check)
                ProgressMessage.SYNC_NOTES -> getString(R.string.sync_notes)
                ProgressMessage.SYNC_APPEARANCE -> getString(R.string.sync_appearance)
                ProgressMessage.SYNC_CATEGORIES -> getString(R.string.sync_categories)
                ProgressMessage.SYNC_TAGS -> getString(R.string.sync_tags)
                ProgressMessage.SYNC_NOTE_TAGS -> getString(R.string.sync_note_tags)
                ProgressMessage.SYNC_IMAGES -> getString(R.string.sync_images)
                ProgressMessage.CLEANUP -> getString(R.string.sync_cleanup)
                ProgressMessage.SYNC_FINISHED -> getString(R.string.sync_done)
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
