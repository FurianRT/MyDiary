package com.furianrt.mydiary.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import com.furianrt.mydiary.data.model.MyLocation

@Dao
interface LocationDao {

    @Insert
    fun insert(location: MyLocation): Long

    @Delete
    fun delete(location: MyLocation)
}