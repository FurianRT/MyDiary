package com.furianrt.mydiary

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.widget.ImageView
import com.furianrt.mydiary.di.application.AppComponent
import com.furianrt.mydiary.di.application.AppModule
import com.furianrt.mydiary.di.application.DaggerAppComponent
import com.furianrt.mydiary.general.GlideApp
import com.yanzhenjie.album.Album
import com.yanzhenjie.album.AlbumConfig
import com.yanzhenjie.album.AlbumFile
import com.yanzhenjie.album.AlbumLoader
import net.danlew.android.joda.JodaTimeAndroid
import java.util.*

class MyApp : Application() {

    companion object {
        const val NOTIFICATION_SYNC_CHANNEL_ID = "sync_channel"
        const val NOTIFICATION_SYNC_CHANNEL_NAME = "Synchronization"
        const val NOTIFICATION_FIREBASE_CHANNEL_ID = "firebase_channel"
        const val NOTIFICATION_FIREBASE_CHANNEL_NAME = "Info"
    }

    val component: AppComponent by lazy {
        DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationSyncChannel()
        createNotificationFirebaseChannel()
        JodaTimeAndroid.init(this)
        initializeImageAlbum()
    }

    private fun createNotificationSyncChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getSystemService(NotificationManager::class.java)
                    .createNotificationChannel(NotificationChannel(
                            NOTIFICATION_SYNC_CHANNEL_ID,
                            NOTIFICATION_SYNC_CHANNEL_NAME,
                            NotificationManager.IMPORTANCE_LOW
                    ).apply {
                        setSound(null, null)
                    })
        }
    }

    private fun createNotificationFirebaseChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getSystemService(NotificationManager::class.java)
                    .createNotificationChannel(NotificationChannel(
                            NOTIFICATION_FIREBASE_CHANNEL_ID,
                            NOTIFICATION_FIREBASE_CHANNEL_NAME,
                            NotificationManager.IMPORTANCE_DEFAULT
                    ))
        }
    }

    private fun initializeImageAlbum() {
        Album.initialize(AlbumConfig.newBuilder(this)
                .setAlbumLoader(object : AlbumLoader {
                    override fun load(imageView: ImageView?, albumFile: AlbumFile?) {
                        load(imageView, albumFile?.path)
                    }

                    override fun load(imageView: ImageView?, url: String?) {
                        if (imageView != null && !url.isNullOrBlank()) {
                            GlideApp.with(imageView.context)
                                    .load(url)
                                    .placeholder(R.drawable.ic_image)
                                    .into(imageView)
                        }
                    }
                })
                .setLocale(Locale.getDefault())
                .build())
    }
}

/*TODO
*
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
* исправить ошибку соединения при регистрации
* добавить заглушку на пустые состояния списков
* реализовать свайп элементов листа главного экрана
* добавить экран с описанием према
* добавить возможность отмены изменений
*
* */

