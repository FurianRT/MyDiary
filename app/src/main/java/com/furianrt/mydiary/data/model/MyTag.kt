package com.furianrt.mydiary.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.furianrt.mydiary.data.model.MyTag.Companion.TABLE_NAME
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = TABLE_NAME)
data class MyTag(
        @ColumnInfo(name = FIELD_ID) @PrimaryKey(autoGenerate = false) var id: String = "",
        @ColumnInfo(name = FIELD_NAME) var name: String = "",
        @Ignore var isChecked: Boolean = false,
        @ColumnInfo(name = FIELD_IS_SYNC) var isSync: Boolean = false,
        @ColumnInfo(name = FIELD_IS_DELETED) var idDeleted: Boolean = false
) : Parcelable {

    companion object {
        const val TABLE_NAME = "Tags"
        const val FIELD_ID = "id_tag"
        const val FIELD_NAME = "name_tag"
        const val FIELD_IS_SYNC = "is_tag_sync"
        const val FIELD_IS_DELETED = "is_tag_deleted"
    }
}