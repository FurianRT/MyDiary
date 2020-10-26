/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.model.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = MyPurchase.TABLE_NAME)
data class MyPurchase(
        @ColumnInfo(name = FIELD_PURCHASE_TOKEN) @PrimaryKey(autoGenerate = false) var purchaseToken: String = "",
        @ColumnInfo(name = FIELD_ORDER_ID) var orderId: String = "",
        @ColumnInfo(name = FIELD_SKU) var sku: String = "",
        @ColumnInfo(name = FIELD_SIGNATURE) var signature: String = "",
        @ColumnInfo(name = FIELD_ORIGINAL_JSON) var originalJson: String = "",
        @ColumnInfo(name = FIELD_IS_ACKNOWLEDGED) var isAcknowledged: Boolean = false
) : Parcelable {

    companion object {
        const val TABLE_NAME = "Purchases"
        const val FIELD_PURCHASE_TOKEN = "purchase_token"
        const val FIELD_ORDER_ID = "order_id"
        const val FIELD_SKU = "sku"
        const val FIELD_SIGNATURE = "signature"
        const val FIELD_ORIGINAL_JSON = "original_json"
        const val FIELD_IS_ACKNOWLEDGED = "is_acknowledged"
    }
}