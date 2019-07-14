package com.furianrt.mydiary.view.screens.main.adapter

import com.furianrt.mydiary.R

data class NoteListHeader(var time: Long) : NoteListItem() {

    override fun getType(): Int = R.layout.activity_main_list_header
}