package com.furianrt.mydiary.services.sync

import android.util.Log
import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyImage
import com.furianrt.mydiary.data.model.MyProfile
import com.furianrt.mydiary.data.model.SyncProgressMessage
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import org.joda.time.DateTime

class SyncPresenter(
        private val dataManager: DataManager
) : SyncContract.Presenter() {

    companion object {
        private const val TAG = "SyncPresenter"
    }

    private class SyncGoneWrongException(val taskIndex: Int) : Throwable()

    private lateinit var mProfile: MyProfile

    override fun onStartCommand() {
        view?.sendProgressUpdate(SyncProgressMessage(SyncProgressMessage.PROFILE_CHECK))
        addDisposable(Observable.concat(listOf(
                getProfile(),
                syncNotes(),
                syncAppearance(),
                syncCategories(),
                syncTags(),
                syncNoteTags(),
                syncImages(),
                cleanup()
        )).doOnComplete {
            Log.e(TAG, "Sync finished")
            view?.close()
        }.subscribe({
            view?.sendProgressUpdate(it)
        }, {
            it.printStackTrace()
            Log.e(TAG, "Sync error")
            view?.sendProgressUpdate(if (it is SyncGoneWrongException) {
                SyncProgressMessage(taskIndex = it.taskIndex, hasError = true)
            } else {
                SyncProgressMessage(taskIndex = SyncProgressMessage.UNKNOWN, hasError = true)
            })
            view?.close()
        }))
    }

    private fun getProfile(): Observable<SyncProgressMessage> =
            dataManager.getDbProfile()
                    .firstOrError()
                    .flatMapObservable { profile ->
                        mProfile = profile
                        Observable.just(SyncProgressMessage(SyncProgressMessage.SYNC_NOTES, 5))
                    }
                    .onErrorResumeNext(Observable.error(SyncGoneWrongException(SyncProgressMessage.PROFILE_CHECK)))

    private fun syncNotes(): Observable<SyncProgressMessage> =
            dataManager.getAllNotes()
                    .first(emptyList())
                    .map { notes -> notes.filter { !it.isSync(mProfile.email) } }
                    .flatMapCompletable { notes ->
                        val notesSync = notes.map { it.apply { it.syncWith.add(mProfile.email) } }
                        return@flatMapCompletable Completable.concat(listOf(
                                dataManager.saveNotesInCloud(notesSync),
                                dataManager.updateNotesSync(notesSync)))
                    }
                    .andThen(dataManager.getDeletedNotes().first(emptyList()))
                    .flatMapCompletable { dataManager.deleteNotesFromCloud(it) }
                    .andThen(dataManager.getAllNotesFromCloud())
                    .flatMapCompletable { dataManager.insertNote(it) }
                    .andThen(Observable.just(SyncProgressMessage(SyncProgressMessage.SYNC_APPEARANCE, 20)))
                    .onErrorResumeNext(Observable.error(SyncGoneWrongException(SyncProgressMessage.SYNC_NOTES)))

    private fun syncAppearance(): Observable<SyncProgressMessage> =
            dataManager.getAllNoteAppearances()
                    .first(emptyList())
                    .map { appearances -> appearances.filter { !it.isSync(mProfile.email) } }
                    .flatMapCompletable { appearances ->
                        val appearancesSync = appearances.map { it.apply { it.syncWith.add(mProfile.email) } }
                        return@flatMapCompletable Completable.concat(listOf(
                                dataManager.saveAppearancesInCloud(appearancesSync),
                                dataManager.updateAppearancesSync(appearancesSync)))
                    }
                    .andThen(dataManager.getDeletedAppearances().first(emptyList()))
                    .flatMapCompletable { dataManager.deleteAppearancesFromCloud(it) }
                    .andThen(dataManager.getAllAppearancesFromCloud())
                    .flatMapCompletable { dataManager.insertAppearance(it) }
                    .andThen(Observable.just(SyncProgressMessage(SyncProgressMessage.SYNC_CATEGORIES, 35)))
                    .onErrorResumeNext(Observable.error(SyncGoneWrongException(SyncProgressMessage.SYNC_APPEARANCE)))

    private fun syncCategories(): Observable<SyncProgressMessage> =
            dataManager.getAllCategories()
                    .first(emptyList())
                    .map { categories -> categories.filter { !it.isSync(mProfile.email) } }
                    .flatMapCompletable { categories ->
                        val categoriesSync = categories.map { it.apply { it.syncWith.add(mProfile.email) } }
                        return@flatMapCompletable Completable.concat(listOf(
                                dataManager.saveCategoriesInCloud(categoriesSync),
                                dataManager.updateCategoriesSync(categoriesSync)))
                    }
                    .andThen(dataManager.getDeletedCategories().first(emptyList()))
                    .flatMapCompletable { dataManager.deleteCategoriesFromCloud(it) }
                    .andThen(dataManager.getAllCategoriesFromCloud())
                    .flatMapCompletable { dataManager.insertCategory(it) }
                    .andThen(Observable.just(SyncProgressMessage(SyncProgressMessage.SYNC_TAGS, 50)))
                    .onErrorResumeNext(Observable.error(SyncGoneWrongException(SyncProgressMessage.SYNC_CATEGORIES)))

    private fun syncTags(): Observable<SyncProgressMessage> =
            dataManager.getAllTags()
                    .map { tags -> tags.filter { !it.isSync(mProfile.email) } }
                    .flatMapCompletable { tags ->
                        val tagsSync = tags.map { it.apply { it.syncWith.add(mProfile.email) } }
                        return@flatMapCompletable Completable.concat(listOf(
                                dataManager.saveTagsInCloud(tagsSync),
                                dataManager.updateTagsSync(tagsSync)))
                    }
                    .andThen(dataManager.getDeletedTags().first(emptyList()))
                    .flatMapCompletable { dataManager.deleteTagsFromCloud(it) }
                    .andThen(dataManager.getAllTagsFromCloud())
                    .flatMapCompletable { dataManager.insertTag(it) }
                    .andThen(Observable.just(SyncProgressMessage(SyncProgressMessage.SYNC_NOTE_TAGS, 65)))
                    .onErrorResumeNext(Observable.error(SyncGoneWrongException(SyncProgressMessage.SYNC_TAGS)))

    private fun syncNoteTags(): Observable<SyncProgressMessage> =
            dataManager.getAllNoteTags()
                    .first(emptyList())
                    .map { noteTags -> noteTags.filter { !it.isSync(mProfile.email) } }
                    .flatMapCompletable { noteTags ->
                        val noteTagsSync = noteTags.map { it.apply { it.syncWith.add(mProfile.email) } }
                        return@flatMapCompletable Completable.concat(listOf(
                                dataManager.saveNoteTagsInCloud(noteTagsSync),
                                dataManager.updateNoteTagsSync(noteTagsSync)))
                    }
                    .andThen(dataManager.getDeletedNoteTags().first(emptyList()))
                    .flatMapCompletable { dataManager.deleteNoteTagsFromCloud(it) }
                    .andThen(dataManager.getAllNoteTagsFromCloud())
                    .flatMapCompletable { dataManager.insertNoteTag(it) }
                    .andThen(Observable.just(SyncProgressMessage(SyncProgressMessage.SYNC_IMAGES, 80)))
                    .onErrorResumeNext(Observable.error(SyncGoneWrongException(SyncProgressMessage.SYNC_NOTE_TAGS)))

    private fun syncImages(): Observable<SyncProgressMessage> =
            dataManager.getAllImages()
                    .first(emptyList())
                    .map { images -> images.filter { !it.isSync(mProfile.email) } }
                    .flatMapCompletable { images ->
                        val imagesSync = images.map { it.apply { it.syncWith.add(mProfile.email) } }
                        return@flatMapCompletable Completable.concat(listOf(
                                dataManager.saveImagesFilesInCloud(imagesSync.filter { !it.isFileSync(mProfile.email) }),
                                dataManager.saveImagesInCloud(imagesSync.map { it.apply { it.fileSyncWith.add(mProfile.email) } }),
                                dataManager.updateImageSync(imagesSync.map { it.apply { it.fileSyncWith.add(mProfile.email) } })
                        ))
                    }
                    .andThen(dataManager.getDeletedImages().first(emptyList()))
                    .flatMapCompletable { dataManager.deleteImagesFromCloud(it) }
                    .andThen(Single.zip(
                            dataManager.getAllImagesFromCloud(),
                            dataManager.getAllImages().firstOrError(),
                            BiFunction<List<MyImage>, List<MyImage>, List<MyImage>> { cloudImages, dbImages ->
                                cloudImages.toMutableList().apply { removeAll(dbImages) }
                            }
                    ))
                    .flatMapCompletable {
                        Completable.concat(listOf(
                                dataManager.loadImageFiles(it),
                                dataManager.insertImages(it)
                        ))
                    }
                    .andThen(Observable.just(SyncProgressMessage(SyncProgressMessage.CLEANUP, 95)))
                    .onErrorResumeNext(Observable.error(SyncGoneWrongException(SyncProgressMessage.SYNC_IMAGES)))

    private fun cleanup(): Observable<SyncProgressMessage> =
            dataManager.cleanupNotes()
                    .andThen(dataManager.cleanupAppearances())
                    .andThen(dataManager.cleanupCategories())
                    .andThen(dataManager.cleanupNoteTags())
                    .andThen(dataManager.cleanupTags())
                    .andThen(dataManager.cleanupImages())
                    .andThen(dataManager.getDbProfile().firstOrError())
                    .flatMapCompletable { profile ->
                        profile.lastSyncTime = DateTime.now().millis
                        return@flatMapCompletable dataManager.updateProfile(profile)
                    }
                    .andThen(Observable.just(SyncProgressMessage(SyncProgressMessage.SYNC_FINISHED, 100)))
                    .onErrorResumeNext(Observable.error(SyncGoneWrongException(SyncProgressMessage.CLEANUP)))
}