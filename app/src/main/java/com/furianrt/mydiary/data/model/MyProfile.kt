package com.furianrt.mydiary.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Profile")
data class MyProfile(
        @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "email") var email: String = "",
        @ColumnInfo(name = "password_hash") var passwordHash: String = "",
        @ColumnInfo(name = "image_url") var imageUrl: String = "",
        @ColumnInfo(name = "has_premium") var hasPremium: Boolean = false)