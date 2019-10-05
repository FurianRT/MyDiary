/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.data.entity

import androidx.room.Embedded
import androidx.room.Ignore

data class MyNoteWithSpans(
        @Embedded var note: MyNote = MyNote(),
        //@Relation использовать не получится, с ним притянутся "удаленные" строки
        @Ignore var textSpans: List<MyTextSpan> = emptyList(),
        @Ignore var deletedTextSpans: List<MyTextSpan> = emptyList()
) {

    fun isSync(email: String): Boolean =
            note.isSync(email)
                    && textSpans.all { it.isSync(email) }
                    && deletedTextSpans.isEmpty()
}