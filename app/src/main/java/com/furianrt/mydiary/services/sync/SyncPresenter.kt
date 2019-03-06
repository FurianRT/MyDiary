package com.furianrt.mydiary.services.sync

import android.util.Log
import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyImage
import com.furianrt.mydiary.data.model.MyProfile
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction

class SyncPresenter(
        private val mDataManager: DataManager
) : SyncContract.Presenter() {

    companion object {
        private const val TAG = "SyncPresenter"
    }

    override fun onStartCommand() {
        view?.sendProgressUpdate(ProgressMessage(ProgressMessage.PROFILE_CHECK))
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
            view?.sendProgressUpdate(ProgressMessage(
                    taskIndex = ProgressMessage.UNKNOWN,
                    hasError = true
            ))
            view?.close()
        }))
    }

    private fun checkProfile(): Observable<ProgressMessage> =
            mDataManager.getDbProfile()
                    .firstOrError()
                    .flatMap { mDataManager.getCloudProfile(it.email).toSingle() }
                    .zipWith(mDataManager.getDbProfile().firstOrError(), BiFunction<MyProfile, MyProfile, Boolean>
                    { cloudProfile, localProfile ->
                        return@BiFunction cloudProfile.passwordHash == localProfile.passwordHash
                    })
                    .flatMapObservable { isPasswordValid ->
                        return@flatMapObservable if (isPasswordValid) {
                            Observable.just(ProgressMessage(ProgressMessage.SYNC_NOTES, 5))
                        } else {
                            Observable.just(ProgressMessage(
                                    taskIndex = ProgressMessage.PROFILE_CHECK,
                                    hasError = true
                            ))
                        }
                    }
                    .onErrorReturnItem(ProgressMessage(
                            taskIndex = ProgressMessage.PROFILE_CHECK,
                            hasError = true
                    ))

    private fun syncNotes(): Observable<ProgressMessage> =
            mDataManager.getAllNotes()
                    .first(emptyList())
                    .map { notes -> notes.filter { !it.isSync } }
                    .flatMapCompletable { notes ->
                        val notesSync = notes.map { it.apply { it.isSync = true } }
                        return@flatMapCompletable Completable.merge(listOf(
                                mDataManager.updateNotesSync(notesSync),
                                mDataManager.saveNotesInCloud(notesSync)))
                    }
                    .andThen(mDataManager.getDeletedNotes().first(emptyList()))
                    .flatMapCompletable { mDataManager.deleteNotesFromCloud(it) }
                    .andThen(mDataManager.getAllNotesFromCloud())
                    .flatMapCompletable { mDataManager.insertNote(it) }
                    .andThen(Observable.just(ProgressMessage(ProgressMessage.SYNC_APPEARANCE, 20)))
                    .onErrorReturnItem(ProgressMessage(
                            taskIndex = ProgressMessage.SYNC_NOTES,
                            hasError = true
                    ))

    private fun syncAppearance(): Observable<ProgressMessage> =
            mDataManager.getAllNoteAppearances()
                    .first(emptyList())
                    .map { appearances -> appearances.filter { !it.isSync } }
                    .flatMapCompletable { appearances ->
                        val appearancesSync = appearances.map { it.apply { it.isSync = true } }
                        return@flatMapCompletable Completable.merge(listOf(
                                mDataManager.updateAppearancesSync(appearancesSync),
                                mDataManager.saveAppearancesInCloud(appearancesSync)))
                    }
                    .andThen(mDataManager.getDeletedAppearances().first(emptyList()))
                    .flatMapCompletable { mDataManager.deleteAppearancesFromCloud(it) }
                    .andThen(mDataManager.getAllAppearancesFromCloud())
                    .flatMapCompletable { mDataManager.insertAppearance(it) }
                    .andThen(Observable.just(ProgressMessage(ProgressMessage.SYNC_CATEGORIES, 35)))
                    .onErrorReturnItem(ProgressMessage(
                            taskIndex = ProgressMessage.SYNC_APPEARANCE,
                            hasError = true
                    ))

    private fun syncCategories(): Observable<ProgressMessage> =
            mDataManager.getAllCategories()
                    .first(emptyList())
                    .map { categories -> categories.filter { !it.isSync } }
                    .flatMapCompletable { categories ->
                        val categoriesSync = categories.map { it.apply { it.isSync = true } }
                        return@flatMapCompletable Completable.merge(listOf(
                                mDataManager.updateCategoriesSync(categoriesSync),
                                mDataManager.saveCategoriesInCloud(categoriesSync)))
                    }
                    .andThen(mDataManager.getDeletedCategories().first(emptyList()))
                    .flatMapCompletable { mDataManager.deleteCategoriesFromCloud(it) }
                    .andThen(mDataManager.getAllCategoriesFromCloud())
                    .flatMapCompletable { mDataManager.insertCategory(it) }
                    .andThen(Observable.just(ProgressMessage(ProgressMessage.SYNC_TAGS, 50)))
                    .onErrorReturnItem(ProgressMessage(
                            taskIndex = ProgressMessage.SYNC_CATEGORIES,
                            hasError = true
                    ))

    private fun syncTags(): Observable<ProgressMessage> =
            mDataManager.getAllTags()
                    .map { tags -> tags.filter { !it.isSync } }
                    .flatMapCompletable { tags ->
                        val tagsSync = tags.map { it.apply { it.isSync = true } }
                        return@flatMapCompletable Completable.merge(listOf(
                                mDataManager.updateTagsSync(tagsSync),
                                mDataManager.saveTagsInCloud(tagsSync)))
                    }
                    .andThen(mDataManager.getDeletedTags().first(emptyList()))
                    .flatMapCompletable { mDataManager.deleteTagsFromCloud(it) }
                    .andThen(mDataManager.getAllTagsFromCloud())
                    .flatMapCompletable { mDataManager.insertTag(it) }
                    .andThen(Observable.just(ProgressMessage(ProgressMessage.SYNC_NOTE_TAGS, 65)))
                    .onErrorReturnItem(ProgressMessage(
                            taskIndex = ProgressMessage.SYNC_TAGS,
                            hasError = true
                    ))

    private fun syncNoteTags(): Observable<ProgressMessage> =
            mDataManager.getAllNoteTags()
                    .first(emptyList())
                    .map { noteTags -> noteTags.filter { !it.isSync } }
                    .flatMapCompletable { noteTags ->
                        val noteTagsSync = noteTags.map { it.apply { it.isSync = true } }
                        return@flatMapCompletable Completable.merge(listOf(
                                mDataManager.updateNoteTagsSync(noteTagsSync),
                                mDataManager.saveNoteTagsInCloud(noteTagsSync)))
                    }
                    .andThen(mDataManager.getDeletedNoteTags().first(emptyList()))
                    .flatMapCompletable { mDataManager.deleteNoteTagsFromCloud(it) }
                    .andThen(mDataManager.getAllNoteTagsFromCloud())
                    .flatMapCompletable { mDataManager.insertNoteTag(it) }
                    .andThen(Observable.just(ProgressMessage(ProgressMessage.SYNC_IMAGES, 80)))
                    .onErrorReturnItem(ProgressMessage(
                            taskIndex = ProgressMessage.SYNC_NOTE_TAGS,
                            hasError = true
                    ))

    private fun syncImages(): Observable<ProgressMessage> =
            mDataManager.getAllImages()
                    .first(emptyList())
                    .map { images -> images.filter { !it.isSync } }
                    .flatMapCompletable { images ->
                        val imagesSync = images.map { it.apply { it.isSync = true } }
                        return@flatMapCompletable Completable.concat(listOf(
                                mDataManager.saveImagesInCloud(imagesSync),
                                mDataManager.updateImagesSync(imagesSync)
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
                    .andThen(Observable.just(ProgressMessage(ProgressMessage.CLEANUP, 95)))
                    .onErrorReturnItem(ProgressMessage(
                            taskIndex = ProgressMessage.SYNC_IMAGES,
                            hasError = true
                    ))

    private fun cleanup(): Observable<ProgressMessage> =
            mDataManager.cleanupNotes()
                    .andThen(mDataManager.cleanupAppearances())
                    .andThen(mDataManager.cleanupCategories())
                    .andThen(mDataManager.cleanupNoteTags())
                    .andThen(mDataManager.cleanupTags())
                    .andThen(mDataManager.cleanupImages())
                    .andThen(Observable.just(ProgressMessage(ProgressMessage.SYNC_FINISHED, 100)))
                    .onErrorReturnItem(ProgressMessage(
                            taskIndex = ProgressMessage.CLEANUP,
                            hasError = true
                    ))
}