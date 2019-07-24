package com.furianrt.mydiary.view.screens.main.adapter

import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyNoteWithProp

data class NoteListContent(val note: MyNoteWithProp) : NoteListItem() {

    override fun getType(): Int = R.layout.activity_main_list_content
}