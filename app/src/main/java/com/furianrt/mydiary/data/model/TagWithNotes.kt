package com.furianrt.mydiary.data.model

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation

data class TagWithNotes(@Embedded val tag: MyTag) {

    @Relation(parentColumn = "name", entityColumn = "tag_name", entity = NoteTag::class,
            projection = ["id_note"])
    lateinit var noteIdList: List<Long>
}