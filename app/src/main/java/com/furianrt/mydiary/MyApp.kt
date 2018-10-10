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
* исправить исчезание заголовка при удалении всех записей
* диктор
* изменить цвет подсказки диалога
* синхронизация с гугл диском
* запилить дравер лайаут с поиском
* изменение цвета текста
* выбор шрифта
* выбор цвета темы
*   измениение тегов
* реализовать выбор локации
* оптимизация
* реклама
* добавить возможность изменения даты/времени
* */

