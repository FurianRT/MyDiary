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

@Parcelize
@Entity(tableName = MyTag.TABLE_NAME)
data class MyTag(
        @ColumnInfo(name = FIELD_ID) @PrimaryKey(autoGenerate = false) var id: String = "",
        @ColumnInfo(name = FIELD_NAME) var name: String = "",
        @ColumnInfo(name = FIELD_SYNC_WITH) var syncWith: MutableList<String> = mutableListOf(),
        @ColumnInfo(name = FIELD_IS_DELETED) var isDeleted: Boolean = false
) : Parcelable {

    companion object {
        const val TABLE_NAME = "Tags"
        const val FIELD_ID = "id_tag"
        const val FIELD_NAME = "name_tag"
        const val FIELD_SYNC_WITH = "tag_sync_with"
        const val FIELD_IS_DELETED = "is_tag_deleted"
        const val DEFAULT_SYNC_EMAIL = "default"
    }

    fun isSync(email: String) = syncWith.contains(DEFAULT_SYNC_EMAIL) || syncWith.contains(email)
}