package com.furianrt.mydiary

import android.app.Application
import com.furianrt.mydiary.di.application.AppComponent
import com.furianrt.mydiary.di.application.AppModule
import com.furianrt.mydiary.di.application.DaggerAppComponent

const val LOG_TAG = "myTag"

class MyApp : Application() {

    val component: AppComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
}

/*TODO
* множественный выбор записей для удаления/шаринга
* изменить цвет подсказки диалога
* запретить перелистывание страниц при редактировании
* исправить построение даты для хедера
* добавление картинки к записи
* синхронизация с гугл диском
* изменить оформление хедера списка
* запилить дравер лайаут с поиском
*   добавить тулбар
* изменение цвета текста
* выбор шрифта
* выбор цвета темы
* переделать дизайн окна записи
*   удаление/добавление/измениение тегов
* удаление/добавление/измениение категории
* реализовать выбор локации
* добавление/удаление/измениение настроения
* оптимизация
* реклама
* задать поведение компонентов при появлении клавиатуры
* добавить возможность изменения даты/времени
* */

