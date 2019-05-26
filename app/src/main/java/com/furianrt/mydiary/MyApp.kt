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
        MobileAds.initialize(this, getString(R.string.banner_ad_unit_id))

        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
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
}

/*TODO
*
* выбор шрифта
* реклама
*   улучшить дефолтные цвета
* добавить notifications
* добавить настройки внешнего вида основного экрана
*   добавить настройки дейли картинки
*   добавить экран с описанием премиума
*   сделать действия для кнопок fab
*   добавить лайауты для горизантальной ориентации
*   сделать что-то с картинкой профиля и описанием
* изменить цвета у дефолтных категорий
* добавить ссылки на используемые библиотеки
*   написать туториал
* добавить анимацию открытия экранов
* добавить отправку картинок или текста из других приложений
* отпечаток пальца
*
* */

