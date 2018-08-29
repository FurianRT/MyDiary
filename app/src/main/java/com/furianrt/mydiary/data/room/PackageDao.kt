package com.furianrt.mydiary.data.room

import android.arch.persistence.room.*
import com.furianrt.mydiary.data.model.MyPackage
import io.reactivex.Flowable

@Dao
interface PackageDao {

    @Insert
    fun insert(pack: MyPackage)

    @Update
    fun update(pack: MyPackage)

    @Delete
    fun delete(pack: MyPackage)

    @Query("SELECT * FROM Packages")
    fun getAllNotes(): Flowable<List<MyPackage>>
}