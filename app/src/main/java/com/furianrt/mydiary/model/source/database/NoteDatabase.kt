/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.model.source.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.furianrt.mydiary.model.entity.*
import com.furianrt.mydiary.model.source.database.dao.*

@Database(
        entities = [MyNote::class, MyCategory::class, MyTag::class, MyLocation::class, MyMood::class,
            NoteTag::class, MyImage::class, MyHeaderImage::class, MyNoteAppearance::class,
            MyProfile::class, MyForecast::class, NoteLocation::class, MyTextSpan::class],
        version = 5,
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
    abstract fun forecastDao(): ForecastDao
    abstract fun noteLocationDao(): NoteLocationDao
    abstract fun spanDao(): SpanDao
}