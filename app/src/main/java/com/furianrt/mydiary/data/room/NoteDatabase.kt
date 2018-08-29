package com.furianrt.mydiary.data.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.furianrt.mydiary.data.model.MyNote
import com.furianrt.mydiary.data.model.MyPackage
import com.furianrt.mydiary.data.model.MyTag

@Database(entities = [MyNote::class, MyPackage::class, MyTag::class], version = 1,
        exportSchema = false)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun myNoteDao(): NoteDao

    abstract fun myPackageDao(): PackageDao

    abstract fun myTagDao(): TagDao
}