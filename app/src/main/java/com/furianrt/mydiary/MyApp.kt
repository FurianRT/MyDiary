package com.furianrt.mydiary

import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.*
import android.widget.ImageView
import com.facebook.stetho.Stetho
import com.furianrt.mydiary.di.application.component.AppComponent
import com.furianrt.mydiary.di.application.component.DaggerAppComponent
import com.furianrt.mydiary.di.application.modules.app.AppContextModule
import com.furianrt.mydiary.di.application.modules.data.DatabaseModule
import com.furianrt.mydiary.di.application.modules.network.ApiModule
import com.furianrt.mydiary.di.application.modules.network.FirebaseModule
import com.furianrt.mydiary.di.application.modules.rx.RxModule
import com.furianrt.mydiary.domain.auth.AuthorizeUseCase
import com.furianrt.mydiary.domain.IncrementLaunchCountUseCase
import com.furianrt.mydiary.domain.check.IsPinEnabledUseCase
import com.furianrt.mydiary.domain.get.GetPinRequestDelayUseCase
import com.furianrt.mydiary.domain.save.ResetSyncProgressUseCase
import com.furianrt.mydiary.view.general.GlideApp
import com.furianrt.mydiary.view.screens.main.MainActivity
import com.furianrt.mydiary.view.screens.pin.PinActivity
import com.google.android.gms.ads.MobileAds
import com.jakewharton.threetenabp.AndroidThreeTen
import com.yanzhenjie.album.Album
import com.yanzhenjie.album.AlbumConfig
import com.yanzhenjie.album.AlbumFile
import com.yanzhenjie.album.AlbumLoader
import net.danlew.android.joda.JodaTimeAndroid
import java.util.Locale
import javax.inject.Inject

class MyApp : Application(), Application.ActivityLifecycleCallbacks {

    companion object {
        const val NOTIFICATION_SYNC_CHANNEL_ID = "sync_channel"
        const val NOTIFICATION_SYNC_CHANNEL_NAME = "Synchronization"
        const val NOTIFICATION_FIREBASE_CHANNEL_ID = "firebase_channel"
        const val NOTIFICATION_FIREBASE_CHANNEL_NAME = "Info"
    }

    val component: AppComponent by lazy {
        DaggerAppComponent.builder()
                .appContextModule(AppContextModule(this))
                .apiModule(ApiModule())
                .firebaseModule(FirebaseModule())
                .databaseModule(DatabaseModule())
                .rxModule(RxModule())
                .build()
    }

    @Inject
    lateinit var authorize: AuthorizeUseCase

    @Inject
    lateinit var isPinEnabled: IsPinEnabledUseCase

    @Inject
    lateinit var getPinRequestDelay: GetPinRequestDelayUseCase

    @Inject
    lateinit var incrementLaunchCount: IncrementLaunchCountUseCase

    @Inject
    lateinit var resetSyncProgress: ResetSyncProgressUseCase

    private val mHandler = Handler(Looper.getMainLooper())
    private val mLogoutRunnable = Runnable { setAuthorized(false) }

    override fun onCreate() {
        component.inject(this)
        super.onCreate()
        JodaTimeAndroid.init(this)
        setAuthorized(false)
        registerActivityLifecycleCallbacks(this)
        createNotificationSyncChannel()
        createNotificationFirebaseChannel()
        initializeImageAlbum()
        MobileAds.initialize(this, getString(R.string.banner_ad_app_id))
        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder().build())
        AndroidThreeTen.init(this)
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }
        incrementLaunchCount.invoke()
        resetSyncProgress.invoke()
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
        activity?.run {
            if (isFinishing) {
                if (this is PinActivity) {
                    overridePendingTransition(R.anim.activity_stay_slide_bottom, R.anim.slide_bottom_down)
                } else if (this !is MainActivity) {
                    overridePendingTransition(R.anim.screen_left_in, R.anim.screen_right_out)
                }
            }
        }
    }

    override fun onActivityStopped(activity: Activity?) {
        if (isPinEnabled.invoke() && activity !is PinActivity) {
            mHandler.postDelayed(mLogoutRunnable, getPinRequestDelay.invoke())
        }
    }

    private fun setAuthorized(authorized: Boolean) {
        authorize.invoke(authorized)
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
* добавить счетчики к категориям, тегам и т.д
*   добавить поддержку ссылок внутри текста
* (?)добавить градиент на экран с паролем
* добавить иконци в настройках
*   сделать определение локации опциональным
*   добавить предложение оценить приложение (в настройках)
*   сделать кнопку сброса настроек
*   сделать дефолтную дейли-картинку
* добавить сканирование текста
* добавить импорт текста с сайтов (как в EverNote)
* вынести модуль в git submodule
* добавить таймер к паролю
* добавить достижения с анимацией
* добавить превью возможностей дневника
* изменить дизайн даты в списке заметок
* изменить дизайн окна профиля
*   липкие заголовки тупят при поиске
* сделать пейджер на экране премиума
*
* */

