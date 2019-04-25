package com.furianrt.mydiary

import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.preference.PreferenceManager
import com.furianrt.mydiary.data.prefs.PreferencesHelper
import com.furianrt.mydiary.di.application.AppComponent
import com.furianrt.mydiary.di.application.AppModule
import com.furianrt.mydiary.di.application.DaggerAppComponent
import com.furianrt.mydiary.general.GlideApp
import com.furianrt.mydiary.pin.PinActivity
import com.yanzhenjie.album.Album
import com.yanzhenjie.album.AlbumConfig
import com.yanzhenjie.album.AlbumFile
import com.yanzhenjie.album.AlbumLoader
import net.danlew.android.joda.JodaTimeAndroid
import java.util.*

class MyApp : Application(), Application.ActivityLifecycleCallbacks {

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

    private val mHandler = Handler(Looper.getMainLooper())
    private val mLogoutRunnable = Runnable { setAuthorized(false) }

    override fun onCreate() {
        component.inject(this)
        super.onCreate()
        setAuthorized(false)
        registerActivityLifecycleCallbacks(this)
        createNotificationSyncChannel()
        createNotificationFirebaseChannel()
        JodaTimeAndroid.init(this)
        initializeImageAlbum()
    }

    override fun onActivityDestroyed(activity: Activity?) {}
    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {}
    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity?) {}
    override fun onActivityStopped(activity: Activity?) {}
    override fun onActivityResumed(activity: Activity?) {
        mHandler.removeCallbacks(mLogoutRunnable)
    }

    override fun onActivityPaused(activity: Activity?) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val isPasswordEnabled = prefs.getBoolean(PreferencesHelper.SECURITY_KEY, false)
        if (isPasswordEnabled && activity !is PinActivity) {
            val delay = prefs.getString(
                    PreferencesHelper.SECURITY_REQUEST_DELAY,
                    PreferencesHelper.DEFAULT_PIN_DELAY
            )!!.toLong()
            mHandler.postDelayed(mLogoutRunnable, delay)
        }
    }

    private fun setAuthorized(authorized: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putBoolean(PreferencesHelper.SECURITY_IS_AUTHORIZED, authorized)
                .apply()
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
* изменить цвет подсказки диалога
* запилить дравер лайаут с поиском
* выбор шрифта
*   измениение тегов
*   поиск тэгов
* реализовать выбор локации
* реклама
* улучшить дефолтные цвета
* добавить анимацию тени к липким заголовкам
* кликабельные итемы android:background="?android:selectableItemBackground"
* добавить notifications
* добавить appsFlyerEvents
* добавить настройки дейли картинки
* добавить настройки внешнего вида основного экрана
* исправить ошибку соединения при регистрации
* реализовать свайп элементов листа главного экрана
* добавить экран с описанием премиума
* изменить цвета у дефолтных категорий
* отрефакторить создание интента для активити
*
* */

