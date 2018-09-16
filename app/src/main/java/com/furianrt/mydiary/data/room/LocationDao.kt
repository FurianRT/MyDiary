package com.furianrt.mydiary.data.room

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import com.furianrt.mydiary.data.model.MyLocation

@Dao
interface LocationDao {

    @Insert
    fun insert(location: MyLocation): Long

    @Delete
    fun delete(location: MyLocation)
}