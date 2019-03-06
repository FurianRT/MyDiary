package com.furianrt.mydiary.services

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.furianrt.mydiary.MyApp
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val NOTIFICATION_ID = 23
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)
        remoteMessage?.notification?.let { showNotification(it) }
    }

    private fun showNotification(notification: RemoteMessage.Notification) {
        val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationBuilder = NotificationCompat.Builder(this, MyApp.NOTIFICATION_FIREBASE_CHANNEL_ID)
        notificationBuilder.setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setStyle(NotificationCompat.BigTextStyle()
                        .setBigContentTitle(notification.title)
                        .bigText(notification.body))
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }
}