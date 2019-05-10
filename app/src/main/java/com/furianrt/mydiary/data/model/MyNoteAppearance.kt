package com.furianrt.mydiary.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/*@Entity(
        tableName = MyNoteAppearance.TABLE_NAME,
        foreignKeys = [
            ForeignKey(
                    entity = MyNote::class,
                    parentColumns = [MyNote.FIELD_ID],
                    childColumns = [MyNoteAppearance.FIELD_ID],
                    onDelete = ForeignKey.CASCADE
            )
        ]
)*/
@Parcelize
@Entity(tableName = MyNoteAppearance.TABLE_NAME)
data class MyNoteAppearance(
        @ColumnInfo(name = FIELD_ID) @PrimaryKey(autoGenerate = false) var appearanceId: String = "",
        @ColumnInfo(name = FIELD_BACKGROUND_COLOR) var background: Int? = null,
        @ColumnInfo(name = FIELD_TEXT_BACKGROUND_COLOR) var textBackground: Int? = null,
        @ColumnInfo(name = FIELD_TEXT_COLOR) var textColor: Int? = null,
        @ColumnInfo(name = FIELD_SURFACE_TEXT_COLOR) var surfaceTextColor: Int? = null,
        @ColumnInfo(name = FIELD_TEXT_SIZE) var textSize: Int? = null,
        @ColumnInfo(name = FIELD_SYNC_WITH) var syncWith: MutableList<String> = mutableListOf(),
        @ColumnInfo(name = FIELD_IS_DELETED) var isDeleted: Boolean = false
) : Parcelable {

    companion object {
        const val TABLE_NAME = "NoteAppearances"
        const val FIELD_ID = "id_appearance"
        const val FIELD_BACKGROUND_COLOR = "background_color"
        const val FIELD_TEXT_BACKGROUND_COLOR = "text_background_color"
        const val FIELD_TEXT_COLOR = "text_color"
        const val FIELD_SURFACE_TEXT_COLOR = "surface_text_color"
        const val FIELD_TEXT_SIZE = "text_size"
        const val FIELD_SYNC_WITH = "appearance_sync_with"
        const val FIELD_IS_DELETED = "is_appearance_deleted"
    }

    fun isSync(email: String) = syncWith.contains(email)
}