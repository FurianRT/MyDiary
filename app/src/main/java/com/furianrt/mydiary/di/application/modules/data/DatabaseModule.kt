/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.di.application.modules.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.source.database.*
import com.furianrt.mydiary.data.entity.*
import com.furianrt.mydiary.di.application.component.AppScope
import com.furianrt.mydiary.di.application.modules.app.AppContext
import com.furianrt.mydiary.utils.generateUniqueId
import com.google.gson.Gson
import dagger.Module
import dagger.Provides

@Module
object DatabaseModule {

    private const val DATABASE_NAME = "Notes.db"

    @JvmStatic
    @Provides
    @AppScope
    fun provideRoomCallback(@AppContext context: Context) =
            object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    createDefaultProperties(db, context)
                }
            }

    @JvmStatic
    @Provides
    @AppScope
    fun provideNoteDatabase(
            @AppContext context: Context,
            callback: RoomDatabase.Callback
    ): NoteDatabase {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE NoteLocation")
                database.execSQL("DROP TABLE Locations")

                database.execSQL("CREATE TABLE Locations (" +
                        "id_location TEXT NOT NULL PRIMARY KEY, " +
                        "name_location TEXT NOT NULL, " +
                        "lat REAL NOT NULL, " +
                        "lon REAL NOT NULL, " +
                        "location_sync_with TEXT NOT NULL, " +
                        "is_location_deleted INTEGER NOT NULL)")

                database.execSQL("CREATE TABLE NoteLocation (" +
                        "id_note TEXT NOT NULL, " +
                        "id_location TEXT NOT NULL, " +
                        "notelocation_sync_with TEXT NOT NULL, " +
                        "is_notelocation_deleted INTEGER NOT NULL, " +
                        "PRIMARY KEY (id_note, id_location))")

                database.execSQL("CREATE INDEX index_NoteLocation_id_note ON NoteLocation (id_note)")
                database.execSQL("CREATE INDEX index_NoteLocation_id_location ON NoteLocation (id_location)")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE TextSpans (" +
                        "id_span TEXT NOT NULL, " +
                        "id_note TEXT NOT NULL, " +
                        "type INTEGER NOT NULL, " +
                        "start_index INTEGER NOT NULL, " +
                        "end_index INTEGER NOT NULL, " +
                        "color INTEGER, " +
                        "size REAL, " +
                        "span_sync_with TEXT NOT NULL, " +
                        "is_span_deleted INTEGER NOT NULL, " +
                        "PRIMARY KEY (id_span, id_note))")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("UPDATE Locations SET location_sync_with = '[]'")
                database.execSQL("UPDATE NoteLocation SET notelocation_sync_with = '[]'")
            }
        }

        return Room.databaseBuilder(context, NoteDatabase::class.java, DATABASE_NAME)
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                .addCallback(callback)
                .build()
    }

    @JvmStatic
    @Provides
    @AppScope
    fun provideMoodDao(database: NoteDatabase): MoodDao = database.moodDao()

    @JvmStatic
    @Provides
    @AppScope
    fun provideAppearanceDao(database: NoteDatabase): AppearanceDao = database.appearanceDao()

    @JvmStatic
    @Provides
    @AppScope
    fun provideCategoryDao(database: NoteDatabase): CategoryDao = database.categoryDao()

    @JvmStatic
    @Provides
    @AppScope
    fun provideForecastDao(database: NoteDatabase): ForecastDao = database.forecastDao()

    @JvmStatic
    @Provides
    @AppScope
    fun provideHeaderImageDao(database: NoteDatabase): HeaderImageDao = database.headerImageDao()

    @JvmStatic
    @Provides
    @AppScope
    fun provideImageDao(database: NoteDatabase): ImageDao = database.imageDao()

    @JvmStatic
    @Provides
    @AppScope
    fun provideLocationDao(database: NoteDatabase): LocationDao = database.locationDao()

    @JvmStatic
    @Provides
    @AppScope
    fun provideNoteDao(database: NoteDatabase): NoteDao = database.noteDao()

    @JvmStatic
    @Provides
    @AppScope
    fun provideNoteLocationDao(database: NoteDatabase): NoteLocationDao = database.noteLocationDao()

    @JvmStatic
    @Provides
    @AppScope
    fun provideNoteTagDao(database: NoteDatabase): NoteTagDao = database.noteTagDao()

    @JvmStatic
    @Provides
    @AppScope
    fun provideProfileDao(database: NoteDatabase): ProfileDao = database.profileDao()

    @JvmStatic
    @Provides
    @AppScope
    fun provideTagDao(database: NoteDatabase): TagDao = database.tagDao()

    @JvmStatic
    @Provides
    @AppScope
    fun provideSpanDao(database: NoteDatabase): SpanDao = database.spanDao()

    //todo убрать это отсюда, когда будут свои entity у каждого слоя
    private fun createDefaultProperties(db: SupportSQLiteDatabase, context: Context) {
        with(ContentValues()) {
            val moodNames = context.resources.getStringArray(R.array.moods)
            val moodIcons = arrayOf(
                    context.resources.getResourceEntryName(R.drawable.ic_mood_angry),
                    context.resources.getResourceEntryName(R.drawable.ic_mood_awful),
                    context.resources.getResourceEntryName(R.drawable.ic_mood_bad),
                    context.resources.getResourceEntryName(R.drawable.ic_mood_neutral),
                    context.resources.getResourceEntryName(R.drawable.ic_mood_good),
                    context.resources.getResourceEntryName(R.drawable.ic_mood_great)
            )
            for (i in moodNames.indices) {
                put(MyMood.FIELD_NAME, moodNames[i])
                put(MyMood.FIELD_ICON, moodIcons[i])
                db.insert(MyMood.TABLE_NAME, SQLiteDatabase.CONFLICT_IGNORE, this)
            }
            clear()
            val tagNames = context.resources.getStringArray(R.array.tags)
            for (i in tagNames.indices) {
                put(MyTag.FIELD_ID, "default_tag_$i")
                put(MyTag.FIELD_NAME, tagNames[i])
                put(MyTag.FIELD_SYNC_WITH, Gson().toJson(listOf(MyTag.DEFAULT_SYNC_EMAIL)))
                put(MyTag.FIELD_IS_DELETED, false)
                db.insert(MyTag.TABLE_NAME, SQLiteDatabase.CONFLICT_IGNORE, this)
            }
            clear()
            val categoryNames = context.resources.getStringArray(R.array.categories)
            val categoryColors = context.resources.getStringArray(R.array.default_category_colors)
            for (i in categoryNames.indices) {
                put(MyCategory.FIELD_ID, "default_category_$i")
                put(MyCategory.FIELD_NAME, categoryNames[i])
                put(MyCategory.FIELD_COLOR, Color.parseColor(categoryColors[i]))
                put(MyCategory.FIELD_SYNC_WITH, Gson().toJson(listOf(MyCategory.DEFAULT_SYNC_EMAIL)))
                put(MyCategory.FIELD_IS_DELETED, false)
                db.insert(MyCategory.TABLE_NAME, SQLiteDatabase.CONFLICT_IGNORE, this)
            }
            clear()
        }
    }
}