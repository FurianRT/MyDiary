package com.furianrt.mydiary.data.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation

data class MyNoteWithProp(
        @Embedded var note: MyNote,
        @Embedded var mood: MyMood? = null,
        @Embedded var location: MyLocation? = null,
        @Embedded var category: MyCategory? = null

) : Parcelable {

    @Ignore
    constructor(id: String) : this(MyNote(id, "", ""))

    @Relation(entity = NoteTag::class, parentColumn = "id_note", entityColumn = "id_note",
            projection = ["id_tag"])
    var tags: List<String> = ArrayList()

    @Relation(entity = MyImage::class, parentColumn = "id_note", entityColumn = "id_note")
    var images: List<MyImage> = ArrayList()
        get() = field.sortedBy { it.order }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    constructor(parcel: Parcel) : this(
            parcel.readParcelable(MyNote::class.java.classLoader),
            parcel.readParcelable(MyMood::class.java.classLoader),
            parcel.readParcelable(MyLocation::class.java.classLoader),
            parcel.readParcelable(MyCategory::class.java.classLoader)) {
        tags = parcel.createStringArrayList()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(note, flags)
        parcel.writeParcelable(mood, flags)
        parcel.writeParcelable(location, flags)
        parcel.writeParcelable(category, flags)
        parcel.writeStringList(tags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MyNoteWithProp> {
        override fun createFromParcel(parcel: Parcel): MyNoteWithProp {
            return MyNoteWithProp(parcel)
        }

        override fun newArray(size: Int): Array<MyNoteWithProp?> {
            return arrayOfNulls(size)
        }
    }
}