package com.furianrt.mydiary.di.application.modules.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.database.NoteDatabase
import com.furianrt.mydiary.data.model.*
import com.furianrt.mydiary.data.storage.StorageHelper
import com.furianrt.mydiary.di.application.component.AppScope
import com.furianrt.mydiary.di.application.modules.app.AppContext
import com.furianrt.mydiary.utils.generateUniqueId
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import org.joda.time.DateTime

@Module
class DatabaseModule {

    companion object {
        private const val DATABASE_NAME = "Notes.db"
    }

    @Provides
    @AppScope
    fun provideRoomCallback(@AppContext context: Context, storage: StorageHelper) =
            object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    createDefaultProperties(db, context)
                    createTutorialNote(db, context, storage)
                }
            }

    @Provides
    @AppScope
    fun provideNoteDatabase(
            @AppContext context: Context,
            callback: RoomDatabase.Callback
    ): NoteDatabase {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.query("DROP TABLE IF EXISTS NoteLocation")
                database.query("DROP TABLE IF EXISTS Locations")

                database.query("CREATE TABLE Locations (" +
                        "id_location TEXT NOT NULL PRIMARY KEY, " +
                        "name_location TEXT NOT NULL, " +
                        "lat REAL NOT NULL, " +
                        "lon REAL NOT NULL, " +
                        "location_sync_with TEXT NOT NULL, " +
                        "is_location_deleted INTEGER NOT NULL)")

                database.query("CREATE TABLE NoteLocation (" +
                        "id_note TEXT NOT NULL, " +
                        "id_location TEXT NOT NULL, " +
                        "notelocation_sync_with TEXT NOT NULL, " +
                        "is_notelocation_deleted INTEGER NOT NULL, " +
                        "PRIMARY KEY (id_note, id_location)")
            }
        }

        return Room.databaseBuilder(context, NoteDatabase::class.java, DATABASE_NAME)
                .addMigrations(MIGRATION_1_2)
                .addCallback(callback)
                .build()

    }

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
            for (i in 0 until moodNames.size) {
                put(MyMood.FIELD_NAME, moodNames[i])
                put(MyMood.FIELD_ICON, moodIcons[i])
                db.insert(MyMood.TABLE_NAME, SQLiteDatabase.CONFLICT_IGNORE, this)
            }
            clear()
            val tagNames = context.resources.getStringArray(R.array.tags)
            for (i in 0 until tagNames.size) {
                put(MyTag.FIELD_ID, "default_tag_$i")
                put(MyTag.FIELD_NAME, tagNames[i])
                put(MyTag.FIELD_SYNC_WITH, Gson().toJson(listOf(MyTag.DEFAULT_SYNC_EMAIL)))
                put(MyTag.FIELD_IS_DELETED, false)
                db.insert(MyTag.TABLE_NAME, SQLiteDatabase.CONFLICT_IGNORE, this)
            }
            clear()
            val categoryNames = context.resources.getStringArray(R.array.categories)
            val categoryColors = context.resources.getStringArray(R.array.default_category_colors)
            for (i in 0 until categoryNames.size) {
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

    private fun createTutorialNote(db: SupportSQLiteDatabase, context: Context, storage: StorageHelper) {
        val note = MyNote(
                id = generateUniqueId(),
                title = context.getString(R.string.tutorial_note_title),
                content = context.getString(R.string.tutorial_note_content),
                time = DateTime.now().millis,
                moodId = context.resources.getStringArray(R.array.moods).size,
                categoryId = "default_category_1",
                creationTime = DateTime.now().millis
        )

        val noteAppearance = MyNoteAppearance(appearanceId = note.id)

        val noteTag1 = NoteTag(noteId = note.id, tagId = "default_tag_0")
        val noteTag2 = NoteTag(noteId = note.id, tagId = "default_tag_1")

        val imageFile = storage.copyBitmapToStorage(
                BitmapFactory.decodeResource(context.resources, R.drawable.tutorial_header_image),
                generateUniqueId()
        )

        val image = MyImage(
                name = imageFile.name,
                uri = imageFile.toURI().toString(),
                noteId = note.id,
                addedTime = DateTime.now().millis,
                editedTime = DateTime.now().millis
        )

        with(ContentValues()) {
            put(MyNote.FIELD_ID, note.id)
            put(MyNote.FIELD_TITLE, note.title)
            put(MyNote.FIELD_CONTENT, note.content)
            put(MyNote.FIELD_TIME, note.time)
            put(MyNote.FIELD_MOOD, note.moodId)
            put(MyNote.FIELD_CATEGORY, note.categoryId)
            put(MyNote.FIELD_CREATION_TIME, note.creationTime)
            put(MyNote.FIELD_SYNC_WITH, Gson().toJson(note.syncWith))
            put(MyNote.FIELD_IS_DELETED, note.isDeleted)
            db.insert(MyNote.TABLE_NAME, SQLiteDatabase.CONFLICT_IGNORE, this)
            clear()

            put(MyNoteAppearance.FIELD_ID, noteAppearance.appearanceId)
            put(MyNoteAppearance.FIELD_ID, noteAppearance.appearanceId)
            put(MyNoteAppearance.FIELD_SYNC_WITH, Gson().toJson(noteAppearance.syncWith))
            put(MyNoteAppearance.FIELD_IS_DELETED, noteAppearance.isDeleted)
            db.insert(MyNoteAppearance.TABLE_NAME, SQLiteDatabase.CONFLICT_IGNORE, this)
            clear()

            put(NoteTag.FIELD_NOTE_ID, noteTag1.noteId)
            put(NoteTag.FIELD_TAG_ID, noteTag1.tagId)
            put(NoteTag.FIELD_SYNC_WITH, Gson().toJson(noteTag1.syncWith))
            put(NoteTag.FIELD_IS_DELETED, noteTag1.isDeleted)
            db.insert(NoteTag.TABLE_NAME, SQLiteDatabase.CONFLICT_IGNORE, this)

            put(NoteTag.FIELD_TAG_ID, noteTag2.tagId)
            db.insert(NoteTag.TABLE_NAME, SQLiteDatabase.CONFLICT_IGNORE, this)
            clear()

            put(MyImage.FIELD_NAME, image.name)
            put(MyImage.FIELD_URI, image.uri)
            put(MyImage.FIELD_ID_NOTE, image.noteId)
            put(MyImage.FIELD_ADDED_TIME, image.addedTime)
            put(MyImage.FIELD_EDITED_TIME, image.editedTime)
            put(MyImage.FIELD_ORDER, image.order)
            put(MyImage.FIELD_SYNC_WITH, Gson().toJson(image.syncWith))
            put(MyImage.FIELD_FILE_SYNC_WITH, Gson().toJson(image.fileSyncWith))
            put(MyImage.FIELD_IS_DELETED, image.isDeleted)
            db.insert(MyImage.TABLE_NAME, SQLiteDatabase.CONFLICT_IGNORE, this)
            clear()
        }
    }
}