package com.furianrt.mydiary.data.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.furianrt.mydiary.data.model.*

@Database(entities =
[MyNote::class, MyPackage::class, MyTag::class, MyMood::class, MyLocation::class, NoteTag::class],
        version = 1, exportSchema = false)
@TypeConverters(RoomTypeConverter::class)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    abstract fun packageDao(): PackageDao

    abstract fun tagDao(): TagDao

    abstract fun moodDao(): MoodDao

    abstract fun locationDao(): LocationDao

    abstract fun noteTagDao(): NoteTagDao
}