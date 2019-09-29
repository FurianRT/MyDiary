/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.data.database

import androidx.room.*
import com.furianrt.mydiary.data.model.MyTextSpan
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

    @Query("DELETE FROM TextSpans WHERE id_note = :noteId")
    abstract fun deletePermanently(noteId: String): Completable

    @Query("UPDATE TextSpans SET is_span_deleted = 1, span_sync_with = '[]' WHERE id_note = :noteId")
    abstract fun delete(noteId: String): Completable

    @Query("SELECT * FROM TextSpans WHERE is_span_deleted = 0")
    abstract fun getAllTextSpans(): Flowable<List<MyTextSpan>>

    @Query("SELECT * FROM TextSpans WHERE is_span_deleted = 1")
    abstract fun getDeletedTextSpans(): Flowable<List<MyTextSpan>>

    @Query("DELETE FROM TextSpans WHERE is_span_deleted = 1")
    abstract fun cleanup(): Completable
}