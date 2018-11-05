package com.furianrt.mydiary.data.model

import android.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
        tableName = "NoteAppearances",
        foreignKeys = [
            ForeignKey(
                    entity = MyNote::class,
                    parentColumns = ["id_note"],
                    childColumns = ["id_note"],
                    onDelete = ForeignKey.CASCADE
            )
        ]
)
data class MyNoteAppearance(
        @ColumnInfo(name = "id_note") @PrimaryKey(autoGenerate = false) var noteId: String,
        @ColumnInfo(name = "background_color") var background: Int = Color.LTGRAY,
        @ColumnInfo(name = "text_color") var textColor: Int = Color.BLACK,
        @ColumnInfo(name = "text_size") var textSize: Int = 16
)