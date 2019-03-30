package com.furianrt.mydiary.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.furianrt.mydiary.data.model.MyLocation

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(location: MyLocation)

    @Query("UPDATE Locations SET is_location_deleted = 1, location_sync_with = '[]' WHERE name_location = :locationId")
    fun delete(locationId: String)
}