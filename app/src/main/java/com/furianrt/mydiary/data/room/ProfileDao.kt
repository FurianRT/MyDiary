package com.furianrt.mydiary.data.room

import androidx.room.*
import com.furianrt.mydiary.data.model.MyProfile
import io.reactivex.Observable

@Dao
abstract class ProfileDao {

    @Insert
    abstract fun insert(profile: MyProfile)

    @Transaction
    open fun save(profile: MyProfile) {
        delete()
        insert(profile)
    }

    @Update
    abstract fun update(profile: MyProfile)

    @Query("DELETE FROM Profile")
    abstract fun delete()

    @Query("SELECT * FROM Profile")
    abstract fun getProfile(): Observable<MyProfile>

}