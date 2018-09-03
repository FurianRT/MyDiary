package com.furianrt.mydiary.data

import android.util.Log
import com.furianrt.mydiary.LOG_TAG
import com.furianrt.mydiary.data.api.Forecast
import com.furianrt.mydiary.data.api.WeatherApiService
import com.furianrt.mydiary.data.model.MyNote
import com.furianrt.mydiary.data.model.MyTag
import com.furianrt.mydiary.data.model.NoteTag
import com.furianrt.mydiary.data.model.NoteWithTags
import com.furianrt.mydiary.data.prefs.PreferencesHelper
import com.furianrt.mydiary.data.room.NoteDatabase
import com.furianrt.mydiary.di.application.AppScope
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@AppScope
class DataManagerImp(private val mDatabase: NoteDatabase,
                     private val mPrefs: PreferencesHelper,
                     private val mWeatherApi: WeatherApiService) : DataManager {

    override fun insertNote(note: MyNote): Single<Long> =
            Single.fromCallable { mDatabase.noteDao().insert(note) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    override fun insertNoteTag(noteTag: NoteTag): Completable =
            Completable.fromAction { mDatabase.noteTagDao().insert(noteTag) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    override fun insertAllNoteTag(noteTags: List<NoteTag>): Completable {
        Log.e("trtr", "insertAllNoteTag")
        return Completable.fromAction { mDatabase.noteTagDao().insertAll(noteTags) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun updateNote(note: MyNote): Completable =
            Completable.fromAction { mDatabase.noteDao().update(note) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    override fun deleteNote(note: MyNote): Completable =
            Completable.fromAction { mDatabase.noteDao().delete(note) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    override fun deleteNoteTag(noteTag: NoteTag): Completable =
            Completable.fromAction { mDatabase.noteTagDao().delete(noteTag) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    override fun deleteAllTagsForNote(noteId: Long): Completable {
        Log.e(LOG_TAG, "deleteAllTagsForNote")
        return Completable.fromAction { mDatabase.noteTagDao().deleteAll(noteId) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getAllNotes(): Flowable<List<MyNote>> =
            mDatabase.noteDao()
                    .getAllNotes()
                    .observeOn(AndroidSchedulers.mainThread())

    override fun getNoteWithTags(noteId: Long): Single<NoteWithTags> =
            mDatabase.noteDao()
                    .getNoteWithTags(noteId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    override fun getAllTags(): Single<List<MyTag>> =
            mDatabase.tagDao()
                    .getAllTags()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    override fun findNote(noteId: Long): Maybe<MyNote> =
            mDatabase.noteDao()
                    .findNote(noteId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    override fun getForecast(lat: Double, lon: Double): Single<Forecast> {
        return mWeatherApi.getForecast(lat, lon)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}