package com.furianrt.mydiary.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.furianrt.mydiary.data.api.forecast.Forecast
import kotlinx.android.parcel.Parcelize
import org.joda.time.DateTime

@Parcelize
@Entity(tableName = "Notes")
open class MyNote(
        @ColumnInfo(name = "id_note") @PrimaryKey(autoGenerate = false) var id: String = "",
        @ColumnInfo(name = "title") var title: String = "",
        @ColumnInfo(name = "content") var content: String = "",
        @ColumnInfo(name = "time") var time: Long = DateTime.now().millis,
        @ColumnInfo(name = "mood") var moodId: Int = 0,
        @ColumnInfo(name = "category") var categoryId: String = "",
        @ColumnInfo(name = "creation_time") var creationTime: Long = DateTime.now().millis,
        @ColumnInfo(name = "location") var locationName: String? = null,
        @ColumnInfo(name = "forecast") var forecast: Forecast? = null,
        @ColumnInfo(name = "is_note_sync") var isSync: Boolean = false,
        @ColumnInfo(name = "is_note_deleted") var isDeleted: Boolean = false
) : Parcelable