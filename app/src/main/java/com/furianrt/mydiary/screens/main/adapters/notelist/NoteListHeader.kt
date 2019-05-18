package com.furianrt.mydiary.screens.main.adapters.notelist

import com.furianrt.mydiary.R

data class NoteListHeader(var time: Long) : NoteListItem() {

    override fun getType(): Int = R.layout.activity_main_list_header
}