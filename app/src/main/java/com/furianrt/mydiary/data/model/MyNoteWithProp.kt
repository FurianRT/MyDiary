package com.furianrt.mydiary.data.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parceler
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

    private companion object : Parceler<MyNoteWithProp> {
        override fun MyNoteWithProp.write(parcel: Parcel, flags: Int) {
            parcel.writeParcelable(note, flags)
            parcel.writeParcelable(mood, flags)
            parcel.writeParcelable(location, flags)
            parcel.writeParcelable(category, flags)
            parcel.writeParcelable(appearance, flags)
            parcel.writeStringList(tags)
            parcel.writeList(images)
        }

        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        override fun create(parcel: Parcel) = MyNoteWithProp(
                parcel.readParcelable(MyNote::class.java.classLoader),
                parcel.readParcelable(MyMood::class.java.classLoader),
                parcel.readParcelable(MyLocation::class.java.classLoader),
                parcel.readParcelable(MyCategory::class.java.classLoader),
                parcel.readParcelable(MyNoteAppearance::class.java.classLoader)
        ).apply {
            parcel.readStringList(tags)
            parcel.readList(images, MyImage::class.java.classLoader)
        }
    }
}