package com.furianrt.mydiary.data.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Index

@Entity(tableName = "NoteTag",
        primaryKeys = ["id_tag", "id_note"],
        indices = [Index("id_tag"), Index("id_note")],
        foreignKeys = [
            ForeignKey(entity = MyTag::class, parentColumns = ["id_tag"],
                    childColumns = ["id_tag"], onDelete = ForeignKey.CASCADE),
            ForeignKey(entity = MyNote::class, parentColumns = ["id_note"],
                    childColumns = ["id_note"], onDelete = ForeignKey.CASCADE)
        ])
data class NoteTag(@ColumnInfo(name = "id_note") var noteId: Long,
                   @ColumnInfo(name = "id_tag") var tagId: Long) {
}