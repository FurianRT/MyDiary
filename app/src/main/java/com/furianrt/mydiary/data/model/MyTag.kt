package com.furianrt.mydiary.data.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Tags")
data class MyTag(@ColumnInfo(name = "name_tag") var name: String) : Serializable {

    @ColumnInfo(name = "id_tag")
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @Ignore
    var isChecked: Boolean = false
}