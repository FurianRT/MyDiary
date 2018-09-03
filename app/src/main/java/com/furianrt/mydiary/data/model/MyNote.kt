package com.furianrt.mydiary.data.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

@Entity(tableName = "Notes")
data class MyNote(@ColumnInfo(name = "title") var title: String,
                  @ColumnInfo(name = "content") var content: String,
                  @ColumnInfo(name = "time") var time: Long) : Serializable {

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

}