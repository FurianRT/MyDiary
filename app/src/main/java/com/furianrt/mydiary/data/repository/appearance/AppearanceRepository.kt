package com.furianrt.mydiary.data.repository.appearance

import com.furianrt.mydiary.data.model.MyNoteAppearance
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface AppearanceRepository {
    fun insertAppearance(appearance: MyNoteAppearance): Completable
    fun insertAppearance(appearances: List<MyNoteAppearance>): Completable
    fun updateAppearance(appearance: MyNoteAppearance): Completable
    fun updateAppearancesSync(appearances: List<MyNoteAppearance>): Completable
    fun deleteAppearancesFromCloud(appearances: List<MyNoteAppearance>): Completable
    fun cleanupAppearances(): Completable
    fun getNoteAppearance(noteId: String): Flowable<MyNoteAppearance>
    fun getAllNoteAppearances(): Flowable<List<MyNoteAppearance>>
    fun getDeletedAppearances(): Flowable<List<MyNoteAppearance>>
    fun getAllAppearancesFromCloud(): Single<List<MyNoteAppearance>>
    fun saveAppearancesInCloud(appearances: List<MyNoteAppearance>): Completable
    fun getPrimaryColor(): Int
    fun getAccentColor(): Int
    fun getTextColor(): Int
    fun getSurfaceTextColor(): Int
    fun getTextSize(): Int
    fun getNoteBackgroundColor(): Int
    fun getNoteTextBackgroundColor(): Int
}