package com.furianrt.mydiary.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.furianrt.mydiary.data.model.MyProfile
import io.reactivex.Observable
import io.reactivex.Single

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

    @Query("SELECT COUNT(id) FROM Profile")
    fun getProfileCount(): Single<Int>
}