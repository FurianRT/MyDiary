package com.furianrt.mydiary.data.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.furianrt.mydiary.data.api.Forecast
import java.io.Serializable

@Entity(tableName = "Notes")
data class MyNote(@ColumnInfo(name = "title") var title: String,
                  @ColumnInfo(name = "content") var content: String,
                  @ColumnInfo(name = "time") var time: Long) : Serializable {

    @ColumnInfo(name = "id_note")
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @ColumnInfo(name = "mood")
    var idMood: Long = 0

    @ColumnInfo(name = "location")
    var locationId: Long = 0

    @ColumnInfo(name = "category")
    var categoryId: Long = 0

    @ColumnInfo(name = "forecast")
    var forecast: Forecast? = null
}