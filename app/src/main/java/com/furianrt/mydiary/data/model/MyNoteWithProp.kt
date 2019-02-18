package com.furianrt.mydiary.data.model

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MyNoteWithProp(
        @Embedded var note: MyNote,
        @Embedded var mood: MyMood? = null,
        @Embedded var location: MyLocation? = null,
        @Embedded var category: MyCategory? = null,
        @Embedded var appearance: MyNoteAppearance? = null
) : Parcelable {

    @Ignore
    constructor(id: String) : this(MyNote(id, "", ""))

    @IgnoredOnParcel
    @Relation(entity = NoteTag::class, parentColumn = "id_note", entityColumn = "id_note",
            projection = ["id_tag"])
    var tags: List<String> = arrayListOf()

    @IgnoredOnParcel
    @Relation(entity = MyImage::class, parentColumn = "id_note", entityColumn = "id_note")
    var images: List<MyImage> = arrayListOf()
}