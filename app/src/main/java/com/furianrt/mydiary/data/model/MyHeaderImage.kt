package com.furianrt.mydiary.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "HeaderImages")
data class MyHeaderImage(
        @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "name") var name: String,
        @ColumnInfo(name = "url") var url: String
) {

}