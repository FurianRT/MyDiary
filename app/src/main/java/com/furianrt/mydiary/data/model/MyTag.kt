package com.furianrt.mydiary.data.model

import android.arch.persistence.room.*

@Entity(tableName = "Tags", indices = [Index("id_note")], foreignKeys =
[ForeignKey(entity = MyNote::class, parentColumns = ["id"], childColumns = ["id_note"],
        onDelete = ForeignKey.CASCADE)])
data class MyTag(@ColumnInfo(name = "id_note") val noteId: Long,
                 @ColumnInfo(name = "name") val name: String) {

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}