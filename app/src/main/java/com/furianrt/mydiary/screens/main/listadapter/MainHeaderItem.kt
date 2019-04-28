package com.furianrt.mydiary.screens.main.listadapter

import com.furianrt.mydiary.R

data class MainHeaderItem(var time: Long) : MainListItem() {

    override fun getType(): Int = R.layout.activity_main_list_header
}