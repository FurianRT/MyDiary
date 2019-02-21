package com.furianrt.mydiary.data.model

import android.graphics.Color
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "Categories")
data class MyCategory(
        @ColumnInfo(name = "id_category") @PrimaryKey(autoGenerate = true) var id: Long = 0,
        @ColumnInfo(name = "name_category") var name: String = "",
        @ColumnInfo(name = "color") var color: Int = DEFAULT_COLOR,
        @ColumnInfo(name = "is_category_sync") var isSync: Boolean = false,
        @ColumnInfo(name = "is_category_deleted") var idDeleted: Boolean = false
) : Parcelable {

    companion object {
        const val DEFAULT_COLOR = Color.BLUE
    }
}