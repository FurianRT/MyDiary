package com.furianrt.mydiary.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.furianrt.mydiary.data.model.MyLocation

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(location: MyLocation)

    @Delete
    fun delete(location: MyLocation)
}