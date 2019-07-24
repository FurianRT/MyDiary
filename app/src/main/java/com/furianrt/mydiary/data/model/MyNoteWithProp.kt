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
                    && images.find { !it.isSync(email) } == null
                    && tags.find { !it.isSync(email) } == null
                    && locations.find { !it.isSync(email) } == null

}