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
        @PrimaryKey(autoGenerate = false) @ColumnInfo(name = FIELD_EMAIL) var email: String = "",
        @ColumnInfo(name = FIELD_PASSWORD_HASH) var passwordHash: String = "",
        @ColumnInfo(name = FIELD_PHOTO_URL) var photoUrl: String = "",
        @ColumnInfo(name = FIELD_REGISTRATION_TIME) var registrationTime: Long = DateTime.now().millis,
        @ColumnInfo(name = FIELD_LAST_SYNC_TIME) var lastSyncTime: Long = 0L,
        @ColumnInfo(name = FIELD_HAS_PREMIUM) var hasPremium: Boolean = false
) : Parcelable {

    companion object {
        const val TABLE_NAME = "Profile"
        const val FIELD_EMAIL = "email"
        const val FIELD_PASSWORD_HASH = "password_hash"
        const val FIELD_REGISTRATION_TIME = "registration_time"
        const val FIELD_LAST_SYNC_TIME = "last_sync_time"
        const val FIELD_PHOTO_URL = "photo_url"
        const val FIELD_HAS_PREMIUM = "has_premium"
    }
}