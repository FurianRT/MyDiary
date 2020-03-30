/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.model.source.database.dao

import androidx.room.*
import com.furianrt.mydiary.model.entity.MyTextSpan
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
abstract class SpanDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(textSpan: MyTextSpan): Completable

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(textSpans: List<MyTextSpan>): Completable

    @Update
    abstract fun update(textSpans: List<MyTextSpan>): Completable

    @Query("DELETE FROM ${MyTextSpan.TABLE_NAME} WHERE ${MyTextSpan.FIELD_NOTE_ID} = :noteId")
    abstract fun deletePermanently(noteId: String): Completable

    @Query("UPDATE ${MyTextSpan.TABLE_NAME} SET ${MyTextSpan.FIELD_IS_DELETED} = 1, ${MyTextSpan.FIELD_SYNC_WITH} = '[]' " +
            "WHERE ${MyTextSpan.FIELD_NOTE_ID} = :noteId")
    abstract fun delete(noteId: String): Completable

    @Query("SELECT * FROM ${MyTextSpan.TABLE_NAME} WHERE ${MyTextSpan.FIELD_IS_DELETED} = 0")
    abstract fun getAllTextSpans(): Flowable<List<MyTextSpan>>

    @Query("SELECT * FROM ${MyTextSpan.TABLE_NAME} WHERE ${MyTextSpan.FIELD_NOTE_ID} = :noteId AND ${MyTextSpan.FIELD_IS_DELETED} = 0")
    abstract fun getTextSpans(noteId: String): Flowable<List<MyTextSpan>>

    @Query("SELECT * FROM ${MyTextSpan.TABLE_NAME} WHERE ${MyTextSpan.FIELD_IS_DELETED} = 1")
    abstract fun getDeletedTextSpans(): Flowable<List<MyTextSpan>>

    @Query("SELECT * FROM ${MyTextSpan.TABLE_NAME} WHERE ${MyTextSpan.FIELD_IS_DELETED} = 1 AND ${MyTextSpan.FIELD_NOTE_ID} = :noteId")
    abstract fun getDeletedTextSpans(noteId: String): Flowable<List<MyTextSpan>>

    @Query("DELETE FROM ${MyTextSpan.TABLE_NAME} WHERE ${MyTextSpan.FIELD_IS_DELETED} = 1")
    abstract fun cleanup(): Completable
}