package com.furianrt.mydiary.data

import android.util.Base64
import com.furianrt.mydiary.BuildConfig
import com.furianrt.mydiary.data.api.forecast.Forecast
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

    private fun decryptString(string: String): String {   //todo сделать более надежный алгоритм шифрования
        val keyBytes = BuildConfig.PREFS_PASSWORD.toByteArray()
        val aesKey = SecretKeySpec(keyBytes, "AES")
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, aesKey)
        val decryptedByteValue = cipher.doFinal(Base64.decode(string.toByteArray(), Base64.DEFAULT))
        return String(decryptedByteValue)
    }

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
            Completable.fromAction { database.noteDao().insert(note) }
                    .subscribeOn(rxScheduler)

    override fun insertNote(notes: List<MyNote>): Completable =
            Completable.fromAction { database.noteDao().insert(notes) }
                    .subscribeOn(rxScheduler)

    override fun insertNoteTag(noteTag: NoteTag): Completable =
            Completable.fromAction { database.noteTagDao().insert(noteTag) }
                    .subscribeOn(rxScheduler)

    override fun insertNoteTag(noteTags: List<NoteTag>): Completable =
            Completable.fromAction { database.noteTagDao().insert(noteTags) }
                    .subscribeOn(rxScheduler)

    override fun insertTag(tag: MyTag): Completable =
            Completable.fromAction { database.tagDao().insert(tag) }
                    .subscribeOn(rxScheduler)

    override fun insertTag(tags: List<MyTag>): Completable =
            Completable.fromAction { database.tagDao().insert(tags) }
                    .subscribeOn(rxScheduler)

    override fun insertImage(image: MyImage): Completable =
            Completable.fromAction { database.imageDao().insert(image) }
                    .subscribeOn(rxScheduler)

    override fun insertImages(images: List<MyImage>): Completable =
            Completable.fromAction { database.imageDao().insert(images) }
                    .subscribeOn(rxScheduler)

    override fun insertHeaderImage(headerImage: MyHeaderImage): Single<Long> =
            Single.fromCallable { database.headerImageDao().insert(headerImage) }
                    .subscribeOn(rxScheduler)

    override fun insertCategory(category: MyCategory): Completable =
            Completable.fromAction { database.categoryDao().insert(category) }
                    .subscribeOn(rxScheduler)

    override fun insertCategory(categories: List<MyCategory>): Completable =
            Completable.fromAction { database.categoryDao().insert(categories) }
                    .subscribeOn(rxScheduler)

    override fun insertAppearance(appearance: MyNoteAppearance): Completable =
            Completable.fromAction { database.appearanceDao().insert(appearance) }
                    .subscribeOn(rxScheduler)

    override fun insertAppearance(appearances: List<MyNoteAppearance>): Completable =
            Completable.fromAction { database.appearanceDao().insert(appearances) }
                    .subscribeOn(rxScheduler)

    override fun insertProfile(profile: MyProfile): Completable =
            Completable.fromAction { database.profileDao().insert(profile) }
                    .subscribeOn(rxScheduler)

    override fun addLocation(location: MyLocation): Completable =
            Completable.fromAction { database.locationDao().insert(location) }
                    .subscribeOn(rxScheduler)

    override fun updateNote(note: MyNote): Completable =
            Completable.fromAction { database.noteDao().update(note.apply { syncWith.clear() }) }
                    .subscribeOn(rxScheduler)

    override fun updateNoteText(noteId: String, title: String, content: String): Completable =
            Completable.fromAction { database.noteDao().updateNoteText(noteId, title, content) }
                    .subscribeOn(rxScheduler)

    override fun updateTag(tag: MyTag): Completable =
            Completable.fromAction { database.tagDao().update(tag.apply { syncWith.clear() }) }
                    .subscribeOn(rxScheduler)

    override fun updateImage(image: MyImage): Completable =
            Completable.fromAction {
                database.imageDao().update(image.apply { syncWith.clear() })
            }.subscribeOn(rxScheduler)

    override fun updateImage(images: List<MyImage>): Completable =
            Completable.fromAction {
                database.imageDao().update(images.map { it.apply { syncWith.clear() } })
            }.subscribeOn(rxScheduler)

    override fun updateImageSync(images: List<MyImage>): Completable =
            Completable.fromAction { database.imageDao().update(images) }
                    .subscribeOn(rxScheduler)

    override fun updateCategory(category: MyCategory): Completable =
            Completable.fromAction { database.categoryDao().update(category.apply { syncWith.clear() }) }
                    .andThen(database.noteDao().getAllNotes())
                    .first(emptyList())
                    .flatMapObservable { Observable.fromIterable(it) }
                    .filter { it.categoryId == category.id }
                    .map { it.apply { it.syncWith.clear() } }
                    .flatMapSingle {
                        Completable.fromAction { database.noteDao().update(it) }
                                .toSingleDefault(true)
                    }
                    .collectInto(mutableListOf<Boolean>()) { l, i -> l.add(i) }
                    .ignoreElement()
                    .subscribeOn(rxScheduler)

    override fun updateAppearance(appearance: MyNoteAppearance): Completable =
            Completable.fromAction { database.appearanceDao().update(appearance.apply { syncWith.clear() }) }
                    .andThen(database.noteDao().getNote(appearance.appearanceId))
                    .map { it.apply { it.syncWith.clear() } }
                    .flatMapCompletable { Completable.fromAction { database.noteDao().update(it) } }
                    .subscribeOn(rxScheduler)

    override fun updateDbProfile(profile: MyProfile): Completable =
            Completable.fromAction { database.profileDao().update(profile) }
                    .subscribeOn(rxScheduler)

    override fun updateNotesSync(notes: List<MyNote>): Completable =
            Completable.fromAction { database.noteDao().update(notes) }
                    .subscribeOn(rxScheduler)

    override fun updateAppearancesSync(appearances: List<MyNoteAppearance>): Completable =
            Completable.fromAction { database.appearanceDao().update(appearances) }
                    .subscribeOn(rxScheduler)

    override fun updateCategoriesSync(categories: List<MyCategory>): Completable =
            Completable.fromAction { database.categoryDao().update(categories) }
                    .subscribeOn(rxScheduler)

    override fun updateNoteTagsSync(noteTags: List<NoteTag>): Completable =
            Completable.fromAction { database.noteTagDao().update(noteTags) }
                    .subscribeOn(rxScheduler)

    override fun updateTagsSync(tags: List<MyTag>): Completable =
            Completable.fromAction { database.tagDao().update(tags) }
                    .subscribeOn(rxScheduler)

    override fun deleteTag(tag: MyTag): Completable =
            Completable.fromAction { database.tagDao().delete(tag.id) }
                    .andThen(Completable.fromAction { database.noteTagDao().deleteWithTagId(tag.id) })
                    .subscribeOn(rxScheduler)

    override fun deleteNote(note: MyNote): Completable =
            Completable.fromAction { database.noteDao().delete(note.id) }
                    .andThen(Completable.fromAction { database.noteTagDao().deleteWithNoteId(note.id) })
                    .andThen(Completable.fromAction { database.appearanceDao().delete(note.id) })
                    .andThen(Completable.fromAction { database.imageDao().deleteByNoteId(note.id) })
                    .andThen(database.imageDao().getImagesForNote(note.id))
                    .first(emptyList())
                    .map { images -> images.map { it.name } }
                    .flatMapCompletable { Completable.fromCallable { storage.deleteFiles(it) } }
                    .subscribeOn(rxScheduler)

    override fun deleteImage(image: MyImage): Completable =
            Completable.fromAction { database.imageDao().delete(image.name) }
                    .subscribeOn(rxScheduler)

    override fun deleteImage(images: List<MyImage>): Completable =
            Completable.fromAction { database.imageDao().delete(images.map { it.name }) }
                    .subscribeOn(rxScheduler)

    override fun deleteCategory(category: MyCategory): Completable =
            Completable.fromAction { database.categoryDao().delete(category.id) }
                    .subscribeOn(rxScheduler)

    override fun clearDbProfile(): Completable =
            Completable.fromAction { database.profileDao().clearProfile() }
                    .subscribeOn(rxScheduler)

    override fun replaceNoteTags(noteId: String, tags: List<MyTag>): Completable =
            Completable.fromAction {
                database.noteTagDao()
                        .replaceNoteTags(noteId, tags.map { it.apply { syncWith.clear() } })
            }.subscribeOn(rxScheduler)

    override fun deleteAllTagsForNote(noteId: String): Completable =
            Completable.fromAction { database.noteTagDao().deleteAllTagsForNote(noteId) }
                    .subscribeOn(rxScheduler)

    override fun deleteImageFromStorage(fileName: String): Single<Boolean> =
            Single.fromCallable { storage.deleteFile(fileName) }
                    .subscribeOn(rxScheduler)

    override fun cleanupNotes(): Completable =
            Completable.fromAction { database.noteDao().cleanup() }
                    .subscribeOn(rxScheduler)

    override fun cleanupNoteTags(): Completable =
            Completable.fromAction { database.noteTagDao().cleanup() }
                    .subscribeOn(rxScheduler)

    override fun cleanupAppearances(): Completable =
            Completable.fromAction { database.appearanceDao().cleanup() }
                    .subscribeOn(rxScheduler)

    override fun cleanupCategories(): Completable =
            Completable.fromAction { database.categoryDao().cleanup() }
                    .subscribeOn(rxScheduler)

    override fun cleanupTags(): Completable =
            Completable.fromAction { database.tagDao().cleanup() }
                    .subscribeOn(rxScheduler)

    override fun cleanupImages(): Completable =
            Completable.fromAction { database.imageDao().cleanup() }
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

    override fun getAllTags(): Single<List<MyTag>> =
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

    override fun getForecast(lat: Double, lon: Double): Single<Forecast?> =
            weatherApi.getForecast(lat, lon)
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

    override fun getAllImagesFromCloud(): Single<List<MyImage>> =
            cloud.getAllImages(auth.getUserId())
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
                                cloud.saveProfile(profile)
                                        .doOnError { auth.signOut() },
                                Completable.fromAction { database.profileDao().insert(profile) }
                                        .subscribeOn(rxScheduler)))
                    }
                    .subscribeOn(rxScheduler)

    override fun signIn(email: String, password: String): Completable =
            auth.signIn(email, password)
                    .flatMap { cloud.getProfile(it).doOnError { auth.signOut() } }
                    .flatMapCompletable { profile ->
                        Completable.fromAction { database.profileDao().insert(profile) }
                                .subscribeOn(rxScheduler)
                    }
                    .subscribeOn(rxScheduler)

    override fun signOut(): Completable =
            auth.signOut()
                    .andThen(Completable.fromAction { database.profileDao().clearProfile() }
                            .subscribeOn(rxScheduler))
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
                    .andThen(Completable.fromAction { database.profileDao().update(profile) }
                            .subscribeOn(rxScheduler))
                    .subscribeOn(rxScheduler)

    override fun sendPasswordResetEmail(email: String): Completable =
            auth.sendPasswordResetEmail(email)
                    .subscribeOn(rxScheduler)
}