package com.furianrt.mydiary.data.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Categories")
data class MyCategory(@ColumnInfo(name = "id_category") @PrimaryKey(autoGenerate = true) var id: Long,
                      @ColumnInfo(name = "name_category") var name: String): Serializable {
}