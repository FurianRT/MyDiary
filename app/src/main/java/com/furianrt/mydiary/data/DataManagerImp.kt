package com.furianrt.mydiary.data

import com.furianrt.mydiary.data.api.Forecast
import com.furianrt.mydiary.data.api.WeatherApiService
import com.furianrt.mydiary.data.model.MyNote
import com.furianrt.mydiary.data.prefs.PreferencesHelper
import com.furianrt.mydiary.data.room.NoteDatabase
import com.furianrt.mydiary.di.application.AppScope
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@AppScope
class DataManagerImp(private val mDatabase: NoteDatabase,
                     private val mPrefs: PreferencesHelper,
                     private val mWeatherApi: WeatherApiService) : DataManager {

    override fun insertNote(note: MyNote): Single<Long> =
            Single.fromCallable { mDatabase.myNoteDao().insert(note) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    override fun updateNote(note: MyNote): Completable =
            Completable.fromAction { mDatabase.myNoteDao().update(note) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    override fun deleteNote(note: MyNote): Completable =
            Completable.fromAction { mDatabase.myNoteDao().delete(note) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())


    override fun getAllNotes(): Flowable<List<MyNote>> =
            mDatabase.myNoteDao()
                    .getAllNotes()
                    .observeOn(AndroidSchedulers.mainThread())

    override fun contains(noteId: Long): Single<Boolean> =
            mDatabase.myNoteDao()
                    .contains(noteId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    override fun getForecast(lat: Double, lon: Double): Single<Forecast> {
        return mWeatherApi.getForecast(lat, lon)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}