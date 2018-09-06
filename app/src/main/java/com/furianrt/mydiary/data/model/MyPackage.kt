package com.furianrt.mydiary.data.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "Packages")
data class MyPackage(@ColumnInfo(name = "id_package") @PrimaryKey(autoGenerate = true) var id: Long,
        @ColumnInfo(name = "name") var name: String) {
}