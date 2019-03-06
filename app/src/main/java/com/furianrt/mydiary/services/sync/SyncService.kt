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
        return Service.START_NOT_STICKY
    }

    override fun sendProgressUpdate(progressMessage: ProgressMessage) {
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
