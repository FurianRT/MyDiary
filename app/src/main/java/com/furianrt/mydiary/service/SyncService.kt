package com.furianrt.mydiary.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.furianrt.mydiary.MyApp
import com.furianrt.mydiary.main.MainActivity
import javax.inject.Inject

class SyncService : Service(), SyncContract.View {

    companion object {
        const val TAG = "SyncService"
        private const val FOREGROUNG_ID = 1
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
        val notification = NotificationCompat.Builder(this, MyApp.NOTIFICATION_CHANNEL_ID)
                .setContentText("Test Title")
                .setContentText("Test, Text")
                .setContentIntent(pendingIntent)
                .build()
        startForeground(FOREGROUNG_ID, notification)
        mPresenter.onStartCommand()
        return Service.START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.detachView()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
