package com.furianrt.mydiary.data

import com.furianrt.mydiary.data.api.Forecast
import com.furianrt.mydiary.data.api.WeatherApiService
import com.furianrt.mydiary.data.model.MyNote
import com.furianrt.mydiary.data.model.MyTag
import com.furianrt.mydiary.data.model.NoteTag
import com.furianrt.mydiary.data.prefs.PreferencesHelper
import com.furianrt.mydiary.data.room.NoteDatabase
import com.furianrt.mydiary.di.application.AppScope
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
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

    override fun insertTag(tag: MyTag): Single<Long> =
            Single.fromCallable { mDatabase.tagDao().insert(tag) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    override fun insertTagsForNote(noteId: Long, tags: List<MyTag>): Completable =
            Completable.fromAction { mDatabase.noteTagDao().insertTagsForNote(noteId, tags) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    override fun updateNote(note: MyNote): Completable =
            Completable.fromAction { mDatabase.noteDao().update(note) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    override fun updateTag(tag: MyTag): Completable =
            Completable.fromAction { mDatabase.tagDao().update(tag) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    override fun deleteTag(tag: MyTag): Completable =
            Completable.fromAction { mDatabase.tagDao().delete(tag) }
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

    override fun deleteAllTagsForNote(noteId: Long): Completable =
            Completable.fromAction { mDatabase.noteTagDao().deleteAllTagsForNote(noteId) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    override fun getAllNotes(): Flowable<List<MyNote>> =
            mDatabase.noteDao()
                    .getAllNotes()
                    .observeOn(AndroidSchedulers.mainThread())

    override fun getTagsForNote(noteId: Long): Flowable<List<MyTag>> =
            mDatabase.noteTagDao()
                    .getTagsForNote(noteId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    override fun getNotesWithTag(tagId: Long): Flowable<List<MyNote>> =
            mDatabase.noteTagDao()
                    .getNotesWithTag(tagId)
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

    override fun getForecast(lat: Double, lon: Double): Single<Forecast> =
            mWeatherApi.getForecast(lat, lon)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
}