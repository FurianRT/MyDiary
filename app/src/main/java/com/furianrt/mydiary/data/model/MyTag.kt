package com.furianrt.mydiary.data.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Tags")
 data class MyTag(
        @ColumnInfo(name = "name") @PrimaryKey(autoGenerate = false) var name: String
) : Serializable {

    @Ignore var isChecked: Boolean = false

    constructor(name: String, isChecked: Boolean) : this(name) {
        this.isChecked = isChecked
    }
}