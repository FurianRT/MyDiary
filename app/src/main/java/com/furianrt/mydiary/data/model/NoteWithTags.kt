package com.furianrt.mydiary.data.model

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation

data class NoteWithTags(@Embedded val note: MyNote) {

    @Relation(parentColumn = "id", entityColumn = "id_note", entity = NoteTag::class,
            projection = ["tag_name"])
    lateinit var tagNameList: List<String>
}