package com.furianrt.mydiary.data.model

import android.arch.persistence.room.*

@Entity(tableName = "Locations", indices = [Index("id_note")], foreignKeys =
[ForeignKey(entity = MyNote::class, parentColumns = ["id_note"], childColumns = ["id_note"],
        onDelete = ForeignKey.CASCADE)])
data class MyLocation(@ColumnInfo(name = "id_note") var noteId: Long,
                      @ColumnInfo(name = "name") var name: String,
                      @ColumnInfo(name = "lat") var lat: Double,
                      @ColumnInfo(name = "lon") var lon: Double) {

    @ColumnInfo(name = "id_location")
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}