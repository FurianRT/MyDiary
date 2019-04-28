package com.furianrt.mydiary.data.database

import androidx.room.*
import com.furianrt.mydiary.data.model.MyProfile
import io.reactivex.Observable

@Dao
interface ProfileDao {

    @Insert
    fun insert(profile: MyProfile)

    @Update
    fun update(profile: MyProfile)

    @Query("DELETE FROM Profile")
    fun clearProfile()

    @Query("SELECT * FROM Profile")
    fun getProfile(): Observable<MyProfile>
}