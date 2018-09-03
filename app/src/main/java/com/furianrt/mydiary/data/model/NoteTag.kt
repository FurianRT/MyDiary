package com.furianrt.mydiary.data.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "NoteTag", primaryKeys = ["id_note", "tag_name"])
data class NoteTag(@ColumnInfo(name = "id_note") var noteId: Long,
                   @ColumnInfo(name = "tag_name") var tagName: String) {
}