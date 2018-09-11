package com.furianrt.mydiary.data.model

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import java.io.Serializable

class MyNoteWithProp(@Embedded var note: MyNote): Serializable {

    @Relation(entity = NoteTag::class, parentColumn = "id_note", entityColumn = "id_note",
            projection = ["id_tag", "name_tag"])
    var tags: List<MyTag>? = null

    //@Relation(entity = MyMood::class, parentColumn = "id_mood", entityColumn = "id_mood")
    @Embedded
    var mood: MyMood? = null

    //@Relation(entity = MyLocation::class, parentColumn = "id_location", entityColumn = "id_location")
    @Embedded
    var location: MyLocation? = null

    //@Relation(entity = MyCategory::class, parentColumn = "id_category", entityColumn = "id_category")
    @Embedded
    var category: MyCategory? = null
}