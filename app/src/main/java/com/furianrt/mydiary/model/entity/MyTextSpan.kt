/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.model.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(
        tableName = MyTextSpan.TABLE_NAME,
        primaryKeys = [MyTextSpan.FIELD_ID, MyTextSpan.FIELD_NOTE_ID]
)
data class MyTextSpan(
        @ColumnInfo(name = FIELD_ID) var id: String = "",
        @ColumnInfo(name = FIELD_NOTE_ID) var noteId: String = "",
        @ColumnInfo(name = FIELD_TYPE) val type: Int = -1,
        @ColumnInfo(name = FIELD_START_INDEX) val startIndex: Int = -1,
        @ColumnInfo(name = FIELD_END_INDEX) val endIndex: Int = -1,
        @ColumnInfo(name = FIELD_COLOR) val color: Int? = null,
        @ColumnInfo(name = FIELD_SIZE) val size: Float? = null,
        @ColumnInfo(name = FIELD_SYNC_WITH) var syncWith: MutableList<String> = mutableListOf(),
        @ColumnInfo(name = FIELD_IS_DELETED) var isDeleted: Boolean = false
) : Parcelable {

    companion object {
        const val TABLE_NAME = "TextSpans"
        const val FIELD_ID = "id_span"
        const val FIELD_NOTE_ID = "id_note"
        const val FIELD_TYPE = "type"
        const val FIELD_START_INDEX = "start_index"
        const val FIELD_END_INDEX = "end_index"
        const val FIELD_COLOR = "color"
        const val FIELD_SIZE = "size"
        const val FIELD_SYNC_WITH = "span_sync_with"
        const val FIELD_IS_DELETED = "is_span_deleted"

        const val TYPE_BOLD_TEXT = 0
        const val TYPE_ITALIC_TEXT = 1
        const val TYPE_STRIKETHROUGH_TEXT = 2
        const val TYPE_BIG_TEXT = 3
        const val TYPE_TEXT_COLOR = 4
        const val TYPE_FILL_COLOR = 5
    }

    fun isSync(email: String) = syncWith.contains(email)
}