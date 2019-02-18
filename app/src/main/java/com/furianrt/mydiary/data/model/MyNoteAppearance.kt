package com.furianrt.mydiary.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(
        tableName = "NoteAppearances",
        foreignKeys = [
            ForeignKey(
                    entity = MyNote::class,
                    parentColumns = ["id_note"],
                    childColumns = ["id_appearance"],
                    onDelete = ForeignKey.CASCADE
            )
        ]
)
@Parcelize
data class MyNoteAppearance(
        @ColumnInfo(name = "id_appearance") @PrimaryKey(autoGenerate = false) var appearanceId: String,
        @ColumnInfo(name = "background_color") var background: Int? = null,
        @ColumnInfo(name = "text_background_color") var textBackground: Int? = null,
        @ColumnInfo(name = "text_color") var textColor: Int? = null,
        @ColumnInfo(name = "text_size") var textSize: Int? = null
) : Parcelable