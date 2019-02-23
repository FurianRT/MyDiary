package com.furianrt.mydiary.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "Tags")
data class MyTag(
        @ColumnInfo(name = "id_tag") @PrimaryKey(autoGenerate = false) var id: String = "",
        @ColumnInfo(name = "name_tag") var name: String = "",
        @Ignore var isChecked: Boolean = false,
        @ColumnInfo(name = "is_tag_sync") var isSync: Boolean = false,
        @ColumnInfo(name = "is_tag_deleted") var idDeleted: Boolean = false
) : Parcelable