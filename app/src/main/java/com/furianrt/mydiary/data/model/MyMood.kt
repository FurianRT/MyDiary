package com.furianrt.mydiary.data.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "Moods")
data class MyMood(@ColumnInfo(name = "name") @PrimaryKey(autoGenerate = false) var name: String) {

}