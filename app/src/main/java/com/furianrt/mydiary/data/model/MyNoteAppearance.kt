package com.furianrt.mydiary.data.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
        tableName = "NoteAppearances",
        foreignKeys = [
            ForeignKey(
                    entity = MyNote::class,
                    parentColumns = ["id_note"],
                    childColumns = ["id_appearance"],
                    onDelete = ForeignKey.CASCADE
            )
        ]
)
data class MyNoteAppearance(
        @ColumnInfo(name = "id_appearance") @PrimaryKey(autoGenerate = false) var appearanceId: String,
        @ColumnInfo(name = "background_color") var background: Int? = null,
        @ColumnInfo(name = "text_background_color") var textBackground: Int? = null,
        @ColumnInfo(name = "text_color") var textColor: Int? = null,
        @ColumnInfo(name = "text_size") var textSize: Int? = null
) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString() ?: "",
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(appearanceId)
        parcel.writeValue(background)
        parcel.writeValue(textBackground)
        parcel.writeValue(textColor)
        parcel.writeValue(textSize)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MyNoteAppearance> {
        override fun createFromParcel(parcel: Parcel): MyNoteAppearance {
            return MyNoteAppearance(parcel)
        }

        override fun newArray(size: Int): Array<MyNoteAppearance?> {
            return arrayOfNulls(size)
        }
    }
}