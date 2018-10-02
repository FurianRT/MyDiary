package com.furianrt.mydiary.data.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import java.util.*
import kotlin.collections.ArrayList

data class MyNoteWithProp(
        @Embedded var note: MyNote,
        @Embedded var location: MyLocation?
) : Parcelable {

    @Ignore
    constructor(id: String) : this(MyNote(id, "", "", Date().time), null)

    @Relation(entity = NoteTag::class, parentColumn = "id_note", entityColumn = "id_note",
            projection = ["id_tag"])
    var tags: List<Long> = ArrayList()

    @Relation(entity = MyImage::class, parentColumn = "id_note", entityColumn = "id_note")
    var images: List<MyImage> = ArrayList()
        get() = field.sortedBy { it.order }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    constructor(parcel: Parcel) : this(
            parcel.readParcelable(MyNote::class.java.classLoader),
            parcel.readParcelable(MyLocation::class.java.classLoader)) {

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(note, flags)
        parcel.writeParcelable(location, flags)
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