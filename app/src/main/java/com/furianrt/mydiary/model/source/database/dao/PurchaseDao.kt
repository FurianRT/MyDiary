/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.model.source.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.furianrt.mydiary.model.entity.MyPurchase
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface PurchaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(purchase: MyPurchase): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(purchases: List<MyPurchase>): Completable

    @Query("SELECT * FROM ${MyPurchase.TABLE_NAME}")
    fun getPurchases(): Flowable<List<MyPurchase>>

    @Query("SELECT * FROM ${MyPurchase.TABLE_NAME} WHERE ${MyPurchase.FIELD_SKU} = :sku")
    fun getPurchase(sku: String): Flowable<List<MyPurchase>>
}