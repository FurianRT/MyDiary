package com.furianrt.mydiary.data.model

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
        @ColumnInfo(name = FIELD_IS_SYNC) var isSync: Boolean = false,
        @ColumnInfo(name = FIELD_IS_DELETED) var isDeleted: Boolean = false
) : Parcelable {

    companion object {
        const val DEFAULT_COLOR = Color.BLUE
        const val TABLE_NAME = "Categories"
        const val FIELD_ID = "id_category"
        const val FIELD_NAME = "name_category"
        const val FIELD_COLOR = "color"
        const val FIELD_IS_SYNC = "is_category_sync"
        const val FIELD_IS_DELETED = "is_category_deleted"
    }
}