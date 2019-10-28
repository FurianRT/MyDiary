/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.model.entity

import android.graphics.Color
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = MyCategory.TABLE_NAME)
data class MyCategory(
        @ColumnInfo(name = FIELD_ID) @PrimaryKey(autoGenerate = false) var id: String = "",
        @ColumnInfo(name = FIELD_NAME) var name: String = "",
        @ColumnInfo(name = FIELD_COLOR) var color: Int = DEFAULT_COLOR,
        @ColumnInfo(name = FIELD_SYNC_WITH) var syncWith: MutableList<String> = mutableListOf(),
        @ColumnInfo(name = FIELD_IS_DELETED) var isDeleted: Boolean = false
) : Parcelable {

    companion object {
        val DEFAULT_COLOR = Color.parseColor("#808080")
        const val TABLE_NAME = "Categories"
        const val FIELD_ID = "id_category"
        const val FIELD_NAME = "name_category"
        const val FIELD_COLOR = "color"
        const val FIELD_SYNC_WITH = "category_sync_with"
        const val FIELD_IS_DELETED = "is_category_deleted"
        const val DEFAULT_SYNC_EMAIL = "default"
    }

    fun isSync(email: String) = syncWith.contains(DEFAULT_SYNC_EMAIL) || syncWith.contains(email)
}