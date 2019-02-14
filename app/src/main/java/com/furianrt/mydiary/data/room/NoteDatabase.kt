package com.furianrt.mydiary.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.furianrt.mydiary.data.model.*

@Database(
        entities = [MyNote::class, MyCategory::class, MyTag::class, MyLocation::class, MyMood::class,
            NoteTag::class, MyImage::class, MyHeaderImage::class, MyNoteAppearance::class, MyProfile::class],
        version = 1,
        exportSchema = false
)
@TypeConverters(RoomTypeConverter::class)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao
    abstract fun categoryDao(): CategoryDao
    abstract fun tagDao(): TagDao
    abstract fun locationDao(): LocationDao
    abstract fun noteTagDao(): NoteTagDao
    abstract fun imageDao(): ImageDao
    abstract fun headerImageDao(): HeaderImageDao
    abstract fun moodDao(): MoodDao
    abstract fun appearanceDao(): AppearanceDao
    abstract fun profileDao(): ProfileDao
}