package com.furianrt.mydiary.service

import android.util.Log
import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyProfile
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.functions.BiFunction

class SyncPresenter(
        private val mDataManager: DataManager
) : SyncContract.Presenter() {

    companion object {
        private const val TAG = "SyncPresenter"
    }

    class WrongCredentialException : Throwable()

    override fun onStartCommand() {
        addDisposable(checkProfile()
                .flatMapCompletable { isPasswordValid ->
                    Log.e(TAG, "3")
                    if (isPasswordValid) {
                        return@flatMapCompletable syncNotes()
                    } else {
                        throw WrongCredentialException()
                    }
                }
                .andThen(syncAppearance())
                .andThen(syncCategories())
                .andThen(syncTags())
                .andThen(syncNoteTags())
                .andThen(cleanup())
                .subscribe({
                    Log.e(TAG, "Sync finished")
                    view?.close()
                }, {
                    it.printStackTrace()
                    Log.e(TAG, "Sync error")
                    view?.close()
                }))
    }

    private fun checkProfile(): Single<Boolean> =
            mDataManager.getDbProfile()
                    .firstOrError()
                    .flatMap {
                        Log.e(TAG, "1")
                        return@flatMap mDataManager.getCloudProfile(it.email).toSingle()
                    }
                    .zipWith(mDataManager.getDbProfile().firstOrError(), BiFunction<MyProfile, MyProfile, Boolean>
                    { cloudProfile, localProfile ->
                        Log.e(TAG, "2")
                        return@BiFunction cloudProfile.passwordHash == localProfile.passwordHash
                    })

    private fun syncNotes(): Completable =
            mDataManager.getAllNotes()
                    .first(emptyList())
                    .map { notes -> notes.filter { !it.isSync } }
                    .flatMapCompletable { notes ->
                        Log.e(TAG, "4")
                        val notesSync = notes.map { it.apply { it.isSync = true } }
                        return@flatMapCompletable Completable.merge(listOf(
                                mDataManager.updateNotesSync(notesSync),
                                mDataManager.saveNotesInCloud(notesSync)))
                    }
                    .andThen(mDataManager
                            .getDeletedNotes()
                            .first(emptyList()))
                    .flatMapCompletable {
                        Log.e(TAG, "5")
                        return@flatMapCompletable mDataManager.deleteNotesFromCloud(it)
                    }
                    .andThen(mDataManager.getAllNotesFromCloud())
                    .flatMapCompletable { mDataManager.insertNote(it) }

    private fun syncAppearance(): Completable =
            mDataManager.getAllNoteAppearances()
                    .first(emptyList())
                    .map { appearances -> appearances.filter { !it.isSync } }
                    .flatMapCompletable { appearances ->
                        Log.e(TAG, "6")
                        val appearancesSync = appearances.map { it.apply { it.isSync = true } }
                        return@flatMapCompletable Completable.merge(listOf(
                                mDataManager.updateAppearancesSync(appearancesSync),
                                mDataManager.saveAppearancesInCloud(appearancesSync)))
                    }
                    .andThen(mDataManager
                            .getDeletedAppearances()
                            .first(emptyList()))
                    .flatMapCompletable {
                        Log.e(TAG, "7")
                        return@flatMapCompletable mDataManager.deleteAppearancesFromCloud(it)
                    }
                    .andThen(mDataManager.getAllAppearancesFromCloud())
                    .flatMapCompletable { mDataManager.insertAppearance(it) }

    private fun syncCategories(): Completable =
            mDataManager.getAllCategories()
                    .first(emptyList())
                    .map { categories -> categories.filter { !it.isSync } }
                    .flatMapCompletable { categories ->
                        Log.e(TAG, "8")
                        val categoriesSync = categories.map { it.apply { it.isSync = true } }
                        return@flatMapCompletable Completable.merge(listOf(
                                mDataManager.updateCategoriesSync(categoriesSync),
                                mDataManager.saveCategoriesInCloud(categoriesSync)))
                    }
                    .andThen(mDataManager
                            .getDeletedCategories()
                            .first(emptyList()))
                    .flatMapCompletable {
                        Log.e(TAG, "9")
                        return@flatMapCompletable mDataManager.deleteCategoriesFromCloud(it)
                    }
                    .andThen(mDataManager.getAllCategoriesFromCloud())
                    .flatMapCompletable { mDataManager.insertCategory(it) }

    private fun syncTags(): Completable =
            mDataManager.getAllTags()
                    .map { tags -> tags.filter { !it.isSync } }
                    .flatMapCompletable { tags ->
                        Log.e(TAG, "10")
                        val tagsSync = tags.map { it.apply { it.isSync = true } }
                        return@flatMapCompletable Completable.merge(listOf(
                                mDataManager.updateTagsSync(tagsSync),
                                mDataManager.saveTagsInCloud(tagsSync)))
                    }
                    .andThen(mDataManager
                            .getDeletedTags()
                            .first(emptyList()))
                    .flatMapCompletable {
                        Log.e(TAG, "11")
                        return@flatMapCompletable mDataManager.deleteTagsFromCloud(it)
                    }
                    .andThen(mDataManager.getAllTagsFromCloud())
                    .flatMapCompletable { mDataManager.insertTag(it) }

    private fun syncNoteTags(): Completable =
            mDataManager.getAllNoteTags()
                    .first(emptyList())
                    .map { noteTags -> noteTags.filter { !it.isSync } }
                    .flatMapCompletable { noteTags ->
                        Log.e(TAG, "12")
                        val noteTagsSync = noteTags.map { it.apply { it.isSync = true } }
                        return@flatMapCompletable Completable.merge(listOf(
                                mDataManager.updateNoteTagsSync(noteTagsSync),
                                mDataManager.saveNoteTagsInCloud(noteTagsSync)))
                    }
                    .andThen(mDataManager
                            .getDeletedNoteTags()
                            .first(emptyList()))
                    .flatMapCompletable {
                        Log.e(TAG, "13")
                        return@flatMapCompletable mDataManager.deleteNoteTagsFromCloud(it)
                    }
                    .andThen(mDataManager.getAllNoteTagsFromCloud())
                    .flatMapCompletable { mDataManager.insertNoteTag(it) }

    private fun cleanup(): Completable =
            mDataManager.cleanupNotes()
                    .andThen(mDataManager.cleanupAppearances())
                    .andThen(mDataManager.cleanupCategories())
                    .andThen(mDataManager.cleanupNoteTags())
                    .andThen(mDataManager.cleanupTags())
}