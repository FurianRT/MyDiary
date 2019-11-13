/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary

import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.*
import com.facebook.stetho.Stetho
import com.furianrt.mydiary.di.application.component.AppComponent
import com.furianrt.mydiary.di.application.component.DaggerAppComponent
import com.furianrt.mydiary.di.application.modules.app.AppContextModule
import com.furianrt.mydiary.domain.CreateTutorialNoteUseCase
import com.furianrt.mydiary.domain.auth.AuthorizeUseCase
import com.furianrt.mydiary.domain.IncrementLaunchCountUseCase
import com.furianrt.mydiary.domain.check.IsPinEnabledUseCase
import com.furianrt.mydiary.domain.get.GetPinRequestDelayUseCase
import com.furianrt.mydiary.domain.save.ResetSyncProgressUseCase
import com.furianrt.mydiary.presentation.screens.main.MainActivity
import com.furianrt.mydiary.presentation.screens.pin.PinActivity
import com.google.android.gms.ads.MobileAds
import com.jakewharton.threetenabp.AndroidThreeTen
import net.danlew.android.joda.JodaTimeAndroid
import javax.inject.Inject
import android.os.StrictMode.VmPolicy
import android.os.StrictMode

class MyApp : Application(), Application.ActivityLifecycleCallbacks {

    companion object {
        const val NOTIFICATION_SYNC_CHANNEL_ID = "sync_channel"
        const val NOTIFICATION_SYNC_CHANNEL_NAME = "Synchronization"
        const val NOTIFICATION_FIREBASE_CHANNEL_ID = "firebase_channel"
        const val NOTIFICATION_FIREBASE_CHANNEL_NAME = "Info"
    }

    val component: AppComponent by lazy {
        DaggerAppComponent.builder()
                .appContextModule(AppContextModule(applicationContext))
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

    @Inject
    lateinit var createTutorialNote: CreateTutorialNoteUseCase

    private val mHandler = Handler(Looper.getMainLooper())
    private val mLogoutRunnable = Runnable { authorize.invoke(false) }

    override fun onCreate() {
        component.inject(this)
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                    .detectNetwork()
                    .penaltyLog()
                    .build())
            StrictMode.setVmPolicy(VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .detectFileUriExposure()
                    .detectActivityLeaks()
                    .detectLeakedRegistrationObjects()
                    .penaltyLog()
                    .build())
        }
        JodaTimeAndroid.init(this)
        authorize.invoke(false)
        registerActivityLifecycleCallbacks(this)
        createNotificationSyncChannel()
        createNotificationFirebaseChannel()
        MobileAds.initialize(this, getString(R.string.BANNER_AD_APP_ID))
        AndroidThreeTen.init(this)
        incrementLaunchCount.invoke()
        resetSyncProgress.invoke()
        createTutorialNote.invoke().subscribe()
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
        authorize.invoke(true)
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

    private fun createNotificationSyncChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getSystemService(NotificationManager::class.java)
                    ?.createNotificationChannel(NotificationChannel(
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
                    ?.createNotificationChannel(NotificationChannel(
                            NOTIFICATION_FIREBASE_CHANNEL_ID,
                            NOTIFICATION_FIREBASE_CHANNEL_NAME,
                            NotificationManager.IMPORTANCE_DEFAULT
                    ))
        }
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
* добавить отправку картинок или текста из других приложений
* сделать ежедневный локальный бэкап
* добавить выбор локации
* добавить выбор типа защиты
* поддержка темной темы
* добавить ссылку на гугл таблицы
* добавить поддержку ссылок внутри текста
* разбить экран настроек на категории
* добавить иконци в настройках
* сделать определение локации опциональным
* сделать дефолтную дейли-картинку
* добавить таймер к паролю
* добавить достижения с анимацией
* добавить превью возможностей дневника
* сделать пейджер на экране премиума
* перенести аналитику в репозиторий
*   добавить удаление заметок по свайпу
*   сделать отмену удаления в списке
*   добавить кнопку очистки фильтров
*   изсправить баг с дефолтными настройками внешнего вида
*   добавить включаемую автоматическу синхронизацию
*   исправить shareNOte
*   автоматически отключать локацию при отсутствии разрешений
*
* */

/*TODO премиум фичи
*
* включаемая автоматическая синхронизация
* статистика по записям
* вывод в pdf
* шифрование выбраной заметки
*
* */

