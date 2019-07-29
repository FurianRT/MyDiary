/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import org.joda.time.DateTime

@Parcelize
@Entity(tableName = MyNote.TABLE_NAME)
data class MyNote(
        @ColumnInfo(name = FIELD_ID) @PrimaryKey(autoGenerate = false) var id: String = "",
        @ColumnInfo(name = FIELD_TITLE) var title: String = "",
        @ColumnInfo(name = FIELD_CONTENT) var content: String = "",
        @ColumnInfo(name = FIELD_TIME) var time: Long = DateTime.now().millis,
        @ColumnInfo(name = FIELD_MOOD) var moodId: Int = 0,
        @ColumnInfo(name = FIELD_CATEGORY) var categoryId: String = "",
        @ColumnInfo(name = FIELD_CREATION_TIME) var creationTime: Long = DateTime.now().millis,
        @ColumnInfo(name = FIELD_SYNC_WITH) var syncWith: MutableList<String> = mutableListOf(),
        @ColumnInfo(name = FIELD_IS_DELETED) var isDeleted: Boolean = false
) : Parcelable {

    companion object {
        const val TABLE_NAME = "Notes"
        const val FIELD_ID = "id_note"
        const val FIELD_TITLE = "title"
        const val FIELD_CONTENT = "content"
        const val FIELD_TIME = "time"
        const val FIELD_MOOD = "mood"
        const val FIELD_CATEGORY = "category"
        const val FIELD_CREATION_TIME = "creation_time"
        const val FIELD_SYNC_WITH = "note_sync_with"
        const val FIELD_IS_DELETED = "is_note_deleted"
        const val DEFAULT_SYNC_EMAIL = "default"
    }

    fun isSync(email: String) = syncWith.contains(DEFAULT_SYNC_EMAIL) || syncWith.contains(email)
}