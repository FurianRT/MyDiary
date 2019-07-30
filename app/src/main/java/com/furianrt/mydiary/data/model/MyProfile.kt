/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import org.joda.time.DateTime

@Parcelize
@Entity(tableName = MyProfile.TABLE_NAME)
data class MyProfile(
        @PrimaryKey(autoGenerate = false) @ColumnInfo(name = FIELD_ID) var id: String = "",
        @ColumnInfo(name = FIELD_EMAIL) var email: String = "",
        @ColumnInfo(name = FIELD_PHOTO_URL) var photoUrl: String? = null,
        @ColumnInfo(name = FIELD_CREATION_TIME) var creationTime: Long = DateTime.now().millis,
        @ColumnInfo(name = FIELD_LAST_SYNC_TIME) var lastSyncTime: Long? = null
) : Parcelable {

    companion object {
        const val TABLE_NAME = "Profile"
        const val FIELD_ID = "id"
        const val FIELD_EMAIL = "email"
        const val FIELD_CREATION_TIME = "creation_time"
        const val FIELD_LAST_SYNC_TIME = "last_sync_time"
        const val FIELD_PHOTO_URL = "photo_url"
    }
}