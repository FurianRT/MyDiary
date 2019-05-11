package com.furianrt.mydiary.data

import android.annotation.SuppressLint
import android.util.Base64
import com.furianrt.mydiary.BuildConfig
import com.furianrt.mydiary.data.api.forecast.WeatherApiService
import com.furianrt.mydiary.data.api.images.Image
import com.furianrt.mydiary.data.api.images.ImageApiService
import com.furianrt.mydiary.data.auth.AuthHelper
import com.furianrt.mydiary.data.cloud.CloudHelper
import com.furianrt.mydiary.data.database.NoteDatabase
import com.furianrt.mydiary.data.model.*
import com.furianrt.mydiary.data.prefs.PreferencesHelper
import com.furianrt.mydiary.data.storage.StorageHelper
import com.furianrt.mydiary.di.application.AppScope
import com.google.gson.Gson
import io.reactivex.*
import org.joda.time.DateTime
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

//Возможно, стоило что-то убрать в отдельные use case
@AppScope
class DataManagerImp(
        private val database: NoteDatabase,
        private val prefs: PreferencesHelper,
        private val storage: StorageHelper,
        private val weatherApi: WeatherApiService,
        private val imageApi: ImageApiService,
        private val cloud: CloudHelper,
        private val auth: AuthHelper,
        private val rxScheduler: Scheduler
) : DataManager {

    @SuppressLint("GetInstance")
    private fun decryptString(string: String): String {
        val keyBytes = BuildConfig.PREFS_PASSWORD.toByteArray()
        val aesKey = SecretKeySpec(keyBytes, "AES")
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, aesKey)
        val decryptedByteValue = cipher.doFinal(Base64.decode(string.toByteArray(), Base64.DEFAULT))
        return String(decryptedByteValue)
    }

    @SuppressLint("GetInstance")
    private fun encryptString(string: String): String {
        val keyBytes = BuildConfig.PREFS_PASSWORD.toByteArray()
        val aesKey = SecretKeySpec(keyBytes, "AES")
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, aesKey)
        val encrypted = cipher.doFinal(string.toByteArray())
        return Base64.encodeToString(encrypted, Base64.DEFAULT)
    }

    private fun Image.toMyHeaderImage(): MyHeaderImage =
            MyHeaderImage(id, largeImageURL, DateTime.now().millis)

    override fun insertNote(note: MyNote): Completable =
            database.noteDao().insert(note)
                    .subscribeOn(rxScheduler)

    override fun insertNote(notes: List<MyNote>): Completable =
            database.noteDao().insert(notes)
                    .subscribeOn(rxScheduler)

    override fun insertNoteTag(noteTag: NoteTag): Completable =
            database.noteTagDao().insert(noteTag)
                    .subscribeOn(rxScheduler)

    override fun insertNoteTag(noteTags: List<NoteTag>): Completable =
            database.noteTagDao().insert(noteTags)
                    .subscribeOn(rxScheduler)

    override fun insertLocation(locations: List<MyLocation>): Completable =
            database.locationDao().insert(locations)
                    .subscribeOn(rxScheduler)

    override fun insertForecast(forecasts: List<MyForecast>): Completable =
            database.forecastDao().insert(forecasts)
                    .subscribeOn(rxScheduler)

    override fun insertTag(tag: MyTag): Completable =
            database.tagDao().insert(tag)
                    .subscribeOn(rxScheduler)

    override fun insertTag(tags: List<MyTag>): Completable =
            database.tagDao().insert(tags)
                    .subscribeOn(rxScheduler)

    override fun insertImage(image: MyImage): Completable =
            database.imageDao().insert(image)
                    .subscribeOn(rxScheduler)

    override fun insertImages(images: List<MyImage>): Completable =
            database.imageDao().insert(images)
                    .subscribeOn(rxScheduler)

    override fun insertHeaderImage(headerImage: MyHeaderImage): Completable =
            database.headerImageDao().insert(headerImage)
                    .subscribeOn(rxScheduler)

    override fun insertCategory(category: MyCategory): Completable =
            database.categoryDao().insert(category)
                    .subscribeOn(rxScheduler)

    override fun insertCategory(categories: List<MyCategory>): Completable =
            database.categoryDao().insert(categories)
                    .subscribeOn(rxScheduler)

    override fun insertAppearance(appearance: MyNoteAppearance): Completable =
            database.appearanceDao().insert(appearance)
                    .subscribeOn(rxScheduler)

    override fun insertAppearance(appearances: List<MyNoteAppearance>): Completable =
            database.appearanceDao().insert(appearances)
                    .subscribeOn(rxScheduler)

    override fun insertProfile(profile: MyProfile): Completable =
            database.profileDao().insert(profile)
                    .subscribeOn(rxScheduler)

    override fun insertForecast(forecast: MyForecast): Completable =
            database.forecastDao().insert(forecast)
                    .subscribeOn(rxScheduler)

    override fun insertLocation(location: MyLocation): Completable =
            database.locationDao().insert(location)
                    .subscribeOn(rxScheduler)

    override fun updateNote(note: MyNote): Completable =
            database.noteDao().update(note.apply { syncWith.clear() })
                    .subscribeOn(rxScheduler)

    override fun updateNoteText(noteId: String, title: String, content: String): Completable =
            database.noteDao().updateNoteText(noteId, title, content)
                    .subscribeOn(rxScheduler)

    override fun updateTag(tag: MyTag): Completable =
            database.tagDao().update(tag.apply { syncWith.clear() })
                    .subscribeOn(rxScheduler)

    override fun updateImage(image: MyImage): Completable =
            database.imageDao().update(image.apply { syncWith.clear() })
                    .subscribeOn(rxScheduler)

    override fun updateImage(images: List<MyImage>): Completable =
            database.imageDao().update(images.map { it.apply { syncWith.clear() } })
                    .subscribeOn(rxScheduler)

    override fun updateImageSync(images: List<MyImage>): Completable =
            database.imageDao().update(images)
                    .subscribeOn(rxScheduler)

    override fun updateCategory(category: MyCategory): Completable =
            database.categoryDao().update(category.apply { syncWith.clear() })
                    .andThen(database.noteDao().getAllNotes())
                    .first(emptyList())
                    .flatMapObservable { Observable.fromIterable(it) }
                    .filter { it.categoryId == category.id }
                    .map { it.apply { it.syncWith.clear() } }
                    .flatMapSingle { database.noteDao().update(it).toSingleDefault(true) }
                    .collectInto(mutableListOf<Boolean>()) { l, i -> l.add(i) }
                    .ignoreElement()
                    .subscribeOn(rxScheduler)

    override fun updateAppearance(appearance: MyNoteAppearance): Completable =
            database.appearanceDao().update(appearance.apply { syncWith.clear() })
                    .andThen(database.noteDao().getNote(appearance.appearanceId))
                    .map { it.apply { it.syncWith.clear() } }
                    .flatMapCompletable { database.noteDao().update(it) }
                    .subscribeOn(rxScheduler)

    override fun updateDbProfile(profile: MyProfile): Completable =
            database.profileDao().update(profile)
                    .subscribeOn(rxScheduler)

    override fun updateNotesSync(notes: List<MyNote>): Completable =
            database.noteDao().update(notes)
                    .subscribeOn(rxScheduler)

    override fun updateAppearancesSync(appearances: List<MyNoteAppearance>): Completable =
            database.appearanceDao().update(appearances)
                    .subscribeOn(rxScheduler)

    override fun updateCategoriesSync(categories: List<MyCategory>): Completable =
            database.categoryDao().update(categories)
                    .subscribeOn(rxScheduler)

    override fun updateNoteTagsSync(noteTags: List<NoteTag>): Completable =
            database.noteTagDao().update(noteTags)
                    .subscribeOn(rxScheduler)

    override fun updateLocationsSync(locations: List<MyLocation>): Completable =
            database.locationDao().update(locations)
                    .subscribeOn(rxScheduler)

    override fun updateForecastsSync(forecasts: List<MyForecast>): Completable =
            database.forecastDao().update(forecasts)
                    .subscribeOn(rxScheduler)

    override fun updateTagsSync(tags: List<MyTag>): Completable =
            database.tagDao().update(tags)
                    .subscribeOn(rxScheduler)

    override fun deleteTag(tag: MyTag): Completable =
            database.tagDao().delete(tag.id)
                    .andThen(database.noteTagDao().deleteWithTagId(tag.id))
                    .subscribeOn(rxScheduler)

    override fun deleteNote(noteId: String): Completable =
            database.noteTagDao().deleteWithNoteId(noteId)
                    .andThen(database.appearanceDao().delete(noteId))
                    .andThen(database.imageDao().getImagesForNote(noteId))
                    .first(emptyList())
                    .flatMapObservable { Observable.fromIterable(it) }
                    .flatMapSingle { Single.fromCallable { storage.deleteFile(it.name) } }
                    .collectInto(mutableListOf<Boolean>()) { l, i -> l.add(i) }
                    .flatMapCompletable { database.imageDao().deleteByNoteId(noteId) }
                    .andThen(database.noteDao().delete(noteId))
                    .subscribeOn(rxScheduler)

    override fun deleteImage(imageName: String): Completable =
            database.imageDao().delete(imageName)
                    .andThen(Completable.fromCallable { storage.deleteFile(imageName) })
                    .subscribeOn(rxScheduler)

    override fun deleteImage(imageNames: List<String>): Completable =
            database.imageDao().delete(imageNames)
                    .andThen(Observable.fromIterable(imageNames))
                    .flatMapSingle { Single.fromCallable { storage.deleteFile(it) } }
                    .collectInto(mutableListOf<Boolean>()) { l, i -> l.add(i) }
                    .ignoreElement()
                    .subscribeOn(rxScheduler)

    override fun deleteCategory(category: MyCategory): Completable =
            database.categoryDao().delete(category.id)
                    .subscribeOn(rxScheduler)

    override fun deleteNoteTag(noteId: String, tagId: String): Completable =
            database.noteTagDao().delete(noteId, tagId)
                    .subscribeOn(rxScheduler)

    override fun clearDbProfile(): Completable =
            database.profileDao().clearProfile()
                    .subscribeOn(rxScheduler)

    override fun deleteImageFromStorage(fileName: String): Single<Boolean> =
            Single.fromCallable { storage.deleteFile(fileName) }
                    .subscribeOn(rxScheduler)

    override fun cleanupNotes(): Completable =
            database.noteDao().cleanup()
                    .subscribeOn(rxScheduler)

    override fun cleanupNoteTags(): Completable =
            database.noteTagDao().cleanup()
                    .subscribeOn(rxScheduler)

    override fun cleanupAppearances(): Completable =
            database.appearanceDao().cleanup()
                    .subscribeOn(rxScheduler)

    override fun cleanupCategories(): Completable =
            database.categoryDao().cleanup()
                    .subscribeOn(rxScheduler)

    override fun cleanupTags(): Completable =
            database.tagDao().cleanup()
                    .subscribeOn(rxScheduler)

    override fun cleanupImages(): Completable =
            database.imageDao().cleanup()
                    .subscribeOn(rxScheduler)

    override fun getAllNotes(): Flowable<List<MyNote>> =
            database.noteDao()
                    .getAllNotes()
                    .subscribeOn(rxScheduler)

    override fun getDeletedNotes(): Flowable<List<MyNote>> =
            database.noteDao()
                    .getDeletedNotes()
                    .subscribeOn(rxScheduler)

    override fun getTagsForNote(noteId: String): Flowable<List<MyTag>> =
            database.noteTagDao()
                    .getTagsForNote(noteId)
                    .subscribeOn(rxScheduler)

    override fun getDeletedNoteTags(): Flowable<List<NoteTag>> =
            database.noteTagDao()
                    .getDeletedNoteTags()
                    .subscribeOn(rxScheduler)

    override fun getNote(noteId: String): Flowable<MyNote> =
            database.noteDao()
                    .getNote(noteId)
                    .subscribeOn(rxScheduler)

    override fun getMood(moodId: Int): Single<MyMood> =
            database.moodDao()
                    .getMood(moodId)
                    .subscribeOn(rxScheduler)

    override fun getAllMoods(): Single<List<MyMood>> =
            database.moodDao()
                    .getAllMoods()
                    .subscribeOn(rxScheduler)

    override fun getAllImages(): Flowable<List<MyImage>> =
            database.imageDao()
                    .getAllImages()
                    .subscribeOn(rxScheduler)

    override fun getDeletedImages(): Flowable<List<MyImage>> =
            database.imageDao()
                    .getDeletedImages()
                    .subscribeOn(rxScheduler)

    override fun getAllTags(): Flowable<List<MyTag>> =
            database.tagDao()
                    .getAllTags()
                    .subscribeOn(rxScheduler)

    override fun getDeletedTags(): Flowable<List<MyTag>> =
            database.tagDao()
                    .getDeletedTags()
                    .subscribeOn(rxScheduler)

    override fun getAllCategories(): Flowable<List<MyCategory>> =
            database.categoryDao()
                    .getAllCategories()
                    .subscribeOn(rxScheduler)

    override fun getNoteAppearance(noteId: String): Flowable<MyNoteAppearance> =
            database.appearanceDao()
                    .getNoteAppearance(noteId)
                    .subscribeOn(rxScheduler)

    override fun getDeletedAppearances(): Flowable<List<MyNoteAppearance>> =
            database.appearanceDao()
                    .getDeletedAppearances()
                    .subscribeOn(rxScheduler)

    override fun getAllNoteAppearances(): Flowable<List<MyNoteAppearance>> =
            database.appearanceDao()
                    .getAllNoteAppearances()
                    .subscribeOn(rxScheduler)

    override fun getDbProfile(): Observable<MyProfile> =
            database.profileDao()
                    .getProfile()
                    .subscribeOn(rxScheduler)

    override fun findNote(noteId: String): Maybe<MyNote> =
            database.noteDao()
                    .findNote(noteId)
                    .subscribeOn(rxScheduler)

    override fun loadForecast(lat: Double, lon: Double): Single<MyForecast> =
            weatherApi.getForecast(lat, lon)
                    .map {
                        MyForecast(
                                temp = it.main.temp,
                                icon =  DataManager.BASE_WEATHER_IMAGE_URL + it.weather[0].icon + ".png"
                        )
                    }
                    .subscribeOn(rxScheduler)

    override fun getAllDbForecasts(): Single<List<MyForecast>> =
            database.forecastDao().getAllForecasts()
                    .subscribeOn(rxScheduler)

    override fun getAllDbLocations(): Single<List<MyLocation>> =
            database.locationDao().getAllLocations()
                    .subscribeOn(rxScheduler)

    override fun getCategory(categoryId: String): Single<MyCategory> =
            database.categoryDao()
                    .getCategory(categoryId)
                    .subscribeOn(rxScheduler)

    override fun getDeletedCategories(): Flowable<List<MyCategory>> =
            database.categoryDao()
                    .getDeletedCategories()
                    .subscribeOn(rxScheduler)

    override fun getAllNoteTags(): Flowable<List<NoteTag>> =
            database.noteTagDao()
                    .getAllNoteTags()
                    .subscribeOn(rxScheduler)

    override fun saveImageToStorage(image: MyImage): Single<MyImage> =
            Single.fromCallable { storage.copyImageToStorage(image.uri, image.name) }
                    .map { file -> MyImage(file.name, file.toURI().toString(), image.noteId, image.addedTime) }
                    .subscribeOn(rxScheduler)

    override fun getAllNotesWithProp(): Flowable<List<MyNoteWithProp>> =
            database.noteDao()
                    .getAllNotesWithProp()
                    .subscribeOn(rxScheduler)

    override fun getImagesForNote(noteId: String): Flowable<List<MyImage>> =
            database.imageDao().getImagesForNote(noteId)
                    .subscribeOn(rxScheduler)

    override fun getHeaderImages(): Flowable<List<MyHeaderImage>> =
            database.headerImageDao().getHeaderImages()
                    .map { it.sortedByDescending { image -> image.addedTime } }
                    .subscribeOn(rxScheduler)

    override fun getPin(): Single<String> =
            Single.fromCallable { prefs.getPin() }
                    .map { decryptString(it) }
                    .subscribeOn(rxScheduler)

    override fun setPin(pin: String): Completable =
            Completable.fromAction { prefs.setPin(encryptString(pin)) }
                    .subscribeOn(rxScheduler)

    override fun getBackupEmail(): String = prefs.getBackupEmail()

    override fun setBackupEmail(email: String) {
        prefs.setBackupEmail(email)
    }

    override fun isWeatherEnabled(): Boolean = prefs.isWeatherEnabled()

    override fun getWeatherUnits(): Int = prefs.getWeatherUnits()

    override fun isLocationEnabled(): Boolean = prefs.isMapEnabled()

    override fun isMoodEnabled(): Boolean = prefs.isMoodEnabled()

    override fun isSortDesc(): Boolean = prefs.isSortDesc()

    override fun setSortDesc(desc: Boolean) {
        prefs.setSortDesc(desc)
    }

    override fun isProfileExists(email: String): Single<Boolean> =
            auth.isProfileExists(email)
                    .subscribeOn(rxScheduler)

    override fun getTextColor(): Int = prefs.getTextColor()

    override fun getSurfaceTextColor(): Int = prefs.getSurfaceTextColor()

    override fun getTextSize(): Int = prefs.getTextSize()

    override fun getNoteBackgroundColor(): Int = prefs.getNoteBackgroundColor()

    override fun getNoteTextBackgroundColor(): Int = prefs.getNoteTextBackgroundColor()

    override fun is24TimeFormat(): Boolean = prefs.is24TimeFormat()

    override fun isAuthorized(): Boolean = prefs.isAuthorized()

    override fun setAuthorized(authorized: Boolean) {
        prefs.setAuthorized(authorized)
    }

    override fun getPasswordRequestDelay(): Long = prefs.getPasswordRequestDelay()

    override fun setPasswordRequestDelay(delay: Long) {
        prefs.setPasswordRequestDelay(delay)
    }

    override fun isPasswordEnabled(): Boolean = prefs.isPasswordEnabled()

    override fun setPasswordEnabled(enable: Boolean) {
        prefs.setPasswordEnabled(enable)
    }

    override fun getLastSyncMessage(): SyncProgressMessage? {
        val message = prefs.getLastSyncMessage()
        return if (message.isNullOrBlank()) {
            null
        } else {
            Gson().fromJson(message, SyncProgressMessage::class.java)
        }
    }

    override fun setLastSyncMessage(message: SyncProgressMessage) {
        prefs.setLastSyncMessage(Gson().toJson(message))
    }

    override fun loadHeaderImages(page: Int, perPage: Int): Single<List<MyHeaderImage>> =
            imageApi.getImages()
                    .map { response ->
                        response.images
                                .map { it.toMyHeaderImage() }
                                .sortedByDescending { it.addedTime }
                    }
                    .subscribeOn(rxScheduler)

    override fun saveNotesInCloud(notes: List<MyNote>): Completable =
            cloud.saveNotes(notes, auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun saveCategoriesInCloud(categories: List<MyCategory>): Completable =
            cloud.saveCategories(categories, auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun saveNoteTagsInCloud(noteTags: List<NoteTag>): Completable =
            cloud.saveNoteTags(noteTags, auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun saveTagsInCloud(tags: List<MyTag>): Completable =
            cloud.saveTags(tags, auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun saveAppearancesInCloud(appearances: List<MyNoteAppearance>): Completable =
            cloud.saveAppearances(appearances, auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun saveLocationsInCloud(locations: List<MyLocation>): Completable =
            cloud.saveLocations(locations, auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun saveForecastsInCloud(forecasts: List<MyForecast>): Completable =
            cloud.saveForecasts(forecasts, auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun saveImagesInCloud(images: List<MyImage>): Completable =
            cloud.saveImages(images, auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun saveImagesFilesInCloud(images: List<MyImage>): Completable =
            cloud.saveImagesFiles(images, auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun deleteNotesFromCloud(notes: List<MyNote>): Completable =
            cloud.deleteNotes(notes, auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun deleteCategoriesFromCloud(categories: List<MyCategory>): Completable =
            cloud.deleteCategories(categories, auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun deleteNoteTagsFromCloud(noteTags: List<NoteTag>): Completable =
            cloud.deleteNoteTags(noteTags, auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun deleteTagsFromCloud(tags: List<MyTag>): Completable =
            cloud.deleteTags(tags, auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun deleteAppearancesFromCloud(appearances: List<MyNoteAppearance>): Completable =
            cloud.deleteAppearances(appearances, auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun deleteImagesFromCloud(images: List<MyImage>): Completable =
            cloud.deleteImages(images, auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun getAllNotesFromCloud(): Single<List<MyNote>> =
            cloud.getAllNotes(auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun getAllCategoriesFromCloud(): Single<List<MyCategory>> =
            cloud.getAllCategories(auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun getAllTagsFromCloud(): Single<List<MyTag>> =
            cloud.getAllTags(auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun getAllAppearancesFromCloud(): Single<List<MyNoteAppearance>> =
            cloud.getAllAppearances(auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun getAllNoteTagsFromCloud(): Single<List<NoteTag>> =
            cloud.getAllNoteTags(auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun getAllLocationsFromCloud(): Single<List<MyLocation>> =
            cloud.getAllLocations(auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun getAllForecastsFromCloud(): Single<List<MyForecast>> =
            cloud.getAllForecasts(auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun getAllImagesFromCloud(): Single<List<MyImage>> =
            cloud.getAllImages(auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun getDbProfileCount(): Single<Int> =
            database.profileDao()
                    .getProfileCount()
                    .subscribeOn(rxScheduler)

    override fun loadImageFiles(images: List<MyImage>): Completable =
            cloud.loadImageFiles(images, auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun isSignedIn(): Boolean = auth.isSignedIn()

    override fun signUp(email: String, password: String): Completable =
            auth.signUp(email, password)
                    .map { MyProfile(it.id, it.email, it.photoUri) }
                    .flatMapCompletable { profile ->
                        Completable.concat(listOf(
                                cloud.saveProfile(profile).doOnError { auth.signOut() }
                                        .subscribeOn(rxScheduler),
                                database.profileDao().insert(profile).doOnError { auth.signOut() }
                                        .subscribeOn(rxScheduler)
                        ))
                    }
                    .subscribeOn(rxScheduler)

    override fun signIn(email: String, password: String): Completable =
            auth.signIn(email, password)
                    .flatMap { userId ->
                        cloud.getProfile(userId)
                                .switchIfEmpty(cloud.saveProfile(MyProfile(userId, email))
                                        .andThen(cloud.getProfile(userId)))
                                .toSingle()
                                .doOnError { auth.signOut() }
                    }
                    .flatMapCompletable { profile ->
                        database.profileDao().insert(profile)
                                .doOnError { auth.signOut() }
                                .subscribeOn(rxScheduler)
                    }
                    .subscribeOn(rxScheduler)

    override fun signOut(): Completable =
            auth.signOut()
                    .andThen(database.profileDao().clearProfile())
                    .subscribeOn(rxScheduler)

    override fun updatePassword(oldPassword: String, newPassword: String): Completable =
            auth.updatePassword(oldPassword, newPassword)
                    .subscribeOn(rxScheduler)

    override fun observeAuthState(): Observable<Int> =
            auth.observeAuthState()
                    .map {
                        if (it == AuthHelper.STATE_SIGN_OUT) {
                            DataManager.SIGN_STATE_SIGN_OUT
                        } else {
                            DataManager.SIGN_STATE_SIGN_IN
                        }
                    }
                    .subscribeOn(rxScheduler)

    override fun updateProfile(profile: MyProfile): Completable =
            cloud.saveProfile(profile)
                    .andThen(database.profileDao().update(profile).subscribeOn(rxScheduler))
                    .subscribeOn(rxScheduler)

    override fun sendPasswordResetEmail(email: String): Completable =
            auth.sendPasswordResetEmail(email)
                    .subscribeOn(rxScheduler)

    override fun sendPinResetEmail(): Completable =
            Completable.fromAction {
                auth.sendPinResetEmail(prefs.getBackupEmail(), decryptString(prefs.getPin()))
            }.subscribeOn(rxScheduler)
}