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

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.furianrt.mydiary.MyApp
import com.furianrt.mydiary.R
import com.furianrt.mydiary.domain.check.IsPremiumPurchasedUseCase
import com.furianrt.mydiary.presentation.screens.main.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import javax.inject.Inject

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MessagingService : FirebaseMessagingService() {

    companion object {
        private const val NOTIFICATION_ID = 23
        private const val ARG_FOR_PREMIUM_USERS = "for_premium_users"
    }

    @Inject
    lateinit var isPremiumPurchasedUseCase: IsPremiumPurchasedUseCase

    override fun onCreate() {
        (applicationContext as MyApp)
                .appComponent
                .presenterComponent()
                .create()
                .inject(this)
        super.onCreate()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        remoteMessage.notification?.let { notification ->
            val forPremiumUsers = remoteMessage.data[ARG_FOR_PREMIUM_USERS]?.toIntOrNull()
            when {
                forPremiumUsers == null -> showNotification(notification)
                forPremiumUsers == 1 && isPremiumPurchasedUseCase() -> showNotification(notification)
                forPremiumUsers == 0 && !isPremiumPurchasedUseCase() -> showNotification(notification)
            }
        }
    }

    private fun showNotification(notification: RemoteMessage.Notification) {
        val pendingIntent =
                PendingIntent.getActivity(applicationContext, 0, newLauncherIntent(applicationContext), 0)
        val notificationStyle = NotificationCompat.BigTextStyle()
                .setBigContentTitle(notification.title)
                .bigText(notification.body)
        val resultNotification = NotificationCompat.Builder(applicationContext, getString(R.string.cloud_notification_channel_id))
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_notification_logo)
                .setContentIntent(pendingIntent)
                .setStyle(notificationStyle)
                .build()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager?
        notificationManager?.notify(NOTIFICATION_ID, resultNotification)
    }

    private fun newLauncherIntent(context: Context): Intent =
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                action = Intent.ACTION_MAIN
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
}