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

import androidx.room.Ignore

data class MyNoteWithImages(
        @Ignore var note: MyNote = MyNote(),
        @Ignore var appearance: MyNoteAppearance? = null,
        @Ignore var images: List<MyImage> = emptyList(),
        @Ignore var deletedImages: List<MyImage> = emptyList()
) {

    fun isSync(email: String): Boolean =
            note.isSync(email)
                    && (appearance?.isSync(email) ?: true)
                    && images.all { it.isSync(email) }
                    && deletedImages.all { it.isSync(email) }
}