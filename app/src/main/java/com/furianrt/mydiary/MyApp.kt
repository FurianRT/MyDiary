package com.furianrt.mydiary

import android.app.Application
import com.furianrt.mydiary.di.application.AppComponent
import com.furianrt.mydiary.di.application.AppModule
import com.furianrt.mydiary.di.application.DaggerAppComponent
import com.furianrt.mydiary.general.MediaLoader
import com.yanzhenjie.album.Album
import com.yanzhenjie.album.AlbumConfig
import net.danlew.android.joda.JodaTimeAndroid
import java.util.*

class MyApp : Application() {

    val component: AppComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()

    override fun onCreate() {
        super.onCreate()
        JodaTimeAndroid.init(this)

        Album.initialize(AlbumConfig.newBuilder(this)
                .setAlbumLoader(MediaLoader())
                .setLocale(Locale.getDefault())
                .build())
    }
}

/*TODO
*
* редактор картинок
* диктор
* изменить цвет подсказки диалога
* синхронизация с firestore
* запилить дравер лайаут с поиском
* выбор шрифта
*   измениение тегов
* реализовать выбор локации
* оптимизация
* реклама
* улучшить дефолтные цвета
* добавить анимацию тени к липким заголовкам
* кликабельные итемы android:background="?android:selectableItemBackground"
* добавить подсказки для drag and prop и другого
* добавить notifications
* добавить appsFlyerEvents
* добавить пейзашную дайли картинку
* добавить настройки внешнего вида основного экрана
* полностью переделать дизайн главного экрана
* добавить анимацию ошибок аутентификации
* добавить заглушку на пустые состояния листов
* исправить баг после быстрого закрытия заметки (некликабельные вьюхи)
*
* */

