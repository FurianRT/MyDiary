package com.furianrt.mydiary.data

import com.furianrt.mydiary.data.api.Forecast
import com.furianrt.mydiary.data.api.WeatherApiService
import com.furianrt.mydiary.data.model.*
import com.furianrt.mydiary.data.prefs.PreferencesHelper
import com.furianrt.mydiary.data.room.NoteDatabase
import com.furianrt.mydiary.data.storage.StorageHelper
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
                     private val mStorage: StorageHelper,
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

    override fun insertImage(image: MyImage): Completable =
            Completable.fromAction { mDatabase.imageDao().insert(image) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    override fun insertImages(images: List<MyImage>): Completable =
            Completable.fromAction { mDatabase.imageDao().insert(images) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    override fun insertHeaderImage(headerImage: MyHeaderImage): Completable =
            Completable.fromAction { mDatabase.headerImageDao().insert(headerImage) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    override fun insertHeaderImage(headerImages: List<MyHeaderImage>): Completable =
            Completable.fromAction { mDatabase.headerImageDao().insert(headerImages) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    override fun addLocation(location: MyLocation): Single<Long> =
            Single.fromCallable { mDatabase.locationDao().insert(location) }
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

    override fun deleteNotes(notes: List<MyNote>): Completable =
            Completable.fromAction { mDatabase.noteDao().delete(notes) }
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

    override fun saveImageToStorage(image: MyImage): Single<MyImage> =
            Single.fromCallable { mStorage.copyImageToStorage(image.url, image.name) }
                    .map { file -> MyImage(file.name, file.path, image.noteId) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    override fun saveHeaderImageToStorage(headerImage: MyHeaderImage): Single<MyHeaderImage> =
            Single.fromCallable { mStorage.copyImageToStorage(headerImage.url, headerImage.name) }
                    .map { file -> MyHeaderImage(file.name, file.path) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    /*override fun getImageFromStorage(imageName: String): Single<File> =
            Single.fromCallable { mStorage.getFile(imageName) }
                    .map { file -> MyImage(file.name, file.path, image.noteId) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())*/

    override fun getNotesWithProp(): Flowable<List<MyNoteWithProp>> =
            mDatabase.noteDao()
                    .getNotesWithProp()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    override fun getTags(tagIds: List<Long>): Single<List<MyTag>> =
            Single.fromCallable { mDatabase.tagDao().getTags(tagIds) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    override fun getImagesForNote(noteId: Long): Flowable<List<MyImage>> =
            mDatabase.imageDao().getImagesForNote(noteId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    override fun getHeaderImages(): Flowable<List<MyHeaderImage>> =
            mDatabase.headerImageDao().getHeaderImages()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
}