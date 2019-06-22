package com.furianrt.mydiary

import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.*
import android.widget.ImageView
import androidx.preference.PreferenceManager
import com.furianrt.mydiary.data.prefs.PreferencesHelper
import com.furianrt.mydiary.di.application.AppComponent
import com.furianrt.mydiary.di.application.AppModule
import com.furianrt.mydiary.di.application.DaggerAppComponent
import com.furianrt.mydiary.general.GlideApp
import com.furianrt.mydiary.screens.pin.PinActivity
import com.google.android.gms.ads.MobileAds
import com.yanzhenjie.album.Album
import com.yanzhenjie.album.AlbumConfig
import com.yanzhenjie.album.AlbumFile
import com.yanzhenjie.album.AlbumLoader
import net.danlew.android.joda.JodaTimeAndroid
import org.joda.time.DateTime
import java.util.*

class MyApp : Application(), Application.ActivityLifecycleCallbacks {

    companion object {
        const val NOTIFICATION_SYNC_CHANNEL_ID = "sync_channel"
        const val NOTIFICATION_SYNC_CHANNEL_NAME = "Synchronization"
        const val NOTIFICATION_FIREBASE_CHANNEL_ID = "firebase_channel"
        const val NOTIFICATION_FIREBASE_CHANNEL_NAME = "Info"
        private const val SYNC_PROGRESS_RESET_TIME = 1000 * 60
    }

    val component: AppComponent by lazy {
        DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
    }

    private val mHandler = Handler(Looper.getMainLooper())
    private val mLogoutRunnable = Runnable { setAuthorized(false) }

    override fun onCreate() {
        super.onCreate()
        setAuthorized(false)
        registerActivityLifecycleCallbacks(this)
        createNotificationSyncChannel()
        createNotificationFirebaseChannel()
        JodaTimeAndroid.init(this)
        initializeImageAlbum()
        resetSyncProgress()
        incrementLaunchCounter()
        MobileAds.initialize(this, getString(R.string.banner_ad_app_id))

        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder().build())
    }

    override fun onActivityDestroyed(activity: Activity?) {}
    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {}
    override fun onActivityResumed(activity: Activity?) {}
    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        mHandler.removeCallbacks(mLogoutRunnable)
    }

    override fun onActivityStarted(activity: Activity?) {
        mHandler.removeCallbacks(mLogoutRunnable)
    }

    override fun onActivityPaused(activity: Activity?) {
        setAuthorized(true)
    }

    override fun onActivityStopped(activity: Activity?) {
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

    private fun resetSyncProgress() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val currentTime = DateTime.now().millis
        val launchTimeDiff = currentTime - prefs.getLong(PreferencesHelper.LAST_APP_LAUNCH_TIME, currentTime)
        if (launchTimeDiff >= SYNC_PROGRESS_RESET_TIME) {
            prefs.edit().putString(PreferencesHelper.LAST_PROGRESS_MESSAGE, "").apply()
        }
        prefs.edit().putLong(PreferencesHelper.LAST_APP_LAUNCH_TIME, currentTime).apply()
    }

    private fun incrementLaunchCounter() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        var launches = prefs.getInt(PreferencesHelper.NUMBER_OF_LAUNCHES, 0)
        prefs.edit().putInt(PreferencesHelper.NUMBER_OF_LAUNCHES, ++launches).apply()
    }
}

/*TODO
*
* выбор шрифта
*   добавить notifications
*   сделать напоминание - прочитать указанную заметку через указанный период
* добавить настройки внешнего вида основного экрана
* расширить настройки дейли картинки
* добавить лайауты для горизонтальной ориентации
* поддержать планшеты
* добавить ссылки на используемые библиотеки
* добавить анимацию открытия экранов
* добавить отправку картинок или текста из других приложений
* добавить вывод в pdf
* сделать ежедневный локальный бэкап
*   добавить выбор локации
* добавить выбор типа защиты
* поддержка темной темы
* добавить статистику по записям
* разбить экран настроек на категории
* добавить ссылку на гугл таблицы
*   добавить поиск по дате
* добавить счетчики к категориям, тегам и т.д
*   добавить поддержку ссылок внутри текста
* (?)добавить градиент на экран с паролем
* добавить иконци в настройках
*   сделать определение локации опциональным
*   уменьшить размер картинок
*   добавить предложение оценить приложение (в настройках)
* добавить сканирование текста
* добавить импорт текста с сайтов (как в EverNote)
* прикрутить ProGuard
* вынести модуль в git submodule
*
* */

