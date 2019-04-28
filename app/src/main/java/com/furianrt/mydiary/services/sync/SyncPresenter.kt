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
        private val mDataManager: DataManager
) : SyncContract.Presenter() {

    companion object {
        private const val TAG = "SyncPresenter"
    }

    private class SyncGoneWrongException(val taskIndex: Int) : Throwable()

    private lateinit var mProfile: MyProfile

    override fun onStartCommand() {
        view?.sendProgressUpdate(SyncProgressMessage(SyncProgressMessage.PROFILE_CHECK))
        addDisposable(Observable.concat(listOf(
                checkProfile(),
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
            if (it is SyncGoneWrongException) {
                view?.sendProgressUpdate(SyncProgressMessage(
                        taskIndex = it.taskIndex,
                        hasError = true
                ))
            } else {
                view?.sendProgressUpdate(SyncProgressMessage(
                        taskIndex = SyncProgressMessage.UNKNOWN,
                        hasError = true
                ))
            }
            view?.close()
        }))
    }

    private fun checkProfile(): Observable<SyncProgressMessage> =
            mDataManager.getDbProfile()
                    .firstOrError()
                    .flatMap { mDataManager.getCloudProfile(it.email) }
                    .zipWith(mDataManager.getDbProfile().firstOrError(), BiFunction<MyProfile, MyProfile, Boolean>
                    { cloudProfile, localProfile ->
                        mProfile = localProfile
                        return@BiFunction cloudProfile.passwordHash == localProfile.passwordHash
                    })
                    .flatMapObservable { isPasswordValid ->
                        return@flatMapObservable if (isPasswordValid) {
                            Observable.just(SyncProgressMessage(SyncProgressMessage.SYNC_NOTES, 5))
                        } else {
                            Observable.just(SyncProgressMessage(
                                    taskIndex = SyncProgressMessage.PROFILE_CHECK,
                                    hasError = true
                            ))
                        }
                    }
                    .onErrorResumeNext(Observable.error(SyncGoneWrongException(SyncProgressMessage.PROFILE_CHECK)))

    private fun syncNotes(): Observable<SyncProgressMessage> =
            mDataManager.getAllNotes()
                    .first(emptyList())
                    .map { notes -> notes.filter { !it.isSync(mProfile.email) } }
                    .flatMapCompletable { notes ->
                        val notesSync = notes.map { it.apply { it.syncWith.add(mProfile.email) } }
                        return@flatMapCompletable Completable.concat(listOf(
                                mDataManager.saveNotesInCloud(notesSync),
                                mDataManager.updateNotesSync(notesSync)))
                    }
                    .andThen(mDataManager.getDeletedNotes().first(emptyList()))
                    .flatMapCompletable { mDataManager.deleteNotesFromCloud(it) }
                    .andThen(mDataManager.getAllNotesFromCloud())
                    .flatMapCompletable { mDataManager.insertNote(it) }
                    .andThen(Observable.just(SyncProgressMessage(SyncProgressMessage.SYNC_APPEARANCE, 20)))
                    .onErrorResumeNext(Observable.error(SyncGoneWrongException(SyncProgressMessage.SYNC_NOTES)))

    private fun syncAppearance(): Observable<SyncProgressMessage> =
            mDataManager.getAllNoteAppearances()
                    .first(emptyList())
                    .map { appearances -> appearances.filter { !it.isSync(mProfile.email) } }
                    .flatMapCompletable { appearances ->
                        val appearancesSync = appearances.map { it.apply { it.syncWith.add(mProfile.email) } }
                        return@flatMapCompletable Completable.concat(listOf(
                                mDataManager.saveAppearancesInCloud(appearancesSync),
                                mDataManager.updateAppearancesSync(appearancesSync)))
                    }
                    .andThen(mDataManager.getDeletedAppearances().first(emptyList()))
                    .flatMapCompletable { mDataManager.deleteAppearancesFromCloud(it) }
                    .andThen(mDataManager.getAllAppearancesFromCloud())
                    .flatMapCompletable { mDataManager.insertAppearance(it) }
                    .andThen(Observable.just(SyncProgressMessage(SyncProgressMessage.SYNC_CATEGORIES, 35)))
                    .onErrorResumeNext(Observable.error(SyncGoneWrongException(SyncProgressMessage.SYNC_APPEARANCE)))

    private fun syncCategories(): Observable<SyncProgressMessage> =
            mDataManager.getAllCategories()
                    .first(emptyList())
                    .map { categories -> categories.filter { !it.isSync(mProfile.email) } }
                    .flatMapCompletable { categories ->
                        val categoriesSync = categories.map { it.apply { it.syncWith.add(mProfile.email) } }
                        return@flatMapCompletable Completable.concat(listOf(
                                mDataManager.saveCategoriesInCloud(categoriesSync),
                                mDataManager.updateCategoriesSync(categoriesSync)))
                    }
                    .andThen(mDataManager.getDeletedCategories().first(emptyList()))
                    .flatMapCompletable { mDataManager.deleteCategoriesFromCloud(it) }
                    .andThen(mDataManager.getAllCategoriesFromCloud())
                    .flatMapCompletable { mDataManager.insertCategory(it) }
                    .andThen(Observable.just(SyncProgressMessage(SyncProgressMessage.SYNC_TAGS, 50)))
                    .onErrorResumeNext(Observable.error(SyncGoneWrongException(SyncProgressMessage.SYNC_CATEGORIES)))

    private fun syncTags(): Observable<SyncProgressMessage> =
            mDataManager.getAllTags()
                    .map { tags -> tags.filter { !it.isSync(mProfile.email) } }
                    .flatMapCompletable { tags ->
                        val tagsSync = tags.map { it.apply { it.syncWith.add(mProfile.email) } }
                        return@flatMapCompletable Completable.concat(listOf(
                                mDataManager.saveTagsInCloud(tagsSync),
                                mDataManager.updateTagsSync(tagsSync)))
                    }
                    .andThen(mDataManager.getDeletedTags().first(emptyList()))
                    .flatMapCompletable { mDataManager.deleteTagsFromCloud(it) }
                    .andThen(mDataManager.getAllTagsFromCloud())
                    .flatMapCompletable { mDataManager.insertTag(it) }
                    .andThen(Observable.just(SyncProgressMessage(SyncProgressMessage.SYNC_NOTE_TAGS, 65)))
                    .onErrorResumeNext(Observable.error(SyncGoneWrongException(SyncProgressMessage.SYNC_TAGS)))

    private fun syncNoteTags(): Observable<SyncProgressMessage> =
            mDataManager.getAllNoteTags()
                    .first(emptyList())
                    .map { noteTags -> noteTags.filter { !it.isSync(mProfile.email) } }
                    .flatMapCompletable { noteTags ->
                        val noteTagsSync = noteTags.map { it.apply { it.syncWith.add(mProfile.email) } }
                        return@flatMapCompletable Completable.concat(listOf(
                                mDataManager.saveNoteTagsInCloud(noteTagsSync),
                                mDataManager.updateNoteTagsSync(noteTagsSync)))
                    }
                    .andThen(mDataManager.getDeletedNoteTags().first(emptyList()))
                    .flatMapCompletable { mDataManager.deleteNoteTagsFromCloud(it) }
                    .andThen(mDataManager.getAllNoteTagsFromCloud())
                    .flatMapCompletable { mDataManager.insertNoteTag(it) }
                    .andThen(Observable.just(SyncProgressMessage(SyncProgressMessage.SYNC_IMAGES, 80)))
                    .onErrorResumeNext(Observable.error(SyncGoneWrongException(SyncProgressMessage.SYNC_NOTE_TAGS)))

    private fun syncImages(): Observable<SyncProgressMessage> =
            mDataManager.getAllImages()
                    .first(emptyList())
                    .map { images -> images.filter { !it.isSync(mProfile.email) } }
                    .flatMapCompletable { images ->
                        val imagesSync = images.map { it.apply { it.syncWith.add(mProfile.email) } }
                        return@flatMapCompletable Completable.concat(listOf(
                                mDataManager.saveImagesFilesInCloud(imagesSync.filter { !it.isFileSync(mProfile.email) }),
                                mDataManager.saveImagesInCloud(imagesSync.map { it.apply { it.fileSyncWith.add(mProfile.email) } }),
                                mDataManager.updateImageSync(imagesSync.map { it.apply { it.fileSyncWith.add(mProfile.email) } })
                        ))
                    }
                    .andThen(mDataManager.getDeletedImages().first(emptyList()))
                    .flatMapCompletable { mDataManager.deleteImagesFromCloud(it) }
                    .andThen(Single.zip(
                            mDataManager.getAllImagesFromCloud(),
                            mDataManager.getAllImages().firstOrError(),
                            BiFunction<List<MyImage>, List<MyImage>, List<MyImage>> { cloudImages, dbImages ->
                                cloudImages.toMutableList().apply { removeAll(dbImages) }
                            }
                    ))
                    .flatMapCompletable {
                        Completable.concat(listOf(
                                mDataManager.loadImageFiles(it),
                                mDataManager.insertImages(it)
                        ))
                    }
                    .andThen(Observable.just(SyncProgressMessage(SyncProgressMessage.CLEANUP, 95)))
                    .onErrorResumeNext(Observable.error(SyncGoneWrongException(SyncProgressMessage.SYNC_IMAGES)))

    private fun cleanup(): Observable<SyncProgressMessage> =
            mDataManager.cleanupNotes()
                    .andThen(mDataManager.cleanupAppearances())
                    .andThen(mDataManager.cleanupCategories())
                    .andThen(mDataManager.cleanupNoteTags())
                    .andThen(mDataManager.cleanupTags())
                    .andThen(mDataManager.cleanupImages())
                    .andThen(mDataManager.getDbProfile().firstOrError())
                    .flatMapCompletable { profile ->
                        profile.lastSyncTime = DateTime.now().millis
                        return@flatMapCompletable Completable.concat(listOf(
                                mDataManager.saveProfile(profile),
                                mDataManager.updateDbProfile(profile)
                        ))
                    }
                    .andThen(Observable.just(SyncProgressMessage(SyncProgressMessage.SYNC_FINISHED, 100)))
                    .onErrorResumeNext(Observable.error(SyncGoneWrongException(SyncProgressMessage.CLEANUP)))
}