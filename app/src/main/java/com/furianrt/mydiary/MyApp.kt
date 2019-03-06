package com.furianrt.mydiary

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.furianrt.mydiary.di.application.AppComponent
import com.furianrt.mydiary.di.application.AppModule
import com.furianrt.mydiary.di.application.DaggerAppComponent
import com.furianrt.mydiary.general.MediaLoader
import com.yanzhenjie.album.Album
import com.yanzhenjie.album.AlbumConfig
import net.danlew.android.joda.JodaTimeAndroid
import java.util.*

class MyApp : Application() {

    companion object {
        const val NOTIFICATION_SYNC_CHANNEL_ID = "sync_channel"
        const val NOTIFICATION_SYNC_CHANNEL_NAME = "Synchronization"
        const val NOTIFICATION_FIREBASE_CHANNEL_ID = "firebase_channel"
        const val NOTIFICATION_FIREBASE_CHANNEL_NAME = "Info"
    }

    val component: AppComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()

    override fun onCreate() {
        super.onCreate()
        createNotificationSyncChannel()
        createNotificationFirebaseChannel()
        JodaTimeAndroid.init(this)
        Album.initialize(AlbumConfig.newBuilder(this)
                .setAlbumLoader(MediaLoader())
                .setLocale(Locale.getDefault())
                .build())
    }

    private fun createNotificationSyncChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                    NOTIFICATION_SYNC_CHANNEL_ID,
                    NOTIFICATION_SYNC_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW
            ).apply { setSound(null, null) }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun createNotificationFirebaseChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                    NOTIFICATION_FIREBASE_CHANNEL_ID,
                    NOTIFICATION_FIREBASE_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }
}

/*TODO
*
* редактор картинок
* диктор
* изменить цвет подсказки диалога
* запилить дравер лайаут с поиском
* выбор шрифта
*   измениение тегов
* реализовать выбор локации
* реклама
* улучшить дефолтные цвета
* добавить анимацию тени к липким заголовкам
* кликабельные итемы android:background="?android:selectableItemBackground"
* добавить подсказки для drag and prop и другого
* добавить notifications
* добавить appsFlyerEvents
* добавить настройки дейли картинки
* добавить настройки внешнего вида основного экрана
* полностью переделать дизайн главного экрана
* добавить анимацию ошибок аутентификации
* исправить ошибку соединения при регистрации
* добавить заглушку на пустые состояния списков
*
* */

