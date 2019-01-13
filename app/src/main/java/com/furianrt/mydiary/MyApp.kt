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

const val LOG_TAG = "myTag"

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
* синхронизация с гугл диском
* запилить дравер лайаут с поиском
* выбор шрифта
*   измениение тегов
* реализовать выбор локации
* оптимизация
* реклама
* улучшить выбор основной картинки
* улучшить дефолтные цвета
* добавить выбор формата времени(24 или АМ РМ)
* добавить анимацию тени к липким заголовкам
* баг после быстрого закрытия заметки (некликабельные вью)
* исправить обновление категории после редактирования
* кликабельные итемы android:background="?android:selectableItemBackground"
*
* */

