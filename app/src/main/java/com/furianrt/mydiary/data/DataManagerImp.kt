package com.furianrt.mydiary.data

import com.furianrt.mydiary.data.api.forecast.Forecast
import com.furianrt.mydiary.data.api.forecast.WeatherApiService
import com.furianrt.mydiary.data.api.images.Image
import com.furianrt.mydiary.data.api.images.ImageApiService
import com.furianrt.mydiary.data.cloud.CloudHelper
import com.furianrt.mydiary.data.model.*
import com.furianrt.mydiary.data.prefs.PreferencesHelper
import com.furianrt.mydiary.data.room.NoteDatabase
import com.furianrt.mydiary.data.storage.StorageHelper
import com.furianrt.mydiary.di.application.AppScope
import io.reactivex.*
import org.joda.time.DateTime

@AppScope
class DataManagerImp(
        private val mDatabase: NoteDatabase,
        private val mPrefs: PreferencesHelper,
        private val mStorage: StorageHelper,
        private val mWeatherApi: WeatherApiService,
        private val mImageApi: ImageApiService,
        private val mCloud: CloudHelper,
        private val mRxScheduler: Scheduler  //Для тестов. Когда-нибудь они появятся.
) : DataManager {

    private fun Image.toMyHeaderImage(): MyHeaderImage =
            MyHeaderImage(id, largeImageURL, DateTime.now().millis)

    override fun insertNote(note: MyNote): Completable =
            Completable.fromAction { mDatabase.noteDao().insert(note) }
                    .subscribeOn(mRxScheduler)

    override fun insertNoteTag(noteTag: NoteTag): Completable =
            Completable.fromAction { mDatabase.noteTagDao().insert(noteTag) }
                    .subscribeOn(mRxScheduler)

    override fun insertTag(tag: MyTag): Completable =
            Completable.fromAction { mDatabase.tagDao().insert(tag) }
                    .subscribeOn(mRxScheduler)

    override fun insertImage(image: MyImage): Completable =
            Completable.fromAction { mDatabase.imageDao().insert(image) }
                    .subscribeOn(mRxScheduler)

    override fun insertImages(images: List<MyImage>): Completable =
            Completable.fromAction { mDatabase.imageDao().insert(images) }
                    .subscribeOn(mRxScheduler)

    override fun insertHeaderImage(headerImage: MyHeaderImage): Single<Long> =
            Single.fromCallable { mDatabase.headerImageDao().insert(headerImage) }
                    .subscribeOn(mRxScheduler)

    override fun insertCategory(category: MyCategory): Single<Long> =
            Single.fromCallable { mDatabase.categoryDao().insert(category) }
                    .subscribeOn(mRxScheduler)

    override fun insertAppearance(appearance: MyNoteAppearance): Completable =
            Completable.fromAction { mDatabase.appearanceDao().insert(appearance) }
                    .subscribeOn(mRxScheduler)

    override fun addLocation(location: MyLocation): Completable =
            Completable.fromAction { mDatabase.locationDao().insert(location) }
                    .subscribeOn(mRxScheduler)

    override fun updateNote(note: MyNote): Completable =
            Completable.fromAction { mDatabase.noteDao().update(note) }
                    .subscribeOn(mRxScheduler)

    override fun updateNoteText(noteId: String, title: String, content: String): Completable =
            Completable.fromAction { mDatabase.noteDao().updateNoteText(noteId, title, content) }
                    .subscribeOn(mRxScheduler)

    override fun updateTag(tag: MyTag): Completable =
            Completable.fromAction { mDatabase.tagDao().update(tag) }
                    .subscribeOn(mRxScheduler)

    override fun updateImages(images: List<MyImage>): Completable =
            Completable.fromAction { mDatabase.imageDao().updateAll(images) }
                    .subscribeOn(mRxScheduler)

    override fun updateCategory(category: MyCategory): Completable =
            Completable.fromAction { mDatabase.categoryDao().update(category) }
                    .subscribeOn(mRxScheduler)

    override fun updateAppearance(appearance: MyNoteAppearance): Completable =
            Completable.fromAction { mDatabase.appearanceDao().update(appearance) }
                    .subscribeOn(mRxScheduler)

    override fun updateDbProfile(profile: MyProfile): Completable =
            Completable.fromAction { mDatabase.profileDao().update(profile) }
                    .subscribeOn(mRxScheduler)

    override fun deleteTag(tag: MyTag): Completable =
            Completable.fromAction { mDatabase.tagDao().delete(tag) }
                    .subscribeOn(mRxScheduler)

    override fun deleteNote(note: MyNote): Completable =
            Completable.fromAction { mDatabase.noteDao().delete(note) }
                    .subscribeOn(mRxScheduler)

    override fun deleteNotes(notes: List<MyNote>): Completable =
            Completable.fromAction { mDatabase.noteDao().delete(notes) }
                    .subscribeOn(mRxScheduler)

    override fun deleteImages(images: List<MyImage>): Completable =
            Completable.fromAction { mDatabase.imageDao().delete(images) }
                    .subscribeOn(mRxScheduler)

    override fun deleteNoteTag(noteTag: NoteTag): Completable =
            Completable.fromAction { mDatabase.noteTagDao().delete(noteTag) }
                    .subscribeOn(mRxScheduler)

    override fun deleteCategory(category: MyCategory): Completable =
            Completable.fromAction { mDatabase.categoryDao().delete(category) }
                    .subscribeOn(mRxScheduler)

    override fun deleteProfile(): Completable =
            Completable.fromAction { mDatabase.profileDao().delete() }
                    .subscribeOn(mRxScheduler)

    override fun replaceNoteTags(noteId: String, tags: List<MyTag>): Completable =
            Completable.fromAction { mDatabase.noteTagDao().replaceNoteTags(noteId, tags) }
                    .subscribeOn(mRxScheduler)

    override fun deleteAllTagsForNote(noteId: String): Completable =
            Completable.fromAction { mDatabase.noteTagDao().deleteAllTagsForNote(noteId) }
                    .subscribeOn(mRxScheduler)

    override fun deleteImageFromStorage(fileName: String): Single<Boolean> =
            Single.fromCallable { mStorage.deleteFile(fileName) }
                    .subscribeOn(mRxScheduler)

    override fun getAllNotes(): Flowable<List<MyNote>> =
            mDatabase.noteDao()
                    .getAllNotes()
                    .subscribeOn(mRxScheduler)

    override fun getTagsForNote(noteId: String): Flowable<List<MyTag>> =
            mDatabase.noteTagDao()
                    .getTagsForNote(noteId)
                    .subscribeOn(mRxScheduler)

    override fun getNoteWithProp(noteId: String): Flowable<MyNoteWithProp> =
            mDatabase.noteDao()
                    .getNoteWithProp(noteId)
                    .subscribeOn(mRxScheduler)

    override fun getNote(noteId: String): Flowable<MyNote> =
            mDatabase.noteDao()
                    .getNote(noteId)
                    .subscribeOn(mRxScheduler)

    override fun getNotesWithTag(tagId: Long): Flowable<List<MyNote>> =
            mDatabase.noteTagDao()
                    .getNotesWithTag(tagId)
                    .subscribeOn(mRxScheduler)

    override fun getMood(moodId: Int): Single<MyMood> =
            mDatabase.moodDao()
                    .getMood(moodId)
                    .subscribeOn(mRxScheduler)

    override fun getAllMoods(): Single<List<MyMood>> =
            mDatabase.moodDao()
                    .getAllMoods()
                    .subscribeOn(mRxScheduler)

    override fun getAllTags(): Single<List<MyTag>> =
            mDatabase.tagDao()
                    .getAllTags()
                    .subscribeOn(mRxScheduler)

    override fun getAllCategories(): Flowable<List<MyCategory>> =
            mDatabase.categoryDao()
                    .getAllCategories()
                    .subscribeOn(mRxScheduler)

    override fun getNoteAppearance(noteId: String): Flowable<MyNoteAppearance> =
            mDatabase.appearanceDao()
                    .getNoteAppearance(noteId)
                    .subscribeOn(mRxScheduler)

    override fun getDbProfile(): Observable<MyProfile> =
            mDatabase.profileDao()
                    .getProfile()
                    .subscribeOn(mRxScheduler)

    override fun getCloudProfile(email: String): Maybe<MyProfile> =
            mCloud.getProfile(email)
                    .subscribeOn(mRxScheduler)


    override fun findNote(noteId: String): Maybe<MyNote> =
            mDatabase.noteDao()
                    .findNote(noteId)
                    .subscribeOn(mRxScheduler)

    override fun getForecast(lat: Double, lon: Double): Single<Forecast?> =
            mWeatherApi.getForecast(lat, lon)
                    .subscribeOn(mRxScheduler)

    override fun getCategory(categoryId: Long): Maybe<MyCategory> =
            mDatabase.categoryDao()
                    .getCategory(categoryId)
                    .subscribeOn(mRxScheduler)

    override fun saveImageToStorage(image: MyImage): Single<MyImage> =
            Single.fromCallable { mStorage.copyImageToStorage(image.url, image.name) }
                    .map { file -> MyImage(file.name, file.path, image.noteId, image.addedTime) }
                    .subscribeOn(mRxScheduler)

    override fun saveProfile(profile: MyProfile): Completable =
            Completable.fromAction { mDatabase.profileDao().save(profile) }
                    .subscribeOn(mRxScheduler)

    override fun getAllNotesWithProp(): Flowable<List<MyNoteWithProp>> =
            mDatabase.noteDao()
                    .getAllNotesWithProp()
                    .subscribeOn(mRxScheduler)

    override fun getImagesForNote(noteId: String): Flowable<List<MyImage>> =
            mDatabase.imageDao().getImagesForNote(noteId)
                    .subscribeOn(mRxScheduler)

    override fun getHeaderImages(): Flowable<List<MyHeaderImage>> =
            mDatabase.headerImageDao().getHeaderImages()
                    .map { it.sortedByDescending { image -> image.addedTime } }
                    .subscribeOn(mRxScheduler)

    override fun createProfile(profile: MyProfile): Completable =
            mCloud.createProfile(profile)
                    .subscribeOn(mRxScheduler)

    override fun isWeatherEnabled(): Boolean = mPrefs.isWeatherEnabled()

    override fun isLocationEnabled(): Boolean = mPrefs.isMapEnabled()

    override fun isMoodEnabled(): Boolean = mPrefs.isMoodEnabled()

    override fun isSortDesc(): Boolean = mPrefs.isSortDesc()

    override fun setSortDesc(desc: Boolean) {
        mPrefs.setSortDesc(desc)
    }

    override fun isProfileExists(email: String): Single<Boolean> =
            mCloud.isProfileExists(email)
                    .subscribeOn(mRxScheduler)

    override fun getTextColor(): Int = mPrefs.getTextColor()

    override fun getTextSize(): Int = mPrefs.getTextSize()

    override fun getNoteBackgroundColor(): Int = mPrefs.getNoteBackgroundColor()

    override fun getNoteTextBackgroundColor(): Int = mPrefs.getNoteTextBackgroundColor()

    override fun getTimeFormat(): Int = mPrefs.getTimeFormat()

    override fun loadHeaderImages(page: Int, perPage: Int): Single<List<MyHeaderImage>> =
            mImageApi.getImages()
                    .map {
                        it.images
                                .map { image -> image.toMyHeaderImage() }
                                .sortedByDescending { image -> image.addedTime }
                    }
                    .subscribeOn(mRxScheduler)
}