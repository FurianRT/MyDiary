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
        private val mRxScheduler: Scheduler
) : DataManager {

    private fun Image.toMyHeaderImage(): MyHeaderImage =
            MyHeaderImage(id, largeImageURL, DateTime.now().millis)

    override fun insertNote(note: MyNote): Completable =
            Completable.fromAction { mDatabase.noteDao().insert(note) }
                    .subscribeOn(mRxScheduler)

    override fun insertNote(notes: List<MyNote>): Completable =
            Completable.fromAction { mDatabase.noteDao().insert(notes) }
                    .subscribeOn(mRxScheduler)

    override fun insertNoteTag(noteTag: NoteTag): Completable =
            Completable.fromAction { mDatabase.noteTagDao().insert(noteTag) }
                    .subscribeOn(mRxScheduler)

    override fun insertNoteTag(noteTags: List<NoteTag>): Completable =
            Completable.fromAction { mDatabase.noteTagDao().insert(noteTags) }
                    .subscribeOn(mRxScheduler)

    override fun insertTag(tag: MyTag): Completable =
            Completable.fromAction { mDatabase.tagDao().insert(tag) }
                    .subscribeOn(mRxScheduler)

    override fun insertTag(tags: List<MyTag>): Completable =
            Completable.fromAction { mDatabase.tagDao().insert(tags) }
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

    override fun insertCategory(category: MyCategory): Completable =
            Completable.fromAction { mDatabase.categoryDao().insert(category) }
                    .subscribeOn(mRxScheduler)

    override fun insertCategory(categories: List<MyCategory>): Completable =
            Completable.fromAction { mDatabase.categoryDao().insert(categories) }
                    .subscribeOn(mRxScheduler)

    override fun insertAppearance(appearance: MyNoteAppearance): Completable =
            Completable.fromAction { mDatabase.appearanceDao().insert(appearance) }
                    .subscribeOn(mRxScheduler)

    override fun insertAppearance(appearances: List<MyNoteAppearance>): Completable =
            Completable.fromAction { mDatabase.appearanceDao().insert(appearances) }
                    .subscribeOn(mRxScheduler)

    override fun addLocation(location: MyLocation): Completable =
            Completable.fromAction { mDatabase.locationDao().insert(location) }
                    .subscribeOn(mRxScheduler)

    override fun updateNote(note: MyNote): Completable =
            Completable.fromAction { mDatabase.noteDao().update(note.apply { isSync = false }) }
                    .subscribeOn(mRxScheduler)

    override fun updateNoteText(noteId: String, title: String, content: String): Completable =
            Completable.fromAction { mDatabase.noteDao().updateNoteText(noteId, title, content) }
                    .subscribeOn(mRxScheduler)

    override fun updateTag(tag: MyTag): Completable =
            Completable.fromAction { mDatabase.tagDao().update(tag.apply { isSync = false }) }
                    .subscribeOn(mRxScheduler)

    override fun updateImages(images: List<MyImage>): Completable =
            Completable.fromAction {
                mDatabase.imageDao().update(images.map { it.apply { isSync = false } })
            }.subscribeOn(mRxScheduler)

    override fun updateImagesSync(images: List<MyImage>): Completable =
            Completable.fromAction { mDatabase.imageDao().update(images) }
                    .subscribeOn(mRxScheduler)

    override fun updateCategory(category: MyCategory): Completable =
            Completable.fromAction { mDatabase.categoryDao().update(category.apply { isSync = false }) }
                    .andThen(mDatabase.noteDao().getAllNotes())
                    .first(emptyList())
                    .flatMapObservable { Observable.fromIterable(it) }
                    .filter { it.categoryId == category.id }
                    .map { it.apply { it.isSync = false } }
                    .flatMapSingle {
                        Completable.fromAction { mDatabase.noteDao().update(it) }
                                .toSingleDefault(true)
                    }
                    .collectInto(mutableListOf<Boolean>()) { l, i -> l.add(i) }
                    .ignoreElement()
                    .subscribeOn(mRxScheduler)

    override fun updateAppearance(appearance: MyNoteAppearance): Completable =
            Completable.fromAction { mDatabase.appearanceDao().update(appearance.apply { isSync = false }) }
                    .andThen(mDatabase.noteDao().getNote(appearance.appearanceId))
                    .map { it.apply { it.isSync = false } }
                    .flatMapCompletable { Completable.fromAction { mDatabase.noteDao().update(it) } }
                    .subscribeOn(mRxScheduler)

    override fun updateDbProfile(profile: MyProfile): Completable =
            Completable.fromAction { mDatabase.profileDao().update(profile) }
                    .subscribeOn(mRxScheduler)

    override fun updateNotesSync(notes: List<MyNote>): Completable =
            Completable.fromAction { mDatabase.noteDao().update(notes) }
                    .subscribeOn(mRxScheduler)

    override fun updateAppearancesSync(appearances: List<MyNoteAppearance>): Completable =
            Completable.fromAction { mDatabase.appearanceDao().update(appearances) }
                    .subscribeOn(mRxScheduler)

    override fun updateCategoriesSync(categories: List<MyCategory>): Completable =
            Completable.fromAction { mDatabase.categoryDao().update(categories) }
                    .subscribeOn(mRxScheduler)

    override fun updateNoteTagsSync(noteTags: List<NoteTag>): Completable =
            Completable.fromAction { mDatabase.noteTagDao().update(noteTags) }
                    .subscribeOn(mRxScheduler)

    override fun updateTagsSync(tags: List<MyTag>): Completable =
            Completable.fromAction { mDatabase.tagDao().update(tags) }
                    .subscribeOn(mRxScheduler)

    override fun deleteTag(tag: MyTag): Completable =
            Completable.fromAction { mDatabase.tagDao().delete(tag.id) }
                    .andThen(Completable.fromAction { mDatabase.noteTagDao().deleteWithTagId(tag.id) })
                    .subscribeOn(mRxScheduler)

    override fun deleteNote(note: MyNote): Completable =
            Completable.fromAction { mDatabase.noteDao().delete(note.id) }
                    .andThen(Completable.fromAction { mDatabase.noteTagDao().deleteWithNoteId(note.id) })
                    .andThen(Completable.fromAction { mDatabase.appearanceDao().delete(note.id) })
                    .andThen(Completable.fromAction { mDatabase.imageDao().deleteByNoteId(note.id) })
                    .andThen(mDatabase.imageDao().getImagesForNote(note.id))
                    .first(emptyList())
                    .map { images -> images.map { it.name } }
                    .flatMapCompletable { Completable.fromCallable { mStorage.deleteFiles(it) } }
                    .subscribeOn(mRxScheduler)

    override fun deleteImage(image: MyImage): Completable =
            Completable.fromAction { mDatabase.imageDao().delete(image.name) }
                    .subscribeOn(mRxScheduler)

    override fun deleteImage(images: List<MyImage>): Completable =
            Completable.fromAction { mDatabase.imageDao().delete(images.map { it.name }) }
                    .subscribeOn(mRxScheduler)

    override fun deleteCategory(category: MyCategory): Completable =
            Completable.fromAction { mDatabase.categoryDao().delete(category.id) }
                    .subscribeOn(mRxScheduler)

    override fun deleteProfile(): Completable =
            Completable.fromAction { mDatabase.profileDao().delete() }
                    .subscribeOn(mRxScheduler)

    override fun replaceNoteTags(noteId: String, tags: List<MyTag>): Completable =
            Completable.fromAction {
                mDatabase.noteTagDao()
                        .replaceNoteTags(noteId, tags.map { it.apply { it.isSync = false } })
            }.subscribeOn(mRxScheduler)

    override fun deleteAllTagsForNote(noteId: String): Completable =
            Completable.fromAction { mDatabase.noteTagDao().deleteAllTagsForNote(noteId) }
                    .subscribeOn(mRxScheduler)

    override fun deleteImageFromStorage(fileName: String): Single<Boolean> =
            Single.fromCallable { mStorage.deleteFile(fileName) }
                    .subscribeOn(mRxScheduler)

    override fun cleanupNotes(): Completable =
            Completable.fromAction { mDatabase.noteDao().cleanup() }
                    .subscribeOn(mRxScheduler)

    override fun cleanupNoteTags(): Completable =
            Completable.fromAction { mDatabase.noteTagDao().cleanup() }
                    .subscribeOn(mRxScheduler)

    override fun cleanupAppearances(): Completable =
            Completable.fromAction { mDatabase.appearanceDao().cleanup() }
                    .subscribeOn(mRxScheduler)

    override fun cleanupCategories(): Completable =
            Completable.fromAction { mDatabase.categoryDao().cleanup() }
                    .subscribeOn(mRxScheduler)

    override fun cleanupTags(): Completable =
            Completable.fromAction { mDatabase.tagDao().cleanup() }
                    .subscribeOn(mRxScheduler)

    override fun cleanupImages(): Completable =
            Completable.fromAction { mDatabase.imageDao().cleanup() }
                    .subscribeOn(mRxScheduler)

    override fun getAllNotes(): Flowable<List<MyNote>> =
            mDatabase.noteDao()
                    .getAllNotes()
                    .subscribeOn(mRxScheduler)

    override fun getDeletedNotes(): Flowable<List<MyNote>> =
            mDatabase.noteDao()
                    .getDeletedNotes()
                    .subscribeOn(mRxScheduler)

    override fun getTagsForNote(noteId: String): Flowable<List<MyTag>> =
            mDatabase.noteTagDao()
                    .getTagsForNote(noteId)
                    .subscribeOn(mRxScheduler)

    override fun getDeletedNoteTags(): Flowable<List<NoteTag>> =
            mDatabase.noteTagDao()
                    .getDeletedNoteTags()
                    .subscribeOn(mRxScheduler)

    override fun getNote(noteId: String): Flowable<MyNote> =
            mDatabase.noteDao()
                    .getNote(noteId)
                    .subscribeOn(mRxScheduler)

    override fun getMood(moodId: Int): Single<MyMood> =
            mDatabase.moodDao()
                    .getMood(moodId)
                    .subscribeOn(mRxScheduler)

    override fun getAllMoods(): Single<List<MyMood>> =
            mDatabase.moodDao()
                    .getAllMoods()
                    .subscribeOn(mRxScheduler)

    override fun getAllImages(): Flowable<List<MyImage>> =
            mDatabase.imageDao()
                    .getAllImages()
                    .subscribeOn(mRxScheduler)

    override fun getDeletedImages(): Flowable<List<MyImage>> =
            mDatabase.imageDao()
                    .getDeletedImages()
                    .subscribeOn(mRxScheduler)

    override fun getAllTags(): Single<List<MyTag>> =
            mDatabase.tagDao()
                    .getAllTags()
                    .subscribeOn(mRxScheduler)

    override fun getDeletedTags(): Flowable<List<MyTag>> =
            mDatabase.tagDao()
                    .getDeletedTags()
                    .subscribeOn(mRxScheduler)

    override fun getAllCategories(): Flowable<List<MyCategory>> =
            mDatabase.categoryDao()
                    .getAllCategories()
                    .subscribeOn(mRxScheduler)

    override fun getNoteAppearance(noteId: String): Flowable<MyNoteAppearance> =
            mDatabase.appearanceDao()
                    .getNoteAppearance(noteId)
                    .subscribeOn(mRxScheduler)

    override fun getDeletedAppearances(): Flowable<List<MyNoteAppearance>> =
            mDatabase.appearanceDao()
                    .getDeletedAppearances()
                    .subscribeOn(mRxScheduler)

    override fun getAllNoteAppearances(): Flowable<List<MyNoteAppearance>> =
            mDatabase.appearanceDao()
                    .getAllNoteAppearances()
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

    override fun getCategory(categoryId: String): Single<MyCategory> =
            mDatabase.categoryDao()
                    .getCategory(categoryId)
                    .subscribeOn(mRxScheduler)

    override fun getDeletedCategories(): Flowable<List<MyCategory>> =
            mDatabase.categoryDao()
                    .getDeletedCategories()
                    .subscribeOn(mRxScheduler)

    override fun getAllNoteTags(): Flowable<List<NoteTag>> =
            mDatabase.noteTagDao()
                    .getAllNoteTags()
                    .subscribeOn(mRxScheduler)

    override fun saveImageToStorage(image: MyImage): Single<MyImage> =
            Single.fromCallable { mStorage.copyImageToStorage(image.uri, image.name) }
                    .map { file -> MyImage(file.name, file.toURI().toString(), image.noteId, image.addedTime) }
                    .subscribeOn(mRxScheduler)

    override fun newProfile(profile: MyProfile): Completable =
            Completable.fromAction { mDatabase.profileDao().newProfile(profile) }
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

    override fun saveProfile(profile: MyProfile): Completable =
            mCloud.saveProfile(profile)
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

    override fun is24TimeFormat(): Boolean = mPrefs.is24TimeFormat()

    override fun loadHeaderImages(page: Int, perPage: Int): Single<List<MyHeaderImage>> =
            mImageApi.getImages()
                    .map { response ->
                        response.images
                                .map { it.toMyHeaderImage() }
                                .sortedByDescending { it.addedTime }
                    }
                    .subscribeOn(mRxScheduler)

    override fun saveNotesInCloud(notes: List<MyNote>): Completable =
            mDatabase.profileDao().getProfile()
                    .firstOrError()
                    .flatMapCompletable { mCloud.saveNotes(notes, it) }
                    .subscribeOn(mRxScheduler)

    override fun saveCategoriesInCloud(categories: List<MyCategory>): Completable =
            mDatabase.profileDao().getProfile()
                    .firstOrError()
                    .flatMapCompletable { mCloud.saveCategories(categories, it) }
                    .subscribeOn(mRxScheduler)

    override fun saveNoteTagsInCloud(noteTags: List<NoteTag>): Completable =
            mDatabase.profileDao().getProfile()
                    .firstOrError()
                    .flatMapCompletable { mCloud.saveNoteTags(noteTags, it) }
                    .subscribeOn(mRxScheduler)

    override fun saveTagsInCloud(tags: List<MyTag>): Completable =
            mDatabase.profileDao().getProfile()
                    .firstOrError()
                    .flatMapCompletable { mCloud.saveTags(tags, it) }
                    .subscribeOn(mRxScheduler)

    override fun saveAppearancesInCloud(appearances: List<MyNoteAppearance>): Completable =
            mDatabase.profileDao().getProfile()
                    .firstOrError()
                    .flatMapCompletable { mCloud.saveAppearances(appearances, it) }
                    .subscribeOn(mRxScheduler)

    override fun saveImagesInCloud(images: List<MyImage>): Completable =
            mDatabase.profileDao().getProfile()
                    .firstOrError()
                    .flatMapCompletable { mCloud.saveImages(images, it) }
                    .subscribeOn(mRxScheduler)

    override fun saveImagesFilesInCloud(images: List<MyImage>): Completable =
            mDatabase.profileDao().getProfile()
                    .firstOrError()
                    .flatMapCompletable { mCloud.saveImagesFiles(images, it) }
                    .subscribeOn(mRxScheduler)

    override fun deleteNotesFromCloud(notes: List<MyNote>): Completable =
            mDatabase.profileDao().getProfile()
                    .firstOrError()
                    .flatMapCompletable { mCloud.deleteNotes(notes, it) }
                    .subscribeOn(mRxScheduler)

    override fun deleteCategoriesFromCloud(categories: List<MyCategory>): Completable =
            mDatabase.profileDao().getProfile()
                    .firstOrError()
                    .flatMapCompletable { mCloud.deleteCategories(categories, it) }
                    .subscribeOn(mRxScheduler)

    override fun deleteNoteTagsFromCloud(noteTags: List<NoteTag>): Completable =
            mDatabase.profileDao().getProfile()
                    .firstOrError()
                    .flatMapCompletable { mCloud.deleteNoteTags(noteTags, it) }
                    .subscribeOn(mRxScheduler)

    override fun deleteTagsFromCloud(tags: List<MyTag>): Completable =
            mDatabase.profileDao().getProfile()
                    .firstOrError()
                    .flatMapCompletable { mCloud.deleteTags(tags, it) }
                    .subscribeOn(mRxScheduler)

    override fun deleteAppearancesFromCloud(appearances: List<MyNoteAppearance>): Completable =
            mDatabase.profileDao().getProfile()
                    .firstOrError()
                    .flatMapCompletable { mCloud.deleteAppearances(appearances, it) }
                    .subscribeOn(mRxScheduler)

    override fun deleteImagesFromCloud(images: List<MyImage>): Completable =
            mDatabase.profileDao().getProfile()
                    .firstOrError()
                    .flatMapCompletable { mCloud.deleteImages(images, it) }
                    .subscribeOn(mRxScheduler)

    override fun getAllNotesFromCloud(): Single<List<MyNote>> =
            mDatabase.profileDao().getProfile()
                    .firstOrError()
                    .flatMap { mCloud.getAllNotes(it) }
                    .subscribeOn(mRxScheduler)

    override fun getAllCategoriesFromCloud(): Single<List<MyCategory>> =
            mDatabase.profileDao().getProfile()
                    .firstOrError()
                    .flatMap { mCloud.getAllCategories(it) }
                    .subscribeOn(mRxScheduler)

    override fun getAllTagsFromCloud(): Single<List<MyTag>> =
            mDatabase.profileDao().getProfile()
                    .firstOrError()
                    .flatMap { mCloud.getAllTags(it) }
                    .subscribeOn(mRxScheduler)

    override fun getAllAppearancesFromCloud(): Single<List<MyNoteAppearance>> =
            mDatabase.profileDao().getProfile()
                    .firstOrError()
                    .flatMap { mCloud.getAllAppearances(it) }
                    .subscribeOn(mRxScheduler)

    override fun getAllNoteTagsFromCloud(): Single<List<NoteTag>> =
            mDatabase.profileDao().getProfile()
                    .firstOrError()
                    .flatMap { mCloud.getAllNoteTags(it) }
                    .subscribeOn(mRxScheduler)

    override fun getAllImagesFromCloud(): Single<List<MyImage>> =
            mDatabase.profileDao().getProfile()
                    .firstOrError()
                    .flatMap { mCloud.getAllImages(it) }
                    .subscribeOn(mRxScheduler)

    override fun loadImageFiles(images: List<MyImage>): Completable =
            mDatabase.profileDao().getProfile()
                    .firstOrError()
                    .flatMapCompletable { mCloud.loadImageFiles(it, images) }
                    .subscribeOn(mRxScheduler)
}