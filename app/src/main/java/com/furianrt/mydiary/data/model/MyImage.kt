package com.furianrt.mydiary.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(
        tableName = "Images",
        foreignKeys = [
            ForeignKey(
                    entity = MyNote::class,
                    parentColumns = ["id_note"],
                    childColumns = ["id_note"],
                    onDelete = ForeignKey.CASCADE
            )
        ]
)
@Parcelize
data class MyImage(
        @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "name") var name: String,
        @ColumnInfo(name = "url") var url: String,
        @ColumnInfo(name = "id_note", index = true) var noteId: String,
        @ColumnInfo(name = "time_added", index = true) var addedTime: Long,
        @ColumnInfo(name = "order") var order: Int = 0
) : Parcelable