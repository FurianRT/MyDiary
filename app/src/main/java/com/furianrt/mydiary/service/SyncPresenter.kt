package com.furianrt.mydiary.service

import android.util.Log
import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.*
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function4

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
                        return@flatMapCompletable mDataManager.saveNotesInCloud(notes
                                .map { it.apply { isSync = true } })
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

    private fun syncCategories(): Completable =
            mDataManager.getAllCategories()
                    .first(emptyList())
                    .map { categories -> categories.filter { !it.isSync } }
                    .flatMapCompletable { categories ->
                        Log.e(TAG, "6")
                        return@flatMapCompletable mDataManager.saveCategoriesInCloud(categories
                                .map { it.apply { isSync = true } })
                    }
                    .andThen(mDataManager
                            .getDeletedCategories()
                            .first(emptyList()))
                    .flatMapCompletable {
                        Log.e(TAG, "7")
                        return@flatMapCompletable mDataManager.deleteCategoriesFromCloud(it)
                    }
                    .andThen(mDataManager.getAllCategoriesFromCloud())
                    .flatMapCompletable { mDataManager.insertCategory(it) }

    private fun syncTags(): Completable =
            mDataManager.getAllTags()
                    .map { tags -> tags.filter { !it.isSync } }
                    .flatMapCompletable { tags ->
                        Log.e(TAG, "8")
                        return@flatMapCompletable mDataManager.saveTagsInCloud(tags
                                .map { it.apply { isSync = true } })
                    }
                    .andThen(mDataManager
                            .getDeletedTags()
                            .first(emptyList()))
                    .flatMapCompletable {
                        Log.e(TAG, "9")
                        return@flatMapCompletable mDataManager.deleteTagsFromCloud(it)
                    }
                    .andThen(mDataManager.getAllTagsFromCloud())
                    .flatMapCompletable { mDataManager.insertTag(it) }

    private fun syncAppearance(): Completable =
            mDataManager.getDeletedNotes()
                    .first(emptyList())
                    .map { list -> list.map { MyNoteAppearance(it.id) } }
                    .flatMapCompletable { mDataManager.deleteAppearancesFromCloud(it) }
                    .andThen(mDataManager.getAllNoteAppearances()
                            .first(emptyList())
                            .map { appearance -> appearance.filter { !it.isSync } })
                    .flatMapCompletable { appeatances ->
                        mDataManager.saveAppearancesInCloud(appeatances
                                .map { it.apply { isSync = true } })
                    }
                    .andThen(mDataManager.getAllAppearancesFromCloud())
                    .flatMapCompletable { mDataManager.insertAppearance(it) }

    private fun syncNoteTags(): Completable =
            Single.zip(mDataManager.getDeletedNotes().first(emptyList()),
                    mDataManager.getDeletedTags().first(emptyList()),
                    mDataManager.getDeletedNoteTags().first(emptyList()),
                    mDataManager.getAllNoteTags().first(emptyList()),
                    Function4<List<MyNote>, List<MyTag>, List<NoteTag>, List<NoteTag>, List<NoteTag>>
                    { deletedNotes, deletedTags, deletedNoteTags, noteTags ->
                        Log.e(TAG, "10")
                        return@Function4 noteTags
                                .filter { noteTag ->
                                    deletedNotes.find {
                                        it.id == noteTag.noteId
                                    } != null || deletedTags.find {
                                        it.id == noteTag.tagId
                                    } != null
                                }
                                .toMutableList()
                                .apply { addAll(deletedNoteTags) }

                    })
                    .flatMapCompletable {
                        Log.e(TAG, "11")
                        return@flatMapCompletable mDataManager.deleteNoteTagsFromCloud(it)
                    }
                    .andThen(mDataManager.getAllNoteTags().first(emptyList()))
                    .map { noteTags -> noteTags.filter { !it.isSync } }
                    .flatMapCompletable { noteTags ->
                        mDataManager.saveNoteTagsInCloud(noteTags
                                .map { it.apply { isSync = true } })
                    }
                    .andThen(mDataManager.getAllNoteTagsFromCloud())
                    .flatMapCompletable {
                        Log.e(TAG, "12")
                        return@flatMapCompletable mDataManager.insertNoteTag(it)
                    }

    private fun cleanup(): Completable =
            mDataManager.cleanupNotes()
                    .andThen(mDataManager.cleanupAppearances())
                    .andThen(mDataManager.cleanupCategories())
                    .andThen(mDataManager.cleanupNoteTags())
                    .andThen(mDataManager.cleanupTags())
}