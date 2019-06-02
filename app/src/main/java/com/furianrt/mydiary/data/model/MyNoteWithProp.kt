package com.furianrt.mydiary.data.model

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MyNoteWithProp(
        @Embedded var note: MyNote = MyNote(),
        @Embedded var mood: MyMood? = null,
        @Embedded var category: MyCategory? = null,
        @Embedded var appearance: MyNoteAppearance? = null
) : Parcelable {

    @IgnoredOnParcel
    @Relation(
            entity = NoteTag::class,
            parentColumn = MyNote.FIELD_ID,
            entityColumn = NoteTag.FIELD_NOTE_ID
    )
    var tags: List<NoteTag> = emptyList()

    @IgnoredOnParcel
    @Relation(
            entity = NoteLocation::class,
            parentColumn = MyNote.FIELD_ID,
            entityColumn = NoteLocation.FIELD_NOTE_ID
    )
    var locations: List<NoteLocation> = emptyList()

    @IgnoredOnParcel
    @Relation(
            entity = MyImage::class,
            parentColumn = MyNote.FIELD_ID,
            entityColumn = MyImage.FIELD_ID_NOTE
    )
    var images: List<MyImage> = emptyList()
        get() = field.sortedWith(compareBy(MyImage::order, MyImage::addedTime))
}