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
import com.furianrt.mydiary.presentation.screens.pin.PinActivity
import com.jakewharton.threetenabp.AndroidThreeTen
import net.danlew.android.joda.JodaTimeAndroid
import javax.inject.Inject
import android.os.StrictMode.VmPolicy
import android.os.StrictMode
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId

class MyApp : Application(), Application.ActivityLifecycleCallbacks {

    companion object {
        private const val TAG = "MyApp"
        const val NOTIFICATION_SYNC_CHANNEL_ID = "sync_channel"
        const val NOTIFICATION_SYNC_CHANNEL_NAME = "Synchronization"
        const val NOTIFICATION_FIREBASE_CHANNEL_ID = "firebase_channel"
        const val NOTIFICATION_FIREBASE_CHANNEL_NAME = "Info"
        const val NOTIFICATION_REMINDER_CHANNEL_ID = "reminder_channel"
        const val NOTIFICATION_REMINDER_CHANNEL_NAME = "Reminder"
    }

    val component: AppComponent by lazy {
        DaggerAppComponent.builder()
                .appContextModule(AppContextModule(applicationContext))
                .build()
    }

    @Inject
    lateinit var authorizeUseCase: AuthorizeUseCase

    @Inject
    lateinit var isPinEnabledUseCase: IsPinEnabledUseCase

    @Inject
    lateinit var getPinRequestDelayUseCase: GetPinRequestDelayUseCase

    @Inject
    lateinit var incrementLaunchCountUseCase: IncrementLaunchCountUseCase

    @Inject
    lateinit var resetSyncProgressUseCase: ResetSyncProgressUseCase

    @Inject
    lateinit var createTutorialNoteUseCase: CreateTutorialNoteUseCase

    private val mHandler = Handler(Looper.getMainLooper())
    private val mLogoutRunnable = Runnable { authorizeUseCase(false) }

    override fun onCreate() {
        component.inject(this)
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
            initStrictMode()
            logFcmToken()
        }
        JodaTimeAndroid.init(this)
        authorizeUseCase(false)
        registerActivityLifecycleCallbacks(this)
        createNotificationChannels()
        AndroidThreeTen.init(this)
        incrementLaunchCountUseCase()
        resetSyncProgressUseCase()
        createTutorialNoteUseCase().subscribe()
    }

    override fun onActivityDestroyed(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        mHandler.removeCallbacks(mLogoutRunnable)
    }

    override fun onActivityStarted(activity: Activity) {
        mHandler.removeCallbacks(mLogoutRunnable)
    }

    override fun onActivityPaused(activity: Activity) {
        if (activity !is PinActivity) {
            authorizeUseCase(true)
        }
    }

    override fun onActivityStopped(activity: Activity) {
        if (isPinEnabledUseCase() && activity !is PinActivity) {
            mHandler.postDelayed(mLogoutRunnable, getPinRequestDelayUseCase())
        }
    }

    private fun initStrictMode() {
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

    private fun logFcmToken() {
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceId ->
            Log.d(TAG, "FcmToken: ${instanceId.token}")
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)
            //channel for sync notification
            notificationManager?.createNotificationChannel(NotificationChannel(
                    NOTIFICATION_SYNC_CHANNEL_ID,
                    NOTIFICATION_SYNC_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW
            ).apply {
                setSound(null, null)
            })
            //channel for firebase notification
            notificationManager?.createNotificationChannel(NotificationChannel(
                    NOTIFICATION_FIREBASE_CHANNEL_ID,
                    NOTIFICATION_FIREBASE_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            ))
            //channel for reminder notification
            notificationManager?.createNotificationChannel(NotificationChannel(
                    NOTIFICATION_REMINDER_CHANNEL_ID,
                    NOTIFICATION_REMINDER_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            ))
        }
    }
}

/*TODO
*
*   добавить notifications
*   сделать напоминание - прочитать указанную заметку через указанный период
* добавить настройки внешнего вида основного экрана
* расширить настройки дейли картинки
* добавить лайауты для горизонтальной ориентации
* поддержать планшеты
* добавить ссылки на используемые библиотеки
* добавить отправку картинок или текста из других приложений
* добавить выбор локации
* добавить выбор типа защиты
* поддержка темной темы
* добавить ссылку на гугл таблицы
* добавить поддержку ссылок внутри текста
* разбить экран настроек на категории
* добавить иконки в настройках
* сделать определение локации опциональным
* сделать дефолтную дейли-картинку
* добавить таймер к паролю
* добавить достижения с анимацией
* добавить превью возможностей дневника
* сделать пейджер на экране премиума
*   добавить удаление заметок по свайпу
*   сделать отмену удаления в списке
*   добавить кнопку очистки фильтров
*
* */

/*TODO премиум фичи
*
* включаемая автоматическая синхронизация
* статистика по записям
* вывод в pdf
* шифрование выбраной заметки
* ежедневный локальный бэкап
*
* */

