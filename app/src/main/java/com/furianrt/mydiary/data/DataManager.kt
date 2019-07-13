package com.furianrt.mydiary.data

import com.furianrt.mydiary.data.model.*
import io.reactivex.*

interface DataManager {
    companion object {
        const val SIGN_STATE_SIGN_OUT = 0
        const val SIGN_STATE_SIGN_IN = 1
        const val WEATHER_UNITS_CELSIUS = 0
        const val WEATHER_UNITS_FAHRENHEIT = 1
    }
    fun insertNote(note: MyNote): Completable
    fun insertNote(notes: List<MyNote>): Completable
    fun insertNoteTag(noteTag: NoteTag): Completable
    fun insertNoteTag(noteTags: List<NoteTag>): Completable
    fun insertNoteLocation(noteLocation: NoteLocation): Completable
    fun insertNoteLocation(noteLocation: List<NoteLocation>): Completable
    fun insertLocation(locations: List<MyLocation>): Completable
    fun insertForecast(forecasts: List<MyForecast>): Completable
    fun insertTag(tag: MyTag): Completable
    fun insertTag(tags: List<MyTag>): Completable
    fun insertImage(image: MyImage): Completable
    fun insertImages(images: List<MyImage>): Completable
    fun insertHeaderImage(headerImage: MyHeaderImage): Completable
    fun insertCategory(category: MyCategory): Completable
    fun insertCategory(categories: List<MyCategory>): Completable
    fun insertAppearance(appearance: MyNoteAppearance): Completable
    fun insertAppearance(appearances: List<MyNoteAppearance>): Completable
    fun insertProfile(profile: MyProfile): Completable
    fun insertForecast(forecast: MyForecast): Completable
    fun updateNote(note: MyNote): Completable
    fun updateNoteText(noteId: String, title: String, content: String): Completable
    fun updateTag(tag: MyTag): Completable
    fun updateImage(image: MyImage): Completable
    fun updateImage(images: List<MyImage>): Completable
    fun updateImageSync(images: List<MyImage>): Completable
    fun updateCategory(category: MyCategory): Completable
    fun updateAppearance(appearance: MyNoteAppearance): Completable
    fun updateDbProfile(profile: MyProfile): Completable
    fun updateNotesSync(notes: List<MyNote>): Completable
    fun updateAppearancesSync(appearances: List<MyNoteAppearance>): Completable
    fun updateCategoriesSync(categories: List<MyCategory>): Completable
    fun updateTagsSync(tags: List<MyTag>): Completable
    fun updateNoteTagsSync(noteTags: List<NoteTag>): Completable
    fun updateNoteLocationsSync(noteLocations: List<NoteLocation>): Completable
    fun updateLocationsSync(locations: List<MyLocation>): Completable
    fun updateForecastsSync(forecasts: List<MyForecast>): Completable
    fun deleteTag(tag: MyTag): Completable
    fun deleteNote(noteId: String): Completable
    fun deleteImage(imageName: String): Completable
    fun deleteImage(imageNames: List<String>): Completable
    fun deleteImageFromStorage(fileName: String): Single<Boolean>
    fun deleteCategory(category: MyCategory): Completable
    fun deleteNotesFromCloud(notes: List<MyNote>): Completable
    fun deleteCategoriesFromCloud(categories: List<MyCategory>): Completable
    fun deleteNoteTagsFromCloud(noteTags: List<NoteTag>): Completable
    fun deleteNoteLocationsFromCloud(noteLocations: List<NoteLocation>): Completable
    fun deleteLocationsFromCloud(locations: List<MyLocation>): Completable
    fun deleteForecastsFromCloud(forecasts: List<MyForecast>): Completable
    fun deleteTagsFromCloud(tags: List<MyTag>): Completable
    fun deleteAppearancesFromCloud(appearances: List<MyNoteAppearance>): Completable
    fun deleteImagesFromCloud(images: List<MyImage>): Completable
    fun deleteNoteTag(noteId: String, tagId: String): Completable
    fun clearDbProfile(): Completable
    fun cleanupNotes(): Completable
    fun cleanupNoteTags(): Completable
    fun cleanupAppearances(): Completable
    fun cleanupCategories(): Completable
    fun cleanupTags(): Completable
    fun cleanupImages(): Completable
    fun cleanupLocations(): Completable
    fun cleanupNoteLocations(): Completable
    fun cleanupForecasts(): Completable
    fun getAllNotes(): Flowable<List<MyNote>>
    fun getDeletedNotes(): Flowable<List<MyNote>>
    fun getTagsForNote(noteId: String): Flowable<List<MyTag>>
    fun getDeletedNoteTags(): Flowable<List<NoteTag>>
    fun getDeletedNoteLocations(): Flowable<List<NoteLocation>>
    fun getDeletedLocations(): Flowable<List<MyLocation>>
    fun getDeletedForecasts(): Flowable<List<MyForecast>>
    fun getNote(noteId: String): Single<MyNote>
    fun getNoteAsList(noteId: String): Flowable<List<MyNote>>
    fun getAllTags(): Flowable<List<MyTag>>
    fun getDeletedTags(): Flowable<List<MyTag>>
    fun getLocationsForNote(noteId: String): Flowable<List<MyLocation>>
    fun loadForecast(lat: Double, lon: Double): Single<MyForecast>
    fun getMood(moodId: Int): Single<MyMood>
    fun getCategory(categoryId: String): Single<MyCategory>
    fun getAllCategories(): Flowable<List<MyCategory>>
    fun getDeletedCategories(): Flowable<List<MyCategory>>
    fun getAllMoods(): Single<List<MyMood>>
    fun getAllImages(): Flowable<List<MyImage>>
    fun getDeletedImages(): Flowable<List<MyImage>>
    fun getNoteAppearance(noteId: String): Flowable<MyNoteAppearance>
    fun getAllNoteAppearances(): Flowable<List<MyNoteAppearance>>
    fun getDeletedAppearances(): Flowable<List<MyNoteAppearance>>
    fun getImagesForNote(noteId: String): Flowable<List<MyImage>>
    fun getImageCount(): Flowable<Int>
    fun getAllNotesWithProp(): Flowable<List<MyNoteWithProp>>
    fun getHeaderImages(): Flowable<List<MyHeaderImage>>
    fun getAllNoteTags(): Flowable<List<NoteTag>>
    fun getAllNoteLocations(): Flowable<List<NoteLocation>>
    fun getDbProfile(): Observable<MyProfile>
    fun getAllNotesFromCloud(): Single<List<MyNote>>
    fun getAllCategoriesFromCloud(): Single<List<MyCategory>>
    fun getAllTagsFromCloud(): Single<List<MyTag>>
    fun getAllAppearancesFromCloud(): Single<List<MyNoteAppearance>>
    fun getAllNoteTagsFromCloud(): Single<List<NoteTag>>
    fun getAllNoteLocationsFromCloud(): Single<List<NoteLocation>>
    fun getAllLocationsFromCloud(): Single<List<MyLocation>>
    fun getAllForecastsFromCloud(): Single<List<MyForecast>>
    fun getAllImagesFromCloud(): Single<List<MyImage>>
    fun getAllDbLocations(): Flowable<List<MyLocation>>
    fun getAllDbForecasts(): Single<List<MyForecast>>
    fun getDbProfileCount(): Single<Int>
    fun getPrimaryColor(): Int
    fun getAccentColor(): Int
    fun getWeatherUnits(): Int
    fun getTextColor(): Int
    fun getSurfaceTextColor(): Int
    fun getTextSize(): Int
    fun getNoteBackgroundColor(): Int
    fun getNoteTextBackgroundColor(): Int
    fun getPin(): Single<String>
    fun setPin(pin: String): Completable
    fun getBackupEmail(): String
    fun setBackupEmail(email: String)
    fun is24TimeFormat(): Boolean
    fun isSortDesc(): Boolean
    fun isWeatherEnabled(): Boolean
    fun isLocationEnabled(): Boolean
    fun isMoodEnabled(): Boolean
    fun isAuthorized(): Boolean
    fun setAuthorized(authorized: Boolean)
    fun getPasswordRequestDelay(): Long
    fun setPasswordRequestDelay(delay: Long)
    fun isPinEnabled(): Boolean
    fun setPinEnabled(enable: Boolean)
    fun isDailyImageEnabled(): Boolean
    fun setLastAppLaunchTime(time: Long)
    fun getLastAppLaunchTime(): Long
    fun getNumberOfLaunches(): Int
    fun setNumberOfLaunches(count: Int)
    fun isFingerprintEnabled(): Boolean
    fun isProfileExists(email: String): Single<Boolean>
    fun setSortDesc(desc: Boolean)
    fun setLastSyncMessage(message: SyncProgressMessage?)
    fun getLastSyncMessage(): SyncProgressMessage?
    fun findNote(noteId: String): Maybe<MyNote>
    fun insertLocation(location: MyLocation): Completable
    fun loadHeaderImages(page: Int = 1, perPage: Int = 20): Single<List<MyHeaderImage>>
    fun saveImageToStorage(image: MyImage): Single<MyImage>
    fun saveNotesInCloud(notes: List<MyNote>): Completable
    fun saveCategoriesInCloud(categories: List<MyCategory>): Completable
    fun saveTagsInCloud(tags: List<MyTag>): Completable
    fun saveNoteTagsInCloud(noteTags: List<NoteTag>): Completable
    fun saveNoteLocationsInCloud(noteLocations: List<NoteLocation>): Completable
    fun saveAppearancesInCloud(appearances: List<MyNoteAppearance>): Completable
    fun saveLocationsInCloud(locations: List<MyLocation>): Completable
    fun saveForecastsInCloud(forecasts: List<MyForecast>): Completable
    fun saveImagesInCloud(images: List<MyImage>): Completable
    fun saveImagesFilesInCloud(images: List<MyImage>): Completable
    fun loadImageFiles(images: List<MyImage>): Completable
    fun isSignedIn(): Boolean
    fun signUp(email: String, password: String): Completable
    fun signIn(email: String, password: String): Completable
    fun signOut(): Completable
    fun updatePassword(oldPassword: String, newPassword: String): Completable
    fun observeAuthState(): Observable<Int>
    fun updateProfile(profile: MyProfile): Completable
    fun sendPasswordResetEmail(email: String): Completable
    fun sendPinResetEmail(): Completable
}