/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.data.model

import androidx.room.Embedded
import androidx.room.Ignore

data class MyNoteWithProp(
        @Embedded var note: MyNote = MyNote(),
        @Embedded var mood: MyMood? = null,
        @Embedded var category: MyCategory? = null,
        @Embedded var appearance: MyNoteAppearance? = null
) {

    //@Relation использовать не получится, с ним притянутся "удаленные" строки через "удаленные"
    //строки связующих таблиц
    @Ignore var tags = emptyList<MyTag>()
    @Ignore var locations = emptyList<MyLocation>()
    @Ignore var images = emptyList<MyImage>()

    fun isSync(email: String): Boolean =
            note.isSync(email)
                    && images.all { it.isSync(email) }
                    && tags.all { it.isSync(email) }
                    && locations.all { it.isSync(email) }

}