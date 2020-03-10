/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.model.gateway.appearance

import android.graphics.Color
import com.furianrt.mydiary.model.entity.MyNoteAppearance
import com.furianrt.mydiary.model.source.preferences.PreferencesSource
import com.google.common.base.Optional
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface AppearanceGateway {
    fun insertAppearance(appearance: MyNoteAppearance): Completable
    fun insertAppearance(appearances: List<MyNoteAppearance>): Completable
    fun updateAppearance(appearance: MyNoteAppearance): Completable
    fun updateAppearancesSync(appearances: List<MyNoteAppearance>): Completable
    fun deleteAppearance(noteId: String): Completable
    fun deleteAppearancesFromCloud(appearances: List<MyNoteAppearance>): Completable
    fun cleanupAppearances(): Completable
    fun getNoteAppearance(noteId: String): Flowable<Optional<MyNoteAppearance>>
    fun getAllNoteAppearances(): Flowable<List<MyNoteAppearance>>
    fun getDeletedAppearances(): Flowable<List<MyNoteAppearance>>
    fun getAllAppearancesFromCloud(): Single<List<MyNoteAppearance>>
    fun saveAppearancesInCloud(appearances: List<MyNoteAppearance>): Completable
    fun getPrimaryColor(): Int
    fun getAccentColor(): Int
    fun getTextColor(): Int
    fun setTextColor(color: Int)
    fun getSurfaceTextColor(): Int
    fun setSurfaceTextColor(color: Int)
    fun getTextSize(): Int
    fun setTextSize(size: Int)
    fun getNoteBackgroundColor(): Int
    fun setNoteBackgroundColor(color: Int)
    fun getNoteTextBackgroundColor(): Int
    fun setNoteTextBackgroundColor(color: Int)

    companion object {
        val DEFAULT_NOTE_BACKGROUND_COLOR = Color.parseColor("#f2f2f2")
        const val DEFAULT_NOTE_TEXT_BACKGROUND_COLOR = Color.WHITE
        const val DEFAULT_NOTE_TEXT_SIZE = PreferencesSource.DEFAULT_TEXT_SIZE
        const val DEFAULT_NOTE_TEXT_COLOR = Color.BLACK
    }
}