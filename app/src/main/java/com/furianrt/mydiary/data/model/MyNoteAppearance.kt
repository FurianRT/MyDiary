package com.furianrt.mydiary.data.model

import android.graphics.Color
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
        @ColumnInfo(name = "background_color") var background: Int = Color.WHITE,
        @ColumnInfo(name = "text_background_color") var textBackground: Int = Color.WHITE,
        @ColumnInfo(name = "text_color") var textColor: Int = Color.BLACK,
        @ColumnInfo(name = "text_size") var textSize: Int = 16
) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString() ?: "",
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(appearanceId)
        parcel.writeInt(background)
        parcel.writeInt(textBackground)
        parcel.writeInt(textColor)
        parcel.writeInt(textSize)
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